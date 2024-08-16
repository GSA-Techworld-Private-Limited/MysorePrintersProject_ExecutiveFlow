package com.example.mysoreprintersproject.responses

data class NetSalesResponse(
    val net_sale_data: List<NetSaleDataItem>,
    val monthly_sales_data: Map<String, Double>
)

data class MonthlySalesData(
    val monthlySales: Map<String, Double>
)


data class NetSaleDataItem(
    val id: Int,
    val Manager: String,
    val AgentName: String,
    val Territory: String,
    val DropPoint: String,
    val Total_net_sales: Int,
    val Executive: String,
    val Publication: String,
    val Date: String
)
