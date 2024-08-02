package com.example.mysoreprintersproject.network
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mysoreprintersproject.app.profilefragment.ProfileViewModel
import com.example.mysoreprintersproject.repository.AuthRepository
import com.example.mysoreprintersproject.repository.BaseRepository
import com.example.mysoreprintersproject.responses.UserRepository

import java.lang.IllegalArgumentException

class ViewModelFactory(
    private val repository : BaseRepository,
    private val sessionManager: SessionManager
):ViewModelProvider.NewInstanceFactory(){

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when{
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> AuthViewModel(repository as AuthRepository) as T
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> ProfileViewModel(repository as UserRepository,sessionManager) as T
//            modelClass.isAssignableFrom(ResetOtpViewModel::class.java) -> ResetOtpViewModel(repository as AuthRepository) as T
//            modelClass.isAssignableFrom(OtpViewModel::class.java) -> OtpViewModel(repository as AuthRepository) as T
//            modelClass.isAssignableFrom(ResetViewModel::class.java) -> ResetViewModel(repository as AuthRepository) as T
//            modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(repository as UserRepository) as T
//            modelClass.isAssignableFrom(BuyDetailsViewModel::class.java) -> BuyDetailsViewModel(repository as UserRepository) as T
//            modelClass.isAssignableFrom(TicketViewModel::class.java) -> TicketViewModel(repository as UserRepository) as T
//            modelClass.isAssignableFrom(CartViewModel::class.java) -> CartViewModel(repository as UserRepository) as T
//            modelClass.isAssignableFrom(CategoryViewModel::class.java) -> CategoryViewModel(repository as UserRepository) as T
            else -> throw IllegalArgumentException("ViewModelClass Not Found")
        }
    }
}