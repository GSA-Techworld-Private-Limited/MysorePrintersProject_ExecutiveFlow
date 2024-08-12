package com.example.mysoreprintersproject.responses

import com.google.gson.annotations.SerializedName

data class SummaryReportResponses(

val total_hours_worked: String,
val total_distance: String,
val locations_visited_count: Int,
val locations_visited_details: Map<String, Int>

)
