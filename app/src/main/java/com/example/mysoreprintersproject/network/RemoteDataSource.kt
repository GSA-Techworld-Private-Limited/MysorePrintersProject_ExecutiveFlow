package com.example.mysoreprintersproject.network

import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RemoteDataSource {
    companion object {
        //private const val BASE_URL = "http://3.111.197.59:8000"
        private const val BASE_URL = "https://jqtf8plh-8000.inc1.devtunnels.ms/"
    }

    fun getImageUrl(imagePath: String): String {
        return BASE_URL + imagePath
    }

    fun <Api> buildApi(
        api: Class<Api>,
        authToken: String? = null
    ): Api {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(
                OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        chain.proceed(chain.request().newBuilder().also {
                            it.addHeader("Accept", "application/json")
                        }.build())
                    }
                    .also { client ->
                        val logging = HttpLoggingInterceptor()
                        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
                        client.addInterceptor(logging)
                    }
                    // Set a reasonable timeout duration to avoid SocketTimeoutException
                    .connectTimeout(60, TimeUnit.SECONDS) // 60 seconds for connection timeout
                    .readTimeout(60, TimeUnit.SECONDS)    // 60 seconds for read timeout
                    .writeTimeout(60, TimeUnit.SECONDS)   // 60 seconds for write timeout
                    .protocols(listOf(Protocol.HTTP_1_1))
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(api)
    }
}
