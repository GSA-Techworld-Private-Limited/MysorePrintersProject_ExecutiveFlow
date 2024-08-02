package com.example.mysoreprintersproject.responses

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("refresh"       ) var refresh      : String? = null,
    @SerializedName("access"        ) var access       : String? = null,
    @SerializedName("name"          ) var name         : String? = null,
    @SerializedName("email"         ) var email        : String? = null,
    @SerializedName("phonenumber"   ) var phonenumber  : String? = null,
    @SerializedName("user_location" ) var userLocation : String? = null,
    @SerializedName("role"          ) var role         : String? = null,
    @SerializedName("user_id"       ) var userId       : Int?    = null

)
