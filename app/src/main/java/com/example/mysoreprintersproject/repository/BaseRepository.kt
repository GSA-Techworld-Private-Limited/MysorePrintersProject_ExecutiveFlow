package com.example.mysoreprintersproject.repository


import com.example.mysoreprintersproject.network.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

abstract class BaseRepository {
    suspend fun <T> safeApiCall(
        apiCall: suspend () -> T
    ) : Resource<T> {
        return withContext(Dispatchers.IO){
            try {
                Resource.Success(apiCall.invoke())
            }catch (throwable: Throwable){
                when(throwable){
                    is HttpException -> {
                        Resource.Failure(throwable.message(),throwable.code(),throwable.response()?.errorBody())
                    }
                    else -> {
                        Resource.Failure(throwable.message ?: "Unknown error",null,null)
                    }
                }
            }
        }
    }
}

//abstract class BaseRepository {
//
//    suspend fun <T> safeApiCall(
//        apiCall: suspend () -> Response<T>
//    ): Resource<T> {
//        return withContext(Dispatchers.IO) {
//            try {
//                val response = apiCall.invoke()
//
//                if (response.isSuccessful) {
//                    Resource.Success(response.body())
//                } else {
//                    Resource.Failure(response.message(), response.code(), response.errorBody())
//                }
//            } catch (throwable: Throwable) {
//                when (throwable) {
//                    is HttpException -> {
//                        Resource.Failure(throwable.message(), throwable.code(), throwable.response()?.errorBody())
//                    }
//                    else -> {
//                        Resource.Failure(throwable.message ?: "Unknown error", null, null)
//                    }
//                }
//            }
//        }
//    }
//}
