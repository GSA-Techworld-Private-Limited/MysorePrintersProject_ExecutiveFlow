package com.example.mysoreprintersproject.network

import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object APIManager {
    // private val BASE_URL = "http://3.111.197.59:8000"
    private val BASE_URL = "https://jqtf8plh-8000.inc1.devtunnels.ms/"

    // API response interceptor
    val loggingInterceptor = HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BODY)

    // Client with custom timeout settings
    val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(60, TimeUnit.SECONDS)  // 60 seconds connection timeout
        .readTimeout(60, TimeUnit.SECONDS)     // 60 seconds read timeout
        .writeTimeout(60, TimeUnit.SECONDS)    // 60 seconds write timeout
        .protocols(listOf(Protocol.HTTP_1_1))
        .build()

    val apiInterface: DataSource by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(DataSource::class.java)
    }

    fun getImageUrl(imagePath: String): String {
        return BASE_URL + imagePath
    }
}
