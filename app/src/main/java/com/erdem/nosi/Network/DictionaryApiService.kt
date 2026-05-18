package com.erdem.nosi.Network

import com.erdem.nosi.data.ApiResponse
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

private const val BASE_URL =
    "https://api.dictionaryapi.dev/api/v2/"

// Kotlinx Serialization için özelleştirilmiş yapılandırma (konfigürasyon) nesnesi.
// Bu nesne, JSON ayrıştırma (parsing) kurallarını belirler ve uygulamanın çökmesini önler.
val jsonConfig = Json {
    // API'den gelen JSON içinde olup, bizim Kotlin veri sınıfımızda (Data Class)
    // tanımlanmayan alanları (key) görmezden gelir. True yapılmazsa 'UnknownKeyException' hatası verir.
    ignoreUnknownKeys = true

    // API'den geçersiz veya eksik bir değer geldiğinde (örneğin null veya yanlış veri tipi),
    // uygulamanın çökmesi yerine veri sınıfında tanımlanan varsayılan değerlerin (default values) kullanılmasını zorlar.
    coerceInputValues = true
}

// HTTP istekleri ve yanıtları için veri tipini belirleyen nesne.
// Sunucuya "ben JSON formatında veri gönderiyorum/bekliyorum" bilgisini iletmek için kullanılır.
val contentType = "application/json".toMediaType()

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(jsonConfig.asConverterFactory(contentType))
    .build()

interface DictionaryApiService{
    @GET("entries/en/{word}")
    suspend fun getWordDefinition(
        @Path("word") word: String
    ): List<ApiResponse>
}

object DictionaryApi {
    val retrofitService : DictionaryApiService by lazy {
        retrofit.create(DictionaryApiService::class.java)
    }
}