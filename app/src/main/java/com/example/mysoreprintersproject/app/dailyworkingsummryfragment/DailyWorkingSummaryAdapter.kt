package com.example.mysoreprintersproject.app.dailyworkingsummryfragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mysoreprintersproject.R
import com.example.mysoreprintersproject.responses.DailyWorkingSummaryResponses

class DailyWorkingSummaryAdapter(
    private val list:List<DailyWorkingSummaryResponses>
):
    RecyclerView.Adapter<DailyWorkingSummaryAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_daily_working_summary, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        // Bind your data here
        //holder.cardTitle.text = items[position]

        return holder.bindview(list[position])
    }

    override fun getItemCount(): Int{
        return list.size
    }

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtDate: TextView = itemView.findViewById(R.id.txtDate)
        val txtmarketVisited: TextView = itemView.findViewById(R.id.txtmarketVisited)
        val txtagentName: TextView = itemView.findViewById(R.id.txtagentName)

        fun bindview(postmodel:DailyWorkingSummaryResponses){
            txtDate.text=postmodel.Date
            txtagentName.text=postmodel.userName


            val rawAgentVisited = postmodel.agentVisited!!

// Remove square brackets and quotes
            val cleanedAgentVisited = rawAgentVisited
                .replace("[", "")
                .replace("]", "")
                .replace("'", "")
                .replace("\"", "")

// Split the string by commas to get the list of market names
            val marketNames = cleanedAgentVisited.split(",")

// Join the list into a single string with proper formatting
            val formattedMarketVisited = marketNames.joinToString(separator = ",") { it.trim() }

// Set the formatted string to the TextView
            txtmarketVisited.text = formattedMarketVisited

        }
    }
}
