package com.example.mysoreprintersproject.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object APIManager {
   // private val BASE_URL = "http://3.111.197.59:8000"
    private val BASE_URL="https://jqtf8plh-8000.inc1.devtunnels.ms/"

    // API response interceptor
    val loggingInterceptor = HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BODY)

    // Client
    val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    val  apiInterface : DataSource by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).client(client)
            .build()
            .create(DataSource::class.java)
    }

    fun getImageUrl(imagePath: String): String {
        return BASE_URL + imagePath
    }
}