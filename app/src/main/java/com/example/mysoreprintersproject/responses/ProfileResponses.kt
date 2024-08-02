package com.example.mysoreprintersproject.responses

import com.google.gson.annotations.SerializedName

data class ProfileResponses(
    @SerializedName("id"            ) var id           : Int?    = null,
    @SerializedName("name"          ) var name         : String? = null,
    @SerializedName("email"         ) var email        : String? = null,
    @SerializedName("phonenumber"   ) var phonenumber  : String? = null,
    @SerializedName("user_location" ) var userLocation : String? = null,
    @SerializedName("role"          ) var role         : String? = null,
    @SerializedName("created"       ) var created      : String? = null,
    @SerializedName("userID"        ) var userID       : Int?    = null,
    @SerializedName("profile_image" ) var profileImage : String? = null
)
