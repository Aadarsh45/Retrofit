package com.example.retrofit.api


import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class MyIntercepter:Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request:Request = chain.request()
            .newBuilder()
            .addHeader("Content-Type", "application/json")
            .addHeader("X-Platform","Android")
            .addHeader("X-Auth-Token","1212121212")
            .build()
        return chain.proceed(request)

    }

}