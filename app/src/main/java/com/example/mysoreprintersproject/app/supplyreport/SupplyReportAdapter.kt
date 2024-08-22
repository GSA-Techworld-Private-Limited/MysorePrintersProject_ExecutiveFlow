package com.example.mysoreprintersproject.app.supplyreport

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mysoreprintersproject.R
import com.example.mysoreprintersproject.app.dailycollections.DayCollectionAdapter
import com.example.mysoreprintersproject.responses.DailyWorkingSummaryResponses
import com.example.mysoreprintersproject.responses.SupplyReportResponse

class SupplyReportAdapter(
    private val list:List<SupplyReportResponse>
):
    RecyclerView.Adapter<SupplyReportAdapter.CardViewHolder>() {

    fun getSupplyReportList(): List<SupplyReportResponse> {
        return list
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_supply_report, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        // Bind your data here
        //holder.cardTitle.text = items[position]
        holder.bindview(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val seName: TextView = itemView.findViewById(R.id.sename)
        val bpcode: TextView = itemView.findViewById(R.id.bpcode)
        val datetxt: TextView = itemView.findViewById(R.id.date)
        val sopv:TextView=itemView.findViewById(R.id.sopv)

        fun bindview(postmodel:SupplyReportResponse){
            seName.text=postmodel.SEname
            bpcode.text=postmodel.BPcode.toString()
            datetxt.text=postmodel.Date
            sopv.text=postmodel.SumofPv
        }

    }
}