package com.example.mysoreprintersproject.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.ImageView
import androidx.viewpager2.widget.ViewPager2
import com.example.mysoreprintersproject.R

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var indicator1: ImageView
    private lateinit var indicator2: ImageView
    private lateinit var nextButton: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        viewPager = findViewById(R.id.viewPager)
        indicator1 = findViewById(R.id.indicator1)
        indicator2 = findViewById(R.id.indicator2)
        nextButton = findViewById(R.id.nextButton)

        val adapter = ViewPagerAdapter(this)
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
}