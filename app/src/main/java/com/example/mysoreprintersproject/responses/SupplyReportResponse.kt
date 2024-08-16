package com.example.mysoreprintersproject.responses

import com.google.gson.annotations.SerializedName

data class SupplyReportResponse(
    @SerializedName("SEname"  ) var SEname  : String? = null,
    @SerializedName("BPcode"  ) var BPcode  : String?    = null,
    @SerializedName("Date"    ) var Date    : String? = null,
    @SerializedName("SumofPv" ) var SumofPv : String? = null
)
