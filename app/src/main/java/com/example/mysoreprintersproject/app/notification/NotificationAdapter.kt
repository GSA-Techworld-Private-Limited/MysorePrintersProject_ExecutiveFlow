package com.example.mysoreprintersproject.app.notification

import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.mysoreprintersproject.R
import com.example.mysoreprintersproject.app.CollectionSummaryReport.CollectionSummaryReportAdapter
import com.example.mysoreprintersproject.network.APIManager
import com.example.mysoreprintersproject.network.SessionManager
import com.example.mysoreprintersproject.responses.CollectionSummaryReportResponses
import com.example.mysoreprintersproject.responses.NotificationRequest
import com.example.mysoreprintersproject.responses.NotificationResponses
import com.example.mysoreprintersproject.responses.SendDailyWorkSummary
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationAdapter (
    private val list:List<NotificationResponses>,
    private val sessionManager: SessionManager
):
    RecyclerView.Adapter<NotificationAdapter.CardViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return CardViewHolder(view,sessionManager)
    }

    fun getSummaryList(): List<NotificationResponses> {
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
    class CardViewHolder(itemView: View, private val sessionManager: SessionManager) : RecyclerView.ViewHolder(itemView) {
        val tvNotificationId: TextView = itemView.findViewById(R.id.tvNotificationId)
        val tvDateTime: TextView = itemView.findViewById(R.id.tvDateTime)
        val tvContent: TextView = itemView.findViewById(R.id.tvContent)

        val threeDonts :ImageView=itemView.findViewById(R.id.ivOverflowMenu)
        val fullLayout:ConstraintLayout=itemView.findViewById(R.id.fullLayout)

        val progressBar:ProgressBar=itemView.findViewById(R.id.progressBar)

        var id:String=""
        fun bindView(postmodel: NotificationResponses){
            tvNotificationId.text=postmodel.title
            val date=postmodel.DateTime
            tvDateTime.text="Date: $date"
            val content=postmodel.content
            tvContent.text="content: $content"

            id=postmodel.id.toString()

            threeDonts.setOnClickListener {
                showPopupMenu(it)
            }

        }

        private fun showPopupMenu(view: View) {
            val popup = PopupMenu(view.context, view)
            popup.inflate(R.menu.menu_notification_options) // Inflate the menu resource
            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_delete -> {
                        // Handle delete click
                        deleteNotification(id)
                        progressBar.visibility=View.VISIBLE
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }

        private fun deleteNotification(notificationid:String){
            val id=sessionManager.fetchUserId()!!
            val token=sessionManager.fetchAuthToken()
            val authorization="Bearer $token"
            val postRequests= NotificationRequest(notificationid)
            val apiService=APIManager.apiInterface
            val call = apiService.deletNotification(authorization,postRequests)
            call.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    progressBar.visibility=View.GONE
                    if (response.isSuccessful) {

                        Toast.makeText(itemView.context,"Notification Deleted", Toast.LENGTH_SHORT).show()
                        fullLayout.visibility=View.GONE
                    } else {
                        // Handle other HTTP error codes if needed
                        Toast.makeText(itemView.context, "failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    // Handle failure (e.g., network failure, timeout)
                    Toast.makeText(itemView.context, "Failed", Toast.LENGTH_SHORT).show()
                    progressBar.visibility=View.GONE
                }
            })
        }
    }


}