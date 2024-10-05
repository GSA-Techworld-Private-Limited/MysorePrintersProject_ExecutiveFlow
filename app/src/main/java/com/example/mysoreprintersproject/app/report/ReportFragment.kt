package com.example.mysoreprintersproject.app.report

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.mysoreprintersproject.R
import com.example.mysoreprintersproject.app.CollectionSummaryReport.CollectionSummaryReportActivity
import com.example.mysoreprintersproject.app.DailyWorkSummaryActivity
import com.example.mysoreprintersproject.app.SplashScreenActivity
import com.example.mysoreprintersproject.app.attendance.AttendanceActivity
import com.example.mysoreprintersproject.app.collection_performance.CollectionPerformanceActivity
import com.example.mysoreprintersproject.app.dailycollections.DailyCollectionActivity
import com.example.mysoreprintersproject.app.dailyworkingsummryfragment.DailyWorkingSummaryActivity
import com.example.mysoreprintersproject.app.homecontainer.HomeContainerActivity
import com.example.mysoreprintersproject.app.lprmanagement.LPRManagementActivity
import com.example.mysoreprintersproject.app.netsale.NetSaleActivity
import com.example.mysoreprintersproject.app.notification.NotificationActivity
import com.example.mysoreprintersproject.app.supplyreport.SupplyReportActivity
import com.example.mysoreprintersproject.network.APIManager
import com.example.mysoreprintersproject.network.SessionManager
import com.example.mysoreprintersproject.responses.ExecutiveDashboard
import com.example.mysoreprintersproject.responses.ProfileResponses
import com.example.mysoreprintersproject.responses.SummaryReportResponses
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Response

class ReportFragment : Fragment(R.layout.fragment_report) {

    private var selectedPeriod: String = ""
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var sessionManager: SessionManager
    private lateinit var navigationView: NavigationView
    private lateinit var periodSpinner: Spinner

    private lateinit var distanceTravelledText:TextView
    private lateinit var totalplacevisitedCount:TextView
    private lateinit var mainpageHoursVisited:TextView
    private lateinit var totalHoursText:TextView

    private lateinit var locationsTextView: TextView


    private lateinit var profileImage:ImageView
    private lateinit var notificationIcon:ImageView
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        sessionManager = SessionManager(requireActivity())
        drawerLayout = requireView().findViewById(R.id.drawer_layout)
        periodSpinner = requireView().findViewById(R.id.spinnerMonth)
        navigationView = requireView().findViewById(R.id.navigationView)

        distanceTravelledText=requireView().findViewById(R.id.distancetravelled)
        totalplacevisitedCount=requireView().findViewById(R.id.totalplacevisitedcount)

        mainpageHoursVisited=requireView().findViewById(R.id.mainpagehoursText)
        totalHoursText=requireView().findViewById(R.id.totalhours)


        profileImage= requireView().findViewById(R.id.imageSettings2)
        locationsTextView = requireView().findViewById(R.id.locationsTextView)
        notificationIcon=requireView().findViewById(R.id.imageSettings1)

        val navigationViewIcon: ImageView = requireView().findViewById(R.id.imageSettings)
        navigationViewIcon.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        setupSpinner()
        setupNavigationView()


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

        getSummaryResponses()


        getExecutiveProfile()

        notificationIcon.setOnClickListener {
            val i=Intent(requireActivity(),NotificationActivity::class.java)
            startActivity(i)
        }
    }

    private fun setupSpinner() {
        periodSpinner.setSelection(0) // Assuming the first item in the Spinner is "6 months"

        periodSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (view == null) return

                val selectedItem = parent.getItemAtPosition(position).toString()
                selectedPeriod = selectedItem.filter { it.isDigit() }

              getSummaryResponses()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Optionally handle this case
            }
        }
    }

    private fun setupNavigationView() {
        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> startActivity(Intent(requireActivity(), HomeContainerActivity::class.java))
                R.id.nav_attendance -> startActivity(Intent(requireActivity(), AttendanceActivity::class.java))
                R.id.nav_work_summary -> startActivity(Intent(requireActivity(), DailyWorkingSummaryActivity::class.java))
                R.id.nav_collections_performance -> startActivity(Intent(requireActivity(), CollectionPerformanceActivity::class.java))
                R.id.nav_collection_summary -> startActivity(Intent(requireActivity(),
                    CollectionSummaryReportActivity::class.java))
                R.id.nav_daily_work_summary -> startActivity(Intent(requireActivity(),
                    DailyWorkSummaryActivity::class.java))
                R.id.nav_lprmanagement -> startActivity(Intent(requireActivity(),
                    LPRManagementActivity::class.java))
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

    private fun getSummaryResponses() {
        val serviceGenerator = APIManager.apiInterface
        val accessToken = sessionManager.fetchAuthToken()
        val authorization = "Bearer $accessToken"
        val id = sessionManager.fetchUserId()!!

        serviceGenerator.getSummaryReport(authorization, id, selectedPeriod)
            .enqueue(object : retrofit2.Callback<SummaryReportResponses> {
                override fun onResponse(call: Call<SummaryReportResponses>, response: Response<SummaryReportResponses>) {
                    val summaryResponses = response.body()
                    // Handle the response as needed

                    if(summaryResponses!=null){
                        val totalHoursWorked = summaryResponses.totalHoursWorked
                        totalplacevisitedCount.text=summaryResponses.locationsVisitedCount.toString()
                        distanceTravelledText.text=summaryResponses.totalDistance





                        if (!totalHoursWorked.isNullOrEmpty()) {
                            val timeParts = totalHoursWorked.split(":")
                            val hours = timeParts[0].toInt()
                            val minutes = timeParts[1].toInt()

                            val formattedTime = StringBuilder()

                            if (hours > 0) {
                                formattedTime.append("$hours hour")
                                if (hours > 1) {
                                    formattedTime.append("s")
                                }
                            }

                            if (minutes > 0) {
                                if (hours > 0) {
                                    formattedTime.append(" ") // add space between hours and minutes
                                }
                                formattedTime.append("$minutes minute")
                                if (minutes > 1) {
                                    formattedTime.append("s")
                                }
                            }

                            if (hours == 0 && minutes == 0) {
                                formattedTime.append("Less than a minute")
                            }

                            mainpageHoursVisited.text = formattedTime
                            totalHoursText.text = formattedTime

                        } else {
                            mainpageHoursVisited.text = "No Work"
                            totalHoursText.text = "No Work"
                        }


                        // Display locations visited details
                        displayLocations(summaryResponses.locationsVisited)

                    }
                }

                override fun onFailure(call: Call<SummaryReportResponses>, t: Throwable) {

                    Toast.makeText(requireActivity(), "Error fetching data", Toast.LENGTH_SHORT).show()
                }
            })

    }


    private fun displayLocations(locationsVisited: ArrayList<String>?) {
        if (locationsVisited != null && locationsVisited.isNotEmpty()) {
            val formattedString = StringBuilder()
            locationsVisited.forEachIndexed { index, location ->
                // Adding each location to the formatted string
                formattedString.append("${index + 1}. $location\n")
            }
            // Display the formatted locations in the TextView
            locationsTextView.text = formattedString.toString().trim()
        } else {
            // Handle the case where locationsVisited is null or empty
            locationsTextView.text = "No location details available."
        }
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

                    if (profileResponses != null && isAdded) { // Check if fragment is added
                        val image = profileResponses.profileImage
                        val file = APIManager.getImageUrl(image!!)

                        // Make sure the fragment is still attached to an activity
                        if (isAdded && activity != null) {
                            Glide.with(requireActivity())
                                .load(file)
                                .into(profileImage)
                        }
                    }
                }

                override fun onFailure(call: Call<ProfileResponses>, t: Throwable) {
                    if (isAdded && activity != null) {
                        Toast.makeText(requireActivity(), "Error fetching data", Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }

}
