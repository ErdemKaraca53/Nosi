package com.erdem.nosi.request

import com.erdem.nosi.data.GeminiRequest
import com.erdem.nosi.data.GeminiResponse

class GeminiApiRepository(private val api: ApiInterface) {

    suspend fun getData(request: GeminiRequest): GeminiResponse {

        return api.generateContent(
            model = "gemini-2.5-flash",
            request = request
        )

    }

}