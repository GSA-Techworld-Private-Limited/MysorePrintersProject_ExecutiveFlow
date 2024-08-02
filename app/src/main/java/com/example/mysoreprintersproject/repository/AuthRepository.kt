package com.example.mysoreprintersproject.repository

import com.example.mysoreprintersproject.network.AuthApi
import com.example.mysoreprintersproject.responses.UserPreferences


class AuthRepository(
    private val api: AuthApi,
    private val preferences: UserPreferences
) : BaseRepository() {



    suspend fun login(
        username : String,
        password : String
    ) = safeApiCall {
        api.login(username,password)
    }



//    suspend fun fetchUserProfile(authToken: String): Resource<LoginResponse> {
//        try {
//            if (authToken.isNullOrEmpty()) {
//                return Resource.Failure("Access token not found", null, null)
//            }
//
//            val response = api.getUserProfile("Bearer $authToken")
//
//            if (response.isSuccessful) {
//                val user = response.body()
//                if (user != null) {
//                    return Resource.Success(user)
//                } else {
//                    return Resource.Failure("Invalid response", response.code(), response.errorBody())
//                }
//            } else {
//                return Resource.Failure("Failed to fetch user details: ${response.message()}", response.code(), response.errorBody())
//            }
//        } catch (e: Exception) {
//            return Resource.Failure("Error fetching user details: ${e.message}", null, null)
//        }
//    }


    suspend fun saveAuthToken(token : String){
         preferences.saveAuthToken(token)
     }

     suspend fun getAccessToken(): String? {
         return preferences.getAuthToken()
     }

    suspend fun saveRefreshToken(refreshToken:String){
        preferences.saveRefreshToken(refreshToken)
    }

    suspend fun getRefreshToken(refreshToken:String){
        preferences.getRefreshToken()
    }


    suspend fun saveUserId(userId: String){
        preferences.saveUserId(userId)
    }

    suspend fun getUserId(userId: String){
        preferences.getUserId()
    }
}