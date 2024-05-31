package com.example.mysoreprintersproject.app
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.example.mysoreprintersproject.R
import com.example.mysoreprintersproject.app.homecontainer.HomeContainerActivity

class ViewPagerAdapter(private val context: Context) : RecyclerView.Adapter<ViewPagerAdapter.ViewHolder>() {

    private val layouts = arrayOf(
        R.layout.fragment_logo,
        R.layout.fragment_login
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(layouts[viewType], parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // No binding necessary for static layouts
        if (position == 1) {
            holder.itemView.findViewById<AppCompatButton>(R.id.loginButton).setOnClickListener {
                val intent = Intent(context, HomeContainerActivity::class.java)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return layouts.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}