package com.example.mysoreprintersproject.responses

import com.google.gson.annotations.SerializedName

data class CollectionResponses(

    @SerializedName("id"                 ) var id               : Int?    = null,
    @SerializedName("sale_organization"  ) var saleOrganization : Int?    = null,
    @SerializedName("location"           ) var location         : String? = null,
    @SerializedName("manager_name"       ) var managerName      : String? = null,
    @SerializedName("customer"           ) var customer         : Long?    = null,
    @SerializedName("Name_1"             ) var Name1            : String? = null,
    @SerializedName("sale_employee_id"   ) var saleEmployeeId   : Int?    = null,
    @SerializedName("sale_employee"      ) var saleEmployee     : String? = null,
    @SerializedName("sale_district"      ) var saleDistrict     : String? = null,
    @SerializedName("district_name"      ) var districtName     : String? = null,
    @SerializedName("dropping_point"     ) var droppingPoint    : String? = null,
    @SerializedName("dropping_point_knd" ) var droppingPointKnd : String? = null,
    @SerializedName("month"              ) var month            : String? = null,
    @SerializedName("bill_amount"        ) var billAmount       : String? = null,
    @SerializedName("other_adjustment"   ) var otherAdjustment  : String? = null,
    @SerializedName("Bill_credit_notes"  ) var BillCreditNotes  : String? = null,
    @SerializedName("amount_collected"   ) var amountCollected  : String? = null,
    @SerializedName("pervious_dues"      ) var perviousDues     : String? = null,
    @SerializedName("total_dues"         ) var totalDues        : String? = null,
    @SerializedName("balance_amount"     ) var balanceAmount    : String? = null
)
