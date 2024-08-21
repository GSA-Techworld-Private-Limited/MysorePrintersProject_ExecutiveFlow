package com.example.mysoreprintersproject.responses

import com.google.gson.annotations.SerializedName

data class SummaryReportResponses(

    @SerializedName("total_distance"          ) var totalDistance         : String?           = null,
    @SerializedName("total_hours_worked"      ) var totalHoursWorked      : String?           = null,
    @SerializedName("locations_visited_count" ) var locationsVisitedCount : Int?              = null,
    @SerializedName("locations_visited"       ) var locationsVisited      : ArrayList<String> = arrayListOf()

)
