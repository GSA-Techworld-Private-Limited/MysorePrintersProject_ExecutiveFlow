package com.example.mysoreprintersproject.app.netsale

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.mysoreprintersproject.R

class NetSaleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_net_sale)

        window.statusBarColor= ContextCompat.getColor(this,R.color.shade_blue)
    }
}