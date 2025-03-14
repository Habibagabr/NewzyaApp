package com.habiba.newsapp.APIResquests

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object retrofit {
    private const val BASE_URL ="https://newsapi.org/v2/"


    // Step 1: Create Logging Interceptor
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // ✅ Logs full API responses
    }

    // Step 2: Create OkHttpClient with Logging and User-Agent
    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor) // ✅ Logs requests and responses
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 10) NewsApp") // ✅ Prevent Cloudflare blocks
                .build()
            chain.proceed(request)
        }
        .build()

    //  Step 3: Create Retrofit Instance
    private val retrofitInstance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client) // ✅ Attach the OkHttpClient with interceptors
            .build()
    }

    //  Step 4: Create API Interface
    val api: API_Interface by lazy {
        retrofitInstance.create(API_Interface::class.java)
    }
}
