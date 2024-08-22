package com.example.mysoreprintersproject.app.CollectionSummaryReport

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mysoreprintersproject.R

class CollectionSummaryReportActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection_summary_report)
        window.statusBarColor= ContextCompat.getColor(this,R.color.shade_blue)
    }
}