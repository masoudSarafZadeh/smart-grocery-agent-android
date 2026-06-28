package com.example.productlistview.data.model.chat

import com.example.productlistview.data.model.response.InternetProducts
import java.util.UUID

data class InternetProductsItemState(
    val product: InternetProducts,
    val count: Int = 0,
    val isExpanded: Boolean = false
)

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val isStreaming: Boolean = false,
    val statusMessage: String? = null,
    val products: Map<String, List<InternetProductsItemState>> = emptyMap()
)