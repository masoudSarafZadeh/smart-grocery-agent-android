package com.example.productlistview.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonPrimitive

@Serializable
data class InternetProducts(
    val id: Int,
    @SerialName(value = "product_name")
    val productName: String? = null,
    val brand: String? = null,
    val weight: Int? = null,
    val price: Int? = null,
    @SerialName(value = "price_after_off")
    val priceAfterOff: Int? = null,
    @SerialName(value = "off_percent")
    val offPercent: Int? = null,
    @SerialName(value = "llm_guide")
    val llmGuide: String? = null,
    val image: String?=null,
)

@Serializable
data class LangServeResponse(
    val output: OutputState
)

@Serializable
data class OutputState(
    val messages: List<ResponseMessage>,
    @SerialName("raw_db_data") val rawDbData: Map<String, List<InternetProducts>>? = null
)

@Serializable
data class ResponseMessage(
    val type: String,
    // We use JsonElement to capture both raw text and complex JSON tools components cleanly
    val content: JsonElement? = null
) {
    /**
     * Helper property to safely extract textual response text for Chat UI
     * without crashing on structural tool blocks.
     */
    val textContent: String
        get() = when {
            content == null -> ""
            content.toString().startsWith("[") || content.toString().startsWith("{") -> "" // Skip structural tool data
            else -> content.jsonPrimitive.content // Extract actual message content
        }
}