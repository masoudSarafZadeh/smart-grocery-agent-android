package com.example.productlistview.data.repository


import com.example.productlistview.data.remote.LangServeRemoteDataSource
import com.example.productlistview.ui.screens.products.AiStreamEvent
import kotlinx.coroutines.flow.Flow


interface ProductsPhotosRepository {
    //suspend fun askAiAgent(userQuery: String, chatThreadId: String): LangServeResponse // Added suspend and return type
    fun askAiAgentStream(userQuery: String, chatThreadId: String): Flow<AiStreamEvent>
}


/*class NetworkProductsPhotosRepository(
    private val productsApiService: ProductsApiService
) : ProductsPhotosRepository {

    override suspend fun askAiAgent(userQuery: String, chatThreadId: String): LangServeResponse {
        // Construct the expected LangServe input payload
        val requestBody = LangServeRequest(
            input = AgentStateInput(
                messages = listOf(
                    MessagePayload(type = "human", content = userQuery)
                )
            ),
            config = LangServeConfig(
                configurable = ConfigurableFields(threadId = chatThreadId)
            )
        )
        return productsApiService.getAnswer(requestBody)
    }
 }*/
class NetworkProductsPhotosRepository(
    private val remoteDataSource: LangServeRemoteDataSource
) : ProductsPhotosRepository {

    override fun askAiAgentStream(userQuery: String, chatThreadId: String): Flow<AiStreamEvent> {
        // The repository simply requests the stream from the network data source
        return remoteDataSource.startAiStream(userQuery, chatThreadId)
    }
}