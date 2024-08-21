package com.example.mysoreprintersproject.responses

import com.google.gson.annotations.SerializedName

data class ExecutiveDashboard(

    @SerializedName("total_hours_worked"      ) var totalHoursWorked      : String?                    = null,
    @SerializedName("total_distance"          ) var totalDistance         : String?                    = null,
    @SerializedName("locations_visited_count" ) var locationsVisitedCount : Int?                       = null,
    @SerializedName("monthly_report"          ) var monthlyReport         : MonthlyReport?             = MonthlyReport(),
    @SerializedName("attendance_graph"        ) var attendanceGraph       : ArrayList<AttendanceGraph> = arrayListOf()
)


data class MonthlyReport (

    @SerializedName("Aug 2024" ) val Aug2024 : String? = null,
    @SerializedName("Jul 2024" ) var Jul2024 : String? = null

)


data class AttendanceGraph (

    @SerializedName("month"                 ) var month                : String? = null,
    @SerializedName("attendance_percentage" ) var attendancePercentage : Double?    = null

)