package com.example.mysoreprintersproject.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mysoreprintersproject.repository.AuthRepository
import com.example.mysoreprintersproject.responses.LoginResponse
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel(){


    private val _loginResponse : MutableLiveData<Resource<LoginResponse>> = MutableLiveData()
    val loginResponse: LiveData<Resource<LoginResponse>>
        get() = _loginResponse

//    private val _userProfileResponse: MutableLiveData<Resource<User>> = MutableLiveData()
//    val userProfileResponse: LiveData<Resource<User>>
//        get() = _userProfileResponse

    fun login(
        username: String,
        password: String
    )= viewModelScope.launch {
        _loginResponse.value=Resource.Loading
        _loginResponse.value =repository.login(username, password)
    }

//    suspend fun saveAccessToken(token: String){
//        repository.saveAuthToken(token)
//    }

    fun saveAccessToken(token: String) {
        viewModelScope.launch {
            repository.saveAuthToken(token)
        }
    }
    fun saveRefreshToken(refreshToken: String) {
        viewModelScope.launch {
            repository.saveRefreshToken(refreshToken)
        }
    }

    fun saveUSerId(userId: String){
        viewModelScope.launch {
            repository.saveUserId(userId)
        }
    }



//    fun fetchUserProfile() = viewModelScope.launch {
//        _userProfileResponse.value = Resource.Loading
//
//        val authToken = repository.getAccessToken()
//
//        if (authToken != null) {
//            val userProfileResult = withContext(Dispatchers.IO) {
//                repository.fetchUserProfile(authToken)
//            }
//
//            _userProfileResponse.value = userProfileResult
//        } else {
//            // Handle the case where the access token is not available
//            _userProfileResponse.value = Resource.Failure("Access token not found",null,null)
//        }
//    }

}