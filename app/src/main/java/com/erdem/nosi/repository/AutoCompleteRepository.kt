package com.erdem.nosi.repository

import com.erdem.nosi.Network.AutoCompleteApi
import com.erdem.nosi.data.AutoCompleteResponse

class AutoCompleteRepository(private val api: AutoCompleteApi) {
    suspend fun getSuggestions(query: String): List<AutoCompleteResponse> {
        return api.retrofitService.GetSuggestions(query)
    }
}