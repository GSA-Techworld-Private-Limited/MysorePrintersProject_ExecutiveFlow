package com.example.mysoreprintersproject.network

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.example.mysoreprintersproject.repository.BaseRepository
import com.example.mysoreprintersproject.responses.UserPreferences

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

abstract class BaseFragment<VM : ViewModel,B:ViewBinding, R: BaseRepository>:Fragment(){


    protected lateinit var userPreferences: UserPreferences
    protected lateinit var binding: B

    protected lateinit var viewModel: VM
    protected val remoteDateSource=  RemoteDataSource()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        userPreferences=UserPreferences(requireContext())
        binding=getFragmentBinding(inflater, container)
        val factory= ViewModelFactory(getFragmentRepository())
        viewModel= ViewModelProvider(this,factory)[getViewModel()]
        lifecycleScope.launch{userPreferences.authToken.first()}
        return binding.root
    }


    abstract fun getViewModel() : Class<VM>

    abstract fun getFragmentBinding(inflater: LayoutInflater,container: ViewGroup?) :B


    abstract fun getFragmentRepository(): R



}