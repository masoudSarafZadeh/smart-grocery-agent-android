package com.example.productlistview.data.remote

import com.example.productlistview.data.model.request.LangServeRequest
import com.example.productlistview.data.model.response.LangServeResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ProductsApiService {
    @POST("shopping-agent/stream_events")
    suspend fun getAnswer(@Body request: LangServeRequest): LangServeResponse
}