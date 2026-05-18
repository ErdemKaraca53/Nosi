package com.erdem.nosi.Network

import com.erdem.nosi.data.Response
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://api.datamuse.com/"

interface AutoCompleteApiService{

    //URL'de sorgu parametresi için bu gerekli
    @GET("sug")
    suspend fun GetSuggestions(
        @Query("s") word: String
    ): List<Response>
}

object AutoCompleteApi {
    val retrofitService : AutoCompleteApiService by lazy {
        NetworkConfig
            .createRetrofit(BASE_URL)
            .create(AutoCompleteApiService::class.java)
    }
}
