package com.erdem.nosi.Network

import com.erdem.nosi.data.DictionaryApiResponse
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

private const val BASE_URL =
    "https://api.dictionaryapi.dev/api/v2/"

interface DictionaryApiService{
    @GET("entries/en/{word}")
    suspend fun getWordDefinition(
        @Path("word") word: String
    ): List<DictionaryApiResponse>
}

object DictionaryApi {
    val retrofitService : DictionaryApiService by lazy {
        NetworkConfig
            .createRetrofit(BASE_URL)
            .create(DictionaryApiService::class.java)
    }
}