package com.example.mysoreprintersproject.responses

import com.google.gson.annotations.SerializedName

data class CollectionResponses(
    @SerializedName("id"               ) var id              : Int?    = null,
    @SerializedName("agent"            ) var agent           : String? = null,
    @SerializedName("month"            ) var month           : String? = null,
    @SerializedName("bill_amount"      ) var billAmount      : String? = null,
    @SerializedName("other_adjustment" ) var otherAdjustment : String? = null,
    @SerializedName("amount_collected" ) var amountCollected : String? = null,
    @SerializedName("total_dues"       ) var totalDues       : String? = null,
    @SerializedName("balance_amount"   ) var balanceAmount   : String? = null,
    @SerializedName("executive"        ) var executive       : String? = null
)
