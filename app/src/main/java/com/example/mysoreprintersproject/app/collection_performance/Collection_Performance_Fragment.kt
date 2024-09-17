package com.example.mysoreprintersproject.app.collection_performance

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.example.mysoreprintersproject.R
import com.example.mysoreprintersproject.app.CollectionSummaryReport.CollectionSummaryReportActivity
import com.example.mysoreprintersproject.app.DailyWorkSummaryActivity
import com.example.mysoreprintersproject.app.SplashScreenActivity
import com.example.mysoreprintersproject.app.attendance.AttendanceActivity
import com.example.mysoreprintersproject.app.dailycollections.DailyCollectionActivity
import com.example.mysoreprintersproject.app.dailyworkingsummryfragment.DailyWorkingSummaryActivity
import com.example.mysoreprintersproject.app.homecontainer.HomeContainerActivity
import com.example.mysoreprintersproject.app.netsale.NetSaleActivity
import com.example.mysoreprintersproject.app.notification.NotificationActivity
import com.example.mysoreprintersproject.app.supplyreport.SupplyReportActivity
import com.example.mysoreprintersproject.network.APIManager
import com.example.mysoreprintersproject.network.DataSource
import com.example.mysoreprintersproject.network.SessionManager
import com.example.mysoreprintersproject.responses.CollectionReport
import com.example.mysoreprintersproject.responses.ProfileResponses
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class Collection_Performance_Fragment : Fragment(R.layout.fragment_collection__performance_) {

    private lateinit var periodSpinner: Spinner
    private lateinit var apiService:DataSource
    private lateinit var sessionManager: SessionManager
    private lateinit var paymentMethod: String
    private lateinit var submitButton:AppCompatButton


    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var notificationIcon:ImageView
    private lateinit var btnCancelled:AppCompatButton
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sessionManager=SessionManager(requireActivity())

        drawerLayout = requireView().findViewById(R.id.drawer_layout)
        navigationView = requireView().findViewById(R.id.navigationView)
        apiService=APIManager.apiInterface
        periodSpinner = requireView().findViewById(R.id.spinnerCash)
        submitButton=requireView().findViewById(R.id.btnSubmit)

        btnCancelled=requireView().findViewById(R.id.btnCancleed)
        notificationIcon=requireView().findViewById(R.id.imageSettings1)
        setupNavigationView()


        getExecutiveProfile()


        val userType = sessionManager.fetchUserRole() // Fetch user type
        val headerView = navigationView.getHeaderView(0) // Get the header view
        val headerTitle: TextView = headerView.findViewById(R.id.nav_header_title) // Assuming you have this TextView in your header layout

// Set the header title based on the user type
        when (userType) {
            "RM" -> headerTitle.text = "Regional Manager"
            "DGM" -> headerTitle.text = "Deputy General Manager"
            "GM" -> headerTitle.text = "General Manager"
        }

        val menu = navigationView.menu

// Hide certain menu items based on the user type
        when (userType) {
            "RM", "DGM", "GM" -> {
                menu.findItem(R.id.nav_lprmanagement).isVisible = false
                menu.findItem(R.id.nav_daily_work_summary).isVisible = false
                menu.findItem(R.id.nav_collections_performance).isVisible = false
            }
        }



        val agentCode:EditText=requireView().findViewById(R.id.editCode)
        val agentName:EditText=requireView().findViewById(R.id.editName)
        val instrumentNumber:EditText=requireView().findViewById(R.id.editinstumental)
        val amountCollected:EditText=requireView().findViewById(R.id.editAmountCollected)
        // Set the default selection to 6 months
        periodSpinner.setSelection(0) // Assuming the first item in the Spinner is "6 months"

        periodSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (view == null) return // Handle the null case

                 paymentMethod = parent.getItemAtPosition(position).toString()

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Optionally handle this case
            }




        }


        submitButton.setOnClickListener {
            val name = agentName.text.toString().trim()
            val code = agentCode.text.toString().trim()
            val method = paymentMethod.toString().trim()
            val number = instrumentNumber.text.toString().trim()
            val amount = amountCollected.text.toString().trim()

            if (name.isNotEmpty() && code.isNotEmpty() && method.isNotEmpty() && number.isNotEmpty() && amount.isNotEmpty()) {
                postCollectionReport(code, name, method, number, amount)
            } else {
                Toast.makeText(requireActivity(), "All Fields are Mandatory", Toast.LENGTH_SHORT).show()
            }
        }

        btnCancelled.setOnClickListener {
            val i=Intent(requireActivity(),HomeContainerActivity::class.java)
            startActivity(i)
            requireActivity().finish()
        }

        notificationIcon.setOnClickListener {
            val i=Intent(requireActivity(),NotificationActivity::class.java)
            startActivity(i)
        }




    }



    private fun setupNavigationView() {
        val navigationViewIcon: ImageView = requireView().findViewById(R.id.imageSettings)
        navigationViewIcon.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> startActivity(Intent(requireActivity(), HomeContainerActivity::class.java))
                R.id.nav_attendance -> startActivity(Intent(requireActivity(), AttendanceActivity::class.java))
                R.id.nav_work_summary -> startActivity(Intent(requireActivity(), DailyWorkingSummaryActivity::class.java))
                R.id.nav_collections_performance -> startActivity(Intent(requireActivity(), CollectionPerformanceActivity::class.java))
                R.id.nav_collection_summary -> startActivity(
                    Intent(requireActivity(),
                        CollectionSummaryReportActivity::class.java)
                )
                R.id.nav_daily_work_summary -> startActivity(Intent(requireActivity(),
                    DailyWorkSummaryActivity::class.java))
                R.id.nav_collections_report -> startActivity(Intent(requireActivity(), DailyCollectionActivity::class.java))
                R.id.nav_supply_reports -> startActivity(Intent(requireActivity(), SupplyReportActivity::class.java))
                R.id.nav_net_sales_report -> startActivity(Intent(requireActivity(), NetSaleActivity::class.java))
                R.id.nav_notifications -> startActivity(Intent(requireActivity(),
                    NotificationActivity::class.java))
                R.id.nav_logout ->{
                    sessionManager.logout()
                    sessionManager.clearSession()
                    startActivity(Intent(requireActivity(), SplashScreenActivity::class.java))
                    requireActivity().finishAffinity()
                }
                else -> Log.d("NavigationDrawer", "Unhandled item clicked: ${item.itemId}")
            }
            drawerLayout.closeDrawers()
            true
        }
    }


    private fun postCollectionReport(agendCode:String,agentName:String,paymentMethod:String,instrumentNumber:String,amount:String){
        val id=sessionManager.fetchUserId()!!
        val token=sessionManager.fetchAuthToken()
        val authorization="Bearer $token"
        val postRequests= CollectionReport(agentName,agendCode,paymentMethod,instrumentNumber,amount,id.toInt())
        val call = apiService.sendCollectionReport(authorization,postRequests)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {

                if (response.isSuccessful) {

                    Toast.makeText(requireActivity(),"Collection Report Sent Successfully",Toast.LENGTH_SHORT).show()
                    moveToHomeContainer()
                } else {
                    // Handle other HTTP error codes if needed
                    Toast.makeText(requireActivity(), "Something Went Wrong!!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Handle failure (e.g., network failure, timeout)
                Toast.makeText(requireActivity(), "Something Went Wrong!!", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun getExecutiveProfile() {
        val serviceGenerator = APIManager.apiInterface
        val accessToken = sessionManager.fetchAuthToken()
        val authorization = "Bearer $accessToken"
        val id = sessionManager.fetchUserId()!!

        serviceGenerator.getProfileOfExecutive(authorization, id.toInt())
            .enqueue(object : retrofit2.Callback<ProfileResponses> {
                override fun onResponse(call: Call<ProfileResponses>, response: Response<ProfileResponses>) {
                    val profileResponses = response.body()

                    if(profileResponses!=null){

                        val profileImage:ImageView=requireView().findViewById(R.id.imageSettings2)
                        val image=profileResponses.profileImage
                        val file=APIManager.getImageUrl(image!!)
                        Glide.with(requireActivity()).load(file).into(profileImage)
                    }
                }

                override fun onFailure(call: Call<ProfileResponses>, t: Throwable) {
                    Toast.makeText(requireActivity(), "Error fetching data", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun moveToHomeContainer(){
        val i=Intent(requireActivity(),HomeContainerActivity::class.java)
        startActivity(i)
        requireActivity().finish()
    }
}