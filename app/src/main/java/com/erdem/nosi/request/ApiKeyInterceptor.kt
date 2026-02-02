package com.erdem.nosi.request

import com.erdem.nosi.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class ApiKeyInterceptor: Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        val newUrl = original.url.newBuilder()
            .addQueryParameter("key", BuildConfig.key)
            .build()

        val newRequest = original.newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(newRequest)
    }

}