package com.example.mysoreprintersproject.app.supplyreport

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.mysoreprintersproject.R

class SupplyReportActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_supply_report)

        window.statusBarColor= ContextCompat.getColor(this,R.color.shade_blue)
    }
}