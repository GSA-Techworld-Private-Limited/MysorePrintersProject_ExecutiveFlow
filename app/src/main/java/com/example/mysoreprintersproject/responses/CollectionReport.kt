package com.example.mysoreprintersproject.responses

data class CollectionReport(
    val agent:String,
    val agent_code:String,
    val paymentmethod:String,
    val instrumentnumber:String,
    val amountcollected:String,
    val id:Int
)
