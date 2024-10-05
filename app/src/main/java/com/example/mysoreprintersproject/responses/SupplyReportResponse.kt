package com.example.mysoreprintersproject.responses

import com.google.gson.annotations.SerializedName

data class SupplyReportResponse(
    @SerializedName("id"                  ) var id                : Int?    = null,
    @SerializedName("Date"                ) var Date              : String? = null,
    @SerializedName("SEname"              ) var SEname            : String? = null,
    @SerializedName("BPcode"              ) var BPcode            : Long?    = null,
    @SerializedName("SumofPv"             ) var SumofPv           : String? = null,
    @SerializedName("RegionalManager"     ) var RegionalManager   : String? = null,
    @SerializedName("ManagerName"         ) var ManagerName       : String? = null,
    @SerializedName("SumofDH"             ) var SumofDH           : String? = null,
    @SerializedName("segment"             ) var segment           : String? = null,
    @SerializedName("sales_org"           ) var salesOrg          : String? = null,
    @SerializedName("Description"         ) var Description       : String? = null,
    @SerializedName("BP_name"             ) var BPName            : String? = null,
    @SerializedName("Main_Agent"          ) var MainAgent         : String? = null,
    @SerializedName("Se_code"             ) var SeCode            : String? = null,
    @SerializedName("district_code"       ) var districtCode      : String? = null,
    @SerializedName("district_name"       ) var districtName      : String? = null,
    @SerializedName("dropping_point_desc" ) var droppingPointDesc : String? = null,
    @SerializedName("route_description"   ) var routeDescription  : String? = null,
    @SerializedName("segment_1_desc"      ) var segment1Desc      : String? = null
)
