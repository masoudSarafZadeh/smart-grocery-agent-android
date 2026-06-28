package com.example.productlistview.data.repository


import com.example.productlistview.data.remote.LangServeRemoteDataSource
import com.example.productlistview.ui.screens.products.AiStreamEvent
import kotlinx.coroutines.flow.Flow


interface ProductsPhotosRepository {
    fun askAiAgentStream(userQuery: String, chatThreadId: String): Flow<AiStreamEvent>
}

class NetworkProductsPhotosRepository(
    private val remoteDataSource: LangServeRemoteDataSource
) : ProductsPhotosRepository {

    override fun askAiAgentStream(userQuery: String, chatThreadId: String): Flow<AiStreamEvent> {
        return remoteDataSource.startAiStream(userQuery, chatThreadId)
    }
}
