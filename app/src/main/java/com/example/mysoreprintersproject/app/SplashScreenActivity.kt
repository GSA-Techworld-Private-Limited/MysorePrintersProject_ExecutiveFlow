package com.example.mysoreprintersproject.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.ImageView
import androidx.viewpager2.widget.ViewPager2
import com.example.mysoreprintersproject.R
import com.example.mysoreprintersproject.app.homecontainer.HomeContainerActivity
import com.example.mysoreprintersproject.network.APIManager
import com.example.mysoreprintersproject.network.AuthApi
import com.example.mysoreprintersproject.network.RemoteDataSource
import com.example.mysoreprintersproject.network.SessionManager
import com.example.mysoreprintersproject.network.ViewModelFactory
import com.example.mysoreprintersproject.repository.AuthRepository
import com.example.mysoreprintersproject.responses.UserPreferences

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var indicator1: ImageView
    private lateinit var indicator2: ImageView
    private lateinit var nextButton: ImageButton

    private lateinit var sessionManager: SessionManager
    protected val remoteDateSource=  RemoteDataSource()
    override fun onCreate(savedInstanceState: Bundle?) {
        sessionManager=SessionManager(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        viewPager = findViewById(R.id.viewPager)
        indicator1 = findViewById(R.id.indicator1)
        indicator2 = findViewById(R.id.indicator2)
        nextButton = findViewById(R.id.nextButton)


        val userrtype=sessionManager.fetchUserRole()

        if(userrtype=="executive"){
            navigateToHome()
        }else{
            return
        }

        val api = remoteDateSource.buildApi(AuthApi::class.java)
        val preferences = UserPreferences(this)
        val authRepository = AuthRepository(api,preferences)
        val factory = ViewModelFactory(authRepository,sessionManager)
        val adapter = ViewPagerAdapter(this, factory)


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

    fun navigateToHome(){
        val i=Intent(this,HomeContainerActivity::class.java)
        startActivity(i)
        finish()
    }
}