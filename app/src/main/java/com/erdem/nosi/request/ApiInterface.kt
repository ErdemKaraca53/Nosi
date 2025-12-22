package com.erdem.nosi.request

import GeminiResponse
import com.erdem.nosi.data.GeminiRequest
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiInterface {

    @POST
    suspend fun generateContent(
        @Url url: String,
        @Body request: GeminiRequest,
        @Query("key") apiKey: String
    ): GeminiResponse
}