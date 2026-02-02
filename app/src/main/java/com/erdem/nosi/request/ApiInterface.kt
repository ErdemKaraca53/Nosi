package com.erdem.nosi.request

import com.erdem.nosi.data.GeminiRequest
import com.erdem.nosi.data.GeminiResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiInterface {

    //@Body ile payload otomatik olarak jsona çevirilir
    @POST("models/{model}:generateContent")
    suspend fun generateContent(
        @Path("model") model: String,
        @Body request: GeminiRequest
    ): GeminiResponse

}