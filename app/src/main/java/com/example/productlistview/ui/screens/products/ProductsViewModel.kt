package com.example.productlistview.ui.screens.products


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.productlistview.ProductsApplication
import com.example.productlistview.data.model.chat.ChatMessage
import com.example.productlistview.data.model.chat.InternetProductsItemState
import com.example.productlistview.data.model.response.InternetProducts
import com.example.productlistview.data.repository.ProductsPhotosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

sealed interface AiStreamEvent {
    data class StatusUpdate(val status: String) : AiStreamEvent
    data class TokenReceived(val token: String) : AiStreamEvent
    data class DataReceived(val rawDbData: Map<String, List<InternetProducts>>) : AiStreamEvent
    data class Error(val message: String) : AiStreamEvent
    object Done : AiStreamEvent
}
class ProductsViewModel(private val productsPhotosRepository: ProductsPhotosRepository) : ViewModel() {

    private val _internetUiState = MutableStateFlow(ProductsUiState())
    val internetUiState: StateFlow<ProductsUiState> = _internetUiState.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()
    private val chatSessionThreadId: String = UUID.randomUUID().toString()

    fun updateInternetCount(photoId: Int, newCount: Int) {
        _chatMessages.update { messages ->
            messages.map { msg ->
                if (msg.products.isNotEmpty()) {
                    val updatedMap = msg.products.mapValues { entry ->
                        entry.value.map { item ->
                            if (item.product.id == photoId) {
                                item.copy(count = newCount)
                            } else {
                                item
                            }
                        }
                    }
                    msg.copy(products = updatedMap)
                } else {
                    msg
                }
            }
        }
    }
    fun toggleInternetExpand(photoId: Int) {
        _chatMessages.update { messages ->
            messages.map { msg->
                if (msg.products.isNotEmpty()) {
                    val updatedMap = msg.products.mapValues { entry ->
                        entry.value.map { item ->
                            if (item.product.id == photoId) {
                                val newExpandState = !item.isExpanded
                                item.copy(
                                    isExpanded = newExpandState,
                                    count = if (newExpandState) 1 else 0
                                )
                            } else item
                        }
                    }
                    msg.copy(products = updatedMap)
                }else{
                    msg
                }
            }
        }
    }

    fun sendMessageToAi(userQuery: String) {
        if (userQuery.isBlank()) return
        
        viewModelScope.launch {
            val aiMessageId = UUID.randomUUID().toString()
            _chatMessages.update { current ->
                current + listOf(
                    ChatMessage(text = userQuery, isUser = true),
                    ChatMessage(id = aiMessageId, text = "", isUser = false, isStreaming = true, statusMessage = "در حال ارتباط...")
                )
            }

            _internetUiState.update { currentState ->
                currentState.copy(
                    isInitialLoading = true,
                    isLoadingNewMessage = false,
                    isError = false
                )
            }
            productsPhotosRepository.askAiAgentStream(userQuery, chatSessionThreadId)
                .collect { event ->
                    when (event) {
                        is AiStreamEvent.StatusUpdate -> {
                            updateAiMessage(aiMessageId) { it.copy(statusMessage = event.status) }
                        }
                        is AiStreamEvent.TokenReceived -> {
                            _internetUiState.update { currentState ->
                                currentState.copy(
                                    isInitialLoading = false,
                                    isLoadingNewMessage = false
                                )
                            }
                            updateAiMessage(aiMessageId) {
                                it.copy(text = it.text + event.token, statusMessage = null)
                            }
                        }
                        is AiStreamEvent.DataReceived -> {
                            val uiDbProductStates = event.rawDbData.mapValues { entry ->
                                entry.value.map { InternetProductsItemState(product = it) }
                            }
                            updateAiMessage(aiMessageId) {
                                it.copy(products = uiDbProductStates)
                            }
                            _internetUiState.update { currentState ->
                                currentState.copy(
                                    isInitialLoading = false,
                                    isError = false
                                )
                            }
                        }
                        is AiStreamEvent.Error -> {
                            updateAiMessage(aiMessageId) { it.copy(text = "خطا: ${event.message}", isStreaming = false, statusMessage = null) }
                            _internetUiState.update { it.copy(isError = true, isInitialLoading = false, isLoadingNewMessage = false) }
                        }
                        is AiStreamEvent.Done -> {
                            updateAiMessage(aiMessageId) { it.copy(isStreaming = false, statusMessage = null) }
                            _internetUiState.update { it.copy(isInitialLoading = false, isLoadingNewMessage = false) }
                        }
                    }
                }
        }
    }
    private fun updateAiMessage(messageId: String, update: (ChatMessage) -> ChatMessage) {
        _chatMessages.update { messages ->
            messages.map { msg ->
                if (msg.id == messageId) update(msg) else msg
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as ProductsApplication)
                val productsPhotosRepository = application.container.productsPhotosRepository
                ProductsViewModel(productsPhotosRepository = productsPhotosRepository)
            }
        }
    }
}
