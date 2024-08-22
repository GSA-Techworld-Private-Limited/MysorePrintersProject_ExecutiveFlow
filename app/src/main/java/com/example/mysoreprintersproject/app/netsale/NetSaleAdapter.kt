package com.example.mysoreprintersproject.app.netsale

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mysoreprintersproject.R
import com.example.mysoreprintersproject.app.supplyreport.SupplyReportAdapter
import com.example.mysoreprintersproject.responses.DailyWorkingSummaryResponses
import com.example.mysoreprintersproject.responses.NetSaleDataItem

class NetSaleAdapter(
    private val list:List<NetSaleDataItem>
):
    RecyclerView.Adapter<NetSaleAdapter.CardViewHolder>() {


    fun getNetSaleList(): List<NetSaleDataItem> {
        return list
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_net_sale, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        // Bind your data here
        //holder.cardTitle.text = items[position]
        holder.bindView(list[position])
    }

    override fun getItemCount(): Int {

        return list.size
    }

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val agentName: TextView = itemView.findViewById(R.id.agentName)
        val txtterritory: TextView = itemView.findViewById(R.id.txtterritory)
        val txtdropoint: TextView = itemView.findViewById(R.id.txtdropoint)
        val txtNetSale:TextView=itemView.findViewById(R.id.txtNetSale)

        fun bindView(postmodel:NetSaleDataItem){
            agentName.text=postmodel.AgentName
            txtterritory.text=postmodel.Territory
            txtdropoint.text=postmodel.DropPoint
            txtNetSale.text=postmodel.Total_net_sales.toString()
        }
    }
}