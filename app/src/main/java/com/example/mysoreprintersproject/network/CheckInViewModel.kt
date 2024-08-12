package com.example.mysoreprintersproject.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mysoreprintersproject.repository.AuthRepository
import com.example.mysoreprintersproject.responses.CheckInRequest
import com.example.mysoreprintersproject.responses.CheckOutRequest
import com.example.mysoreprintersproject.responses.ChecksResponses
import com.example.mysoreprintersproject.responses.LoginResponse
import com.example.mysoreprintersproject.responses.UserRepository
import kotlinx.coroutines.launch

class CheckInViewModel(
    private val repository: UserRepository
) : ViewModel(){
    private val _checkInResponse : MutableLiveData<Resource<ChecksResponses>> = MutableLiveData()
    val checkInResponse: LiveData<Resource<ChecksResponses>>
        get() = _checkInResponse



    private val _checkOutResponse: MutableLiveData<Resource<ChecksResponses>> = MutableLiveData()
    val checkOutResponse: LiveData<Resource<ChecksResponses>>
        get() = _checkOutResponse


    fun checkIn(
        autorization:String,
        checkInRequest:CheckInRequest
    )=viewModelScope.launch {
        _checkInResponse.value=Resource.Loading
        _checkInResponse.value=repository.checkIn(autorization,checkInRequest)
    }


    fun checkOut(
        authorization: String,
        checkOutRequest: CheckOutRequest
    ) = viewModelScope.launch {
        _checkOutResponse.value = Resource.Loading
        _checkOutResponse.value = repository.checkOut(authorization, checkOutRequest)
    }
}