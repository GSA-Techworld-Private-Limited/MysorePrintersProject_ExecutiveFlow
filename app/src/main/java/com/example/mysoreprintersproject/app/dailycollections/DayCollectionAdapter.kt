package com.example.mysoreprintersproject.app.dailycollections

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mysoreprintersproject.R
import com.example.mysoreprintersproject.app.dailyworkingsummryfragment.DailyWorkingSummaryAdapter
import com.example.mysoreprintersproject.responses.CollectionReport
import com.example.mysoreprintersproject.responses.CollectionResponses
import com.example.mysoreprintersproject.responses.NetSaleDataItem

class DayCollectionAdapter(
    private val list:List<CollectionResponses>
):
    RecyclerView.Adapter<DayCollectionAdapter.CardViewHolder>() {


    fun getCollectionList(): List<CollectionResponses> {
        return list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_collection_report, parent, false)
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
        val txtMonth: TextView = itemView.findViewById(R.id.txtMonth)
        val txtBillAmount: TextView = itemView.findViewById(R.id.txtBillAmount)
        val txtOtherAdujesument: TextView = itemView.findViewById(R.id.txtOtherAdujesument)
        val txtAmountCollected: TextView = itemView.findViewById(R.id.txtAmountCollected)
        val txtTolalDues: TextView = itemView.findViewById(R.id.txtTolalDues)
        val txtbalanceAmount: TextView = itemView.findViewById(R.id.txtbalanceAmount)

        fun bindView(postmodel:CollectionResponses){
            agentName.text=postmodel.agent
            txtMonth.text=postmodel.month
            txtBillAmount.text=postmodel.billAmount
            txtOtherAdujesument.text=postmodel.otherAdjustment
            txtAmountCollected.text=postmodel.amountCollected
            txtTolalDues.text=postmodel.totalDues
            txtbalanceAmount.text=postmodel.balanceAmount
        }
    }
}