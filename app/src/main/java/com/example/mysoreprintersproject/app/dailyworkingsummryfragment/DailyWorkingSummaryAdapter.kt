package com.example.mysoreprintersproject.app.dailyworkingsummryfragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mysoreprintersproject.R

class DailyWorkingSummaryAdapter:
    RecyclerView.Adapter<DailyWorkingSummaryAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_daily_working_summary, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        // Bind your data here
        //holder.cardTitle.text = items[position]
    }

    override fun getItemCount(): Int = 2

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val cardTitle: TextView = itemView.findViewById(R.id.card_title)
//        val cardTime: TextView = itemView.findViewById(R.id.card_time)
//        val cardStatus: TextView = itemView.findViewById(R.id.card_status)
    }
}
