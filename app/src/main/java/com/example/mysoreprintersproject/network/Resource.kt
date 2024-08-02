package com.example.mysoreprintersproject.network

import okhttp3.ResponseBody

//sealed class Resource<out T> {
//    data class Success<out T>(val value: T) : Resource<T>()
//    data class Failure(
//        val isNetworkError: String,
//        val errorCode: Int?,
//        val errorBody: ResponseBody?
//    ) : Resource<Nothing>()
//    object Loading : Resource<Nothing>()
//}
sealed class Resource<out T> {

    data class Success<out T>(val data: T) : Resource<T>()
    data class Failure(
        val message: String,
        val errorCode: Int?,
        val errorBody: ResponseBody?
    ) : Resource<Nothing>()
    object Loading : Resource<Nothing>()
}