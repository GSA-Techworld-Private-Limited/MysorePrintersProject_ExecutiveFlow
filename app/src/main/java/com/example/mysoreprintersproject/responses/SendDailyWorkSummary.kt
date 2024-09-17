package com.example.mysoreprintersproject.responses

data class SendDailyWorkSummary(
    val id:String,
    val markets_visited:String,
    val agent_visited:String,
    val institute_visited:String,
    val task_accomplished:String,
    val whatsapp_num:String,
    val emailID:String,
    val Date:String,
    val agent_code:String
)
