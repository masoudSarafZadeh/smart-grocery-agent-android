package com.example.productlistview.data.remote


import com.example.productlistview.data.model.request.*
import com.example.productlistview.data.model.response.InternetProducts
import com.example.productlistview.ui.screens.products.AiStreamEvent
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import org.json.JSONObject

class LangServeRemoteDataSource(
    private val eventSourceFactory: EventSource.Factory,
    private val networkJson: Json,
    private val baseUrl: String
) {
    fun startAiStream(userQuery: String, chatThreadId: String): Flow<AiStreamEvent> = callbackFlow {
        val requestBodyData = LangServeRequest(
            input = AgentStateInput(messages = listOf(MessagePayload(type = "human", content = userQuery))),
            config = LangServeConfig(configurable = ConfigurableFields(threadId = chatThreadId))
        )
        val jsonString = networkJson.encodeToString(requestBodyData)

        val request = Request.Builder()
            .url("${baseUrl}shopping-agent/stream_events")
            .post(jsonString.toRequestBody("application/json".toMediaType()))
            .addHeader("Accept", "text/event-stream")
            .build()

        val listener = object : EventSourceListener() {
            override fun onEvent(eventSource: EventSource, id: String?, type: String?, data: String) {
                try {
                    val json = JSONObject(data)
                    val eventType = json.optString("event")
                    val name = json.optString("name")

                    when (eventType) {
                        "on_chain_start" -> {
                            if (name == "generate_query") trySend(AiStreamEvent.StatusUpdate("در حال تحلیل درخواست..."))
                            if (name == "run_query") trySend(AiStreamEvent.StatusUpdate("در حال بررسی موجودی انبار..."))
                        }
                        "on_chat_model_stream" -> {
                            val tags = json.optJSONArray("tags")?.toString() ?: ""
                            if (tags.contains("final_answer_stream")) {
                                val token = json.optJSONObject("data")
                                    ?.optJSONObject("chunk")
                                    ?.optString("content") ?: ""
                                trySend(AiStreamEvent.TokenReceived(token))
                            }
                        }
                        "on_chain_end" -> {
                            val output = json.optJSONObject("data")?.optJSONObject("output")
                            if (output != null && output.has("raw_db_data")) {
                                val rawDbString = output.optJSONObject("raw_db_data")?.toString()
                                if (rawDbString != null) {
                                    val dbData: Map<String, List<InternetProducts>> = networkJson.decodeFromString(rawDbString)
                                    trySend(AiStreamEvent.DataReceived(dbData))
                                }
                            }
                        }
                    }
                } catch (e: Exception) { /* Ignore minor parse errors */ }
            }

            override fun onClosed(eventSource: EventSource) {
                trySend(AiStreamEvent.Done)
                close()
            }

            override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
                trySend(AiStreamEvent.Error(t?.message ?: "خطا در اتصال به سرور"))
                close(t)
            }
        }

        val eventSource = eventSourceFactory.newEventSource(request, listener)
        awaitClose { eventSource.cancel() }
    }
}