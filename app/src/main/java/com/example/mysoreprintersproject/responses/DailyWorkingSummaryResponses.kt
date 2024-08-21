package com.example.mysoreprintersproject.responses

import com.google.gson.annotations.SerializedName

data class DailyWorkingSummaryResponses(
    @SerializedName("user_name"      ) var userName      : String? = null,
    @SerializedName("agent_visited"  ) var agentVisited  : String? = null,
    @SerializedName("total_distance" ) var totalDistance : String? = null,
    @SerializedName("Date"           ) var Date          : String? = null
)
