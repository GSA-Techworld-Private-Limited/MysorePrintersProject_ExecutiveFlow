package com.example.mysoreprintersproject.app.dailyworkingsummryfragment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.mysoreprintersproject.R

class DailyWorkingSummaryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_working_summary)
        window.statusBarColor= ContextCompat.getColor(this,R.color.shade_blue)
    }
}