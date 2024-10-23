package com.example.mysoreprintersproject.responses

import com.google.gson.annotations.SerializedName

data class NetSaleDataForOthers(
    @SerializedName("net_sale_data" ) var netSaleData : List<NetSaleDataOthers> = arrayListOf(),
   // @SerializedName("monthly_sales_data" ) var monthlySalesData : MonthlySalesDataOthers?      = MonthlySalesDataOthers()
)


data class NetSaleDataOthers (

    @SerializedName("Manager_Name"    ) var ManagerName   : String? = null,
    @SerializedName("SE_Name"         ) var SEName        : String? = null,
    @SerializedName("BP_Code")var BPCode:String?=null,
    @SerializedName("District_Name"   ) var DistrictName  : String? = null,
    @SerializedName("Month"           ) var Month         : String? = null,
    @SerializedName("Sum_of_DH"       ) var SumOfDH       : Int?    = null,
    @SerializedName("Sum_of_PV"       ) var SumOfPV       : Int?    = null,
    @SerializedName("Sum_of_MY"       ) var SumOfMY       : Int?    = null,
    @SerializedName("total_net_sales" ) var totalNetSales : Int?    = null

)