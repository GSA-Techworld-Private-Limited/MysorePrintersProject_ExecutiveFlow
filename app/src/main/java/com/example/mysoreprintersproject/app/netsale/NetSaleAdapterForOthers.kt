package com.example.mysoreprintersproject.app.netsale

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mysoreprintersproject.R
import com.example.mysoreprintersproject.responses.NetSaleDataItem
import com.example.mysoreprintersproject.responses.NetSaleDataOthers

class NetSaleAdapterForOthers (
    private val list:List<NetSaleDataOthers>
):
    RecyclerView.Adapter<NetSaleAdapterForOthers.CardViewHolder>() {


    fun getNetSaleList(): List<NetSaleDataOthers> {
        return list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_net_sale_others, parent, false)
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
        val txtNetSale: TextView = itemView.findViewById(R.id.txtNetSale)
        val txtsumofdh:TextView=itemView.findViewById(R.id.txtsumofdh)
        val txtsumofpv:TextView=itemView.findViewById(R.id.txtsumofpv)
        val txtsumofmy:TextView=itemView.findViewById(R.id.txtsumofmy)
        val txtBpCode:TextView=itemView.findViewById(R.id.txtBpCode)

        fun bindView(postmodel: NetSaleDataOthers) {
            agentName.text = postmodel.ManagerName
            txtterritory.text = postmodel.SEName
            txtdropoint.text = postmodel.DistrictName
            txtNetSale.text = postmodel.totalNetSales.toString()
            txtsumofdh.text=postmodel.SumOfDH.toString()
            txtsumofpv.text=postmodel.SumOfPV.toString()
            txtsumofmy.text=postmodel.SumOfMY.toString()
            txtBpCode.text=postmodel.BPCode
        }
    }
}