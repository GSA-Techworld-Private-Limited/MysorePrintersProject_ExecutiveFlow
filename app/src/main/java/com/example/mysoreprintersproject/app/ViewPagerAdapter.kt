package com.example.mysoreprintersproject.app
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.mysoreprintersproject.R
import com.example.mysoreprintersproject.app.homecontainer.HomeContainerActivity
import com.example.mysoreprintersproject.network.AuthViewModel
import com.example.mysoreprintersproject.network.Resource
import com.example.mysoreprintersproject.network.SessionManager

class ViewPagerAdapter(private val context: Context,
                       private val factory: ViewModelProvider.Factory) : RecyclerView.Adapter<ViewPagerAdapter.ViewHolder>() {

    private val layouts = arrayOf(
        R.layout.fragment_logo,
        R.layout.fragment_login
    )


    // Assuming you're passing the ViewModelStoreOwner (e.g., Activity) as the context
    private val viewModel = ViewModelProvider(context as ViewModelStoreOwner, factory)[AuthViewModel::class.java]

    private val sessionManager = SessionManager(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(layouts[viewType], parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == 1) {
            holder.itemView.findViewById<AppCompatButton>(R.id.loginButton).setOnClickListener {
                // Assuming you have EditText fields for username and password in your fragment_login layout
                val username = holder.itemView.findViewById<EditText>(R.id.username).text.toString()
                val password = holder.itemView.findViewById<EditText>(R.id.password).text.toString()
                val progressBar=holder.itemView.findViewById<ProgressBar>(R.id.progressBar)

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(context, "Please fill in both email and password", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.login(username, password)
                    // To save login status when the user successfully logs in
                    progressBar.visibility=View.VISIBLE
                }
                // Optionally observe the loginResponse LiveData to react to the result
                viewModel.loginResponse.observe(context as LifecycleOwner, Observer { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            progressBar.visibility=View.GONE
                            // Handle success, e.g., navigate to HomeContainerActivity
                            viewModel.saveAccessToken(resource.data.access!!)
                            sessionManager.saveUserId(resource.data.userId.toString())
                            sessionManager.saveUserRole(resource.data.role!!)
                            val intent = Intent(context, HomeContainerActivity::class.java)
                            context.startActivity(intent)

                        }
                        is Resource.Failure -> {
                            // Handle failure, e.g., show an error message
                            Toast.makeText(context, "Login failed: ${resource.message}", Toast.LENGTH_SHORT).show()
                            progressBar.visibility=View.GONE
                        }
                        is Resource.Loading -> {
                            // Show loading state if needed
                            progressBar.visibility=View.VISIBLE
                        }
                    }
                })
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