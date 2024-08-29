package com.example.mysoreprintersproject.responses

import com.google.gson.annotations.SerializedName

data class LocationNameResponse(
    @SerializedName("locations" ) var locations : ArrayList<String> = arrayListOf()
)
