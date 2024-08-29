package com.example.mysoreprintersproject.responses

import com.google.gson.annotations.SerializedName

data class AgentNameResponses(
    @SerializedName("name" ) var name : String? = null,
    @SerializedName("id"   ) var id   : Int?    = null
)
