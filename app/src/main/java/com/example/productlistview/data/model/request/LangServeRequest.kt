package com.example.productlistview.data.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LangServeRequest(
    val input: AgentStateInput,
    val config: LangServeConfig? = null
)

@Serializable
data class AgentStateInput(
    val messages: List<MessagePayload>
)

@Serializable
data class MessagePayload(
    val type: String,
    val content: String
)

@Serializable
data class LangServeConfig(
    val configurable: ConfigurableFields
)

@Serializable
data class ConfigurableFields(
    @SerialName("thread_id")
    val threadId: String
)
