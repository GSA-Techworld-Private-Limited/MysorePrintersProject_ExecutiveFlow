package com.example.mysoreprintersproject.app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.viewpager2.widget.ViewPager2
import com.example.mysoreprintersproject.R
import com.example.mysoreprintersproject.app.homecontainer.HomeContainerActivity
import com.example.mysoreprintersproject.network.AuthApi
import com.example.mysoreprintersproject.network.RemoteDataSource
import com.example.mysoreprintersproject.network.SessionManager
import com.example.mysoreprintersproject.network.ViewModelFactory
import com.example.mysoreprintersproject.repository.AuthRepository
import com.example.mysoreprintersproject.responses.UserPreferences


class SplashFragment : Fragment(R.layout.fragment_splash) {

    private lateinit var viewPager: ViewPager2
    private lateinit var indicator1: ImageView
    private lateinit var indicator2: ImageView
    private lateinit var nextButton: ImageButton

    private lateinit var sessionManager: SessionManager
    protected val remoteDateSource=  RemoteDataSource()
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        sessionManager=SessionManager(requireActivity())

        // Check if the user is already logged in
        if (sessionManager.fetchAuthToken() != null) {
            // User is logged in, navigate directly to HomeContainerActivity
            navigateToHome()
            return
        }

        viewPager = requireView().findViewById(R.id.viewPager)
        indicator1 = requireView().findViewById(R.id.indicator1)
        indicator2 = requireView().findViewById(R.id.indicator2)
        nextButton = requireView().findViewById(R.id.nextButton)



        val api = remoteDateSource.buildApi(AuthApi::class.java)
        val preferences = UserPreferences(requireActivity())
        val authRepository = AuthRepository(api,preferences)
        val factory = ViewModelFactory(authRepository)
        val adapter = ViewPagerAdapter(requireActivity(), factory)

        viewPager.adapter = adapter

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateIndicators(position)
            }
        })

        nextButton.setOnClickListener {
            if (viewPager.currentItem < adapter.itemCount - 1) {
                viewPager.currentItem += 1
            }
        }

        // Automatically move to the second page after 2 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            if (viewPager.currentItem < adapter.itemCount - 1) {
                viewPager.currentItem += 1
            }
        }, 2000)
    }


    private fun updateIndicators(position: Int) {
        when (position) {
            0 -> {
                indicator1.setImageResource(R.drawable.indicator_selected)
                indicator2.setImageResource(R.drawable.indicator_unselected)
            }
            1 -> {
                indicator1.setImageResource(R.drawable.indicator_unselected)
                indicator2.setImageResource(R.drawable.indicator_selected)
            }
        }
    }


    private fun navigateToHome() {
        val intent = Intent(requireActivity(), HomeContainerActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }


}