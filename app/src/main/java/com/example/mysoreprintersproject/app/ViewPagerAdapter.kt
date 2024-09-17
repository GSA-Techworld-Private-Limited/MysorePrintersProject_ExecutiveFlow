package com.example.mysoreprintersproject.app
import android.content.Context
import android.content.Intent
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
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
import com.example.mysoreprintersproject.network.FirebaseMessageReceiver
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
            val passwordEditText = holder.itemView.findViewById<EditText>(R.id.password)
            val loginButton = holder.itemView.findViewById<AppCompatButton>(R.id.loginButton)
            val progressBar = holder.itemView.findViewById<ProgressBar>(R.id.progressBar)

            // Variables to handle password visibility state
            var isPasswordVisible = false

            // Set the initial eye icon (closed by default)
            passwordEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_open, 0)

            // Listener for the login button click
            loginButton.setOnClickListener {
                val username = holder.itemView.findViewById<EditText>(R.id.username).text.toString()
                val password = passwordEditText.text.toString()
                var fcmToken = ""

                // Retrieve the token from shared preferences
                val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
                val token = sharedPreferences.getString(FirebaseMessageReceiver.FCM_TOKEN, "")
                Log.d("FCM TOKEN", token ?: "")

                // Use the token in your API request
                if (!token.isNullOrEmpty()) {
                    fcmToken = token
                    Log.d("FCM TOKEN", fcmToken)
                }

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(context, "Please fill in both email and password", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.login(username, password, fcmToken)
                    progressBar.visibility = View.VISIBLE
                }

                viewModel.loginResponse.observe(context as LifecycleOwner, Observer { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            progressBar.visibility = View.GONE
                            viewModel.saveAccessToken(resource.data.access!!)
                            sessionManager.saveAuthToken(resource.data.access!!)
                            sessionManager.saveUserId(resource.data.userId.toString())
                            sessionManager.saveUserRole(resource.data.role!!)
                            val intent = Intent(context, HomeContainerActivity::class.java)
                            context.startActivity(intent)
                            (context as? SplashScreenActivity)?.finish()
                        }
                        is Resource.Failure -> {
                            progressBar.visibility = View.GONE
                            if (resource.errorCode == 400) {
                                Toast.makeText(context, "User does not exist", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Login failed: ${resource.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                        is Resource.Loading -> {
                            progressBar.visibility = View.VISIBLE
                        }
                    }
                })
            }

            // Set up touch listener for the password visibility toggle
            passwordEditText.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= (passwordEditText.right - passwordEditText.compoundDrawables[2].bounds.width())) {
                        // Toggle password visibility
                        if (isPasswordVisible) {
                            // Hide password
                            passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                            passwordEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_open, 0)
                        } else {
                            // Show password
                            passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                            passwordEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_closed, 0)
                        }
                        // Move cursor to the end of the text
                        passwordEditText.setSelection(passwordEditText.text.length)
                        // Toggle the visibility state
                        isPasswordVisible = !isPasswordVisible
                        return@setOnTouchListener true
                    }
                }
                false
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