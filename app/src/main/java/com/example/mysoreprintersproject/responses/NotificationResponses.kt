package com.example.mysoreprintersproject.responses

import com.google.gson.annotations.SerializedName

data class NotificationResponses(
    @SerializedName("id"                 ) var id                : Int?    = null,
    @SerializedName("notification_time"  ) var notificationTime  : String? = null,
    @SerializedName("title"              ) var title             : String? = null,
    @SerializedName("content"            ) var content           : String? = null,
    @SerializedName("notification_image" ) var notificationImage : String? = null,
    @SerializedName("role"               ) var role              : String? = null,
    @SerializedName("status"             ) var status            : String? = null,
    @SerializedName("DateTime"           ) var DateTime          : String? = null
)
