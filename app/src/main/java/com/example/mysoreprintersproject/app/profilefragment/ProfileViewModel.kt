package com.example.mysoreprintersproject.app.profilefragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mysoreprintersproject.network.Resource
import com.example.mysoreprintersproject.network.SessionManager
import com.example.mysoreprintersproject.responses.LoginResponse
import com.example.mysoreprintersproject.responses.ProfileResponses
import com.example.mysoreprintersproject.responses.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileViewModel (private val repository: UserRepository,
                        private val sessionManager: SessionManager): ViewModel() {

    private val _login: MutableLiveData<Resource<ProfileResponses>> = MutableLiveData()
    val login : LiveData<Resource<ProfileResponses>> = _login



    fun featchUserProfile() = viewModelScope.launch {

        _login.value = Resource.Loading

        val authToken = repository.getAccessToken()

        val id = sessionManager.fetchUserId()?.toInt() ?: -1

        if (authToken != null) {
            val fetchEventResult = withContext(Dispatchers.IO) {
                repository.getProfile("Bearer $authToken",id)
            }

            // Check if the fetchEventResult is of type Resource.Success and contains a list of EventResponse

            // Cast the result to the expected type and update _event
          // _login.value = fetchEventResult

        } else {
            // Handle the case where the access token is not available
            _login.value = Resource.Failure("Access token not found", null, null)
        }
    }
}