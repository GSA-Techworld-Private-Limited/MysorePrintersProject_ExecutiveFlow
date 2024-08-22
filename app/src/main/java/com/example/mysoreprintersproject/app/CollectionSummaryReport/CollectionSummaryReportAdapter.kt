package com.example.mysoreprintersproject.app.CollectionSummaryReport

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mysoreprintersproject.R
import com.example.mysoreprintersproject.app.dailycollections.DayCollectionAdapter
import com.example.mysoreprintersproject.responses.CollectionReport
import com.example.mysoreprintersproject.responses.CollectionResponses
import com.example.mysoreprintersproject.responses.CollectionSummaryReportResponses

class CollectionSummaryReportAdapter(
    private val list:List<CollectionSummaryReportResponses>
):
    RecyclerView.Adapter<CollectionSummaryReportAdapter.CardViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_collection_summary_report, parent, false)
        return CardViewHolder(view)
    }

    fun getSummaryList(): List<CollectionSummaryReportResponses> {
        return list
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
        val txtDate: TextView = itemView.findViewById(R.id.txtDate)
        val agentCode: TextView = itemView.findViewById(R.id.agentCode)
        val txtPaymentMethod: TextView = itemView.findViewById(R.id.txtPaymentMethod)
        val txtInstrumentNumber: TextView = itemView.findViewById(R.id.txtInstrumentNumber)
        val txtAmountCollected: TextView = itemView.findViewById(R.id.txtAmountCollected)


        fun bindView(postmodel: CollectionSummaryReportResponses){
            txtDate.text=postmodel.Date
            txtInstrumentNumber.text=postmodel.InstrumentNumber.toString()
            txtPaymentMethod.text=postmodel.paymentmethod
            agentCode.text=postmodel.agentCode
            txtAmountCollected.text=postmodel.AmountCollected.toString()
        }
    }
}