package com.example.mysoreprintersproject.network
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory




class RemoteDataSource {
    companion object{
        private const val BASE_URL = "https://np3bx41m-8002.inc1.devtunnels.ms"
      // private const val BASE_URL="https://4439-152-58-82-203.ngrok-free.app"
    }
    fun getImageUrl(imagePath: String): String {
        return BASE_URL + imagePath
    }
    fun<Api> buildApi(
        api: Class<Api>,
        authToken: String? = null
    ):Api{
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(
                OkHttpClient.Builder()
                    .addInterceptor {chain ->
                        chain.proceed(chain.request().newBuilder().also {
                            it.addHeader("Accept","application/json")

                        }.build())
                    }.also { client ->
                            val logging = HttpLoggingInterceptor();
                            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
                            client.addInterceptor(logging)
                    }.build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(api)
    }
}