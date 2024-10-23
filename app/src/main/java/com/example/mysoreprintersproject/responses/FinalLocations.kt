package com.example.mysoreprintersproject.responses

import com.google.gson.annotations.SerializedName

data class FinalLocations(
    @SerializedName("id"        ) var id        : Int?    = null,
    @SerializedName("latitude"  ) var latitude  : String? = null,
    @SerializedName("longitude" ) var longitude : String? = null,
    @SerializedName("timestamp" ) var timestamp : String? = null,
    @SerializedName("location"  ) var location  : Int?    = null
)
