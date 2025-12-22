package com.erdem.nosi.request

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {

    private const val BASE_URL =
        "https://generativelanguage.googleapis.com/v1beta/models/"


    fun getInstance(): Retrofit {

        //log requests and responses
        val client = OkHttpClient()
        val interceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        val clientBuilder: OkHttpClient.Builder =
            client.newBuilder().addInterceptor(interceptor)
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .connectTimeout(30, TimeUnit.SECONDS) // sunucuya bağlanma
            .readTimeout(60, TimeUnit.SECONDS)    // cevabı bekleme (EN ÖNEMLİ)
            .writeTimeout(30, TimeUnit.SECONDS)   // body gönderme
            .build()

        // Retrofit istemcisi oluşturur: Base URL tanımlar ve JSON <-> Kotlin dönüşümü için Gson kullanır
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    }

}