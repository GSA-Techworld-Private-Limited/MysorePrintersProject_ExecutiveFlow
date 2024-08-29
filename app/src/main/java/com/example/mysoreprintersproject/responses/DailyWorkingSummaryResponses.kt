package com.example.mysoreprintersproject.responses

import com.google.gson.annotations.SerializedName

data class DailyWorkingSummaryResponses(
    @SerializedName("id"                  ) var id                 : Int?    = null,
    @SerializedName("Market_visited"      ) var MarketVisited      : String? = null,
    @SerializedName("Agents_visited"      ) var AgentsVisited      : String? = null,
    @SerializedName("Institution_visited" ) var InstitutionVisited : Int?    = null,
    @SerializedName("Tasks_Accomplished"  ) var TasksAccomplished  : String? = null,
    @SerializedName("Date"                ) var Date               : String? = null,
    @SerializedName("Whatsapp_Number"     ) var WhatsappNumber     : String? = null,
    @SerializedName("emailID"             ) var emailID            : String? = null,
    @SerializedName("Executive"           ) var Executive          : Int?    = null
)
