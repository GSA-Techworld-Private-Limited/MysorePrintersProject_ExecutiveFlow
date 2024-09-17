package com.example.mysoreprintersproject.responses

import com.google.gson.annotations.SerializedName

data class CollectionSummaryReportResponses(
    @SerializedName("id"               ) var id               : Int?    = null,
    @SerializedName("executive_name"   ) var executiveName    : String? = null,
    @SerializedName("agent_code"       ) var agentCode        : String? = null,
    @SerializedName("paymentmethod"    ) var paymentmethod    : String? = null,
    @SerializedName("InstrumentNumber" ) var InstrumentNumber : Long?    = null,
    @SerializedName("AmountCollected"  ) var AmountCollected  : Int?    = null,
    @SerializedName("agent"            ) var agent            : String? = null,
    @SerializedName("Date"             ) var Date             : String? = null,
    @SerializedName("Executive"        ) var Executive        : Int?    = null
)
