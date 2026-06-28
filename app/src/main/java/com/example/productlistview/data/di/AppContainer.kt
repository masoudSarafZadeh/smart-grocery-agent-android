package com.example.productlistview.data.di

import com.example.productlistview.data.remote.LangServeRemoteDataSource
import com.example.productlistview.data.repository.NetworkProductsPhotosRepository
import com.example.productlistview.data.repository.ProductsPhotosRepository
import com.example.productlistview.data.remote.ProductsApiService
import retrofit2.Retrofit
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import io.github.jan.supabase.SupabaseClient
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import okhttp3.OkHttpClient
import okhttp3.sse.EventSource
import okhttp3.sse.EventSources
import java.util.concurrent.TimeUnit

interface AppContainer {
    val productsPhotosRepository: ProductsPhotosRepository
    val supabaseClient: SupabaseClient

}

class DefaultAppContainer : AppContainer {

    override val supabaseClient = createSupabaseClient(
        supabaseUrl = "https://opukeehrojxzniygycpw.supabase.co",
        supabaseKey = "sb_publishable_Ez13VCzuGOGbcnk9SRmZ6g_HfreKRUh"
    ) {
        install(Postgrest)
    }

    private val networkJson = Json {
        ignoreUnknownKeys = true
        isLenient = true // 🔥 Allows parsing unconventional strings and unquoted keys safely
        coerceInputValues = true // 🔥 Fallback to defaults if null matches primitives unexpectedly
    }
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()
    // Your new, permanent cloud streaming production endpoint
    private val baseUrl = "https://masoudsarafzadeh-shopping-agent-backend.hf.space/"

    private val eventSourceFactory: EventSource.Factory = EventSources.createFactory(okHttpClient)
    private val retrofit: Retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .addConverterFactory(networkJson.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseUrl)
        .build()
    private val retrofitService: ProductsApiService by lazy {
        retrofit.create(ProductsApiService::class.java)
    }
    /*override val productsPhotosRepository: ProductsPhotosRepository by lazy {
        NetworkProductsPhotosRepository(retrofitService)
    }*/
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