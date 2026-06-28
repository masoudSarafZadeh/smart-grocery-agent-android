package com.example.productlistview.data.di

import com.example.productlistview.data.remote.LangServeRemoteDataSource
import com.example.productlistview.data.repository.NetworkProductsPhotosRepository
import com.example.productlistview.data.repository.ProductsPhotosRepository
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.sse.EventSource
import okhttp3.sse.EventSources
import java.util.concurrent.TimeUnit

interface AppContainer {
    val productsPhotosRepository: ProductsPhotosRepository

}

class DefaultAppContainer : AppContainer {

    private val networkJson = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val baseUrl = "https://masoudsarafzadeh-shopping-agent-backend.hf.space/"
    private val eventSourceFactory: EventSource.Factory = EventSources.createFactory(okHttpClient)

    private val langServeRemoteDataSource: LangServeRemoteDataSource by lazy {
        LangServeRemoteDataSource(
            eventSourceFactory = eventSourceFactory,
            networkJson = networkJson,
            baseUrl = baseUrl
        )
    }
    override val productsPhotosRepository: ProductsPhotosRepository by lazy {
        NetworkProductsPhotosRepository(
            remoteDataSource = langServeRemoteDataSource
        )
    }
}
