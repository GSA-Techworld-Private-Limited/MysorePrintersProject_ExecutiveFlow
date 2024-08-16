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
import com.example.mysoreprintersproject.R
import com.example.mysoreprintersproject.app.attendance.AttendanceActivity
import com.example.mysoreprintersproject.app.dailycollections.DailyCollectionActivity
import com.example.mysoreprintersproject.app.dailyworkingsummryfragment.DailyWorkingSummaryActivity
import com.example.mysoreprintersproject.app.homecontainer.HomeContainerActivity
import com.example.mysoreprintersproject.app.netsale.NetSaleActivity
import com.example.mysoreprintersproject.app.supplyreport.SupplyReportActivity
import com.example.mysoreprintersproject.network.APIManager
import com.example.mysoreprintersproject.network.SessionManager
import com.example.mysoreprintersproject.responses.ExecutiveDashboard
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

        locationsTextView = requireView().findViewById(R.id.locationsTextView)

        val navigationViewIcon: ImageView = requireView().findViewById(R.id.imageSettings)
        navigationViewIcon.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        setupSpinner()
        setupNavigationView()


        getSummaryResponses()
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
                R.id.nav_daily_work_summary -> startActivity(Intent(requireActivity(), DailyWorkingSummaryActivity::class.java))
                R.id.nav_collections_performance -> startActivity(Intent(requireActivity(), DailyCollectionActivity::class.java))
                R.id.nav_collections_report -> startActivity(Intent(requireActivity(), DailyCollectionActivity::class.java))
                R.id.nav_supply_reports -> startActivity(Intent(requireActivity(), SupplyReportActivity::class.java))
                R.id.nav_net_sales_report -> startActivity(Intent(requireActivity(), NetSaleActivity::class.java))
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
                        val totalHoursWorked = summaryResponses.total_hours_worked
                        totalplacevisitedCount.text=summaryResponses.locations_visited_count.toString()
                        distanceTravelledText.text=summaryResponses.total_distance





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
                        displayLocations(summaryResponses.locations_visited_details)

                    }
                }

                override fun onFailure(call: Call<SummaryReportResponses>, t: Throwable) {

                    Toast.makeText(requireActivity(), "Error fetching data", Toast.LENGTH_SHORT).show()
                }
            })

    }


    private fun displayLocations(locationsVisitedDetails: Map<String, Int>?) {
        if (locationsVisitedDetails != null) {
            val formattedString = StringBuilder()
            locationsVisitedDetails.entries.forEachIndexed { index, entry ->
                formattedString.append("${index + 1}. ${entry.key}: ${entry.value} visits\n")
            }
            locationsTextView.text = formattedString.toString()
        } else {
            // Handle the case where locationsVisitedDetails is null
            locationsTextView.text = "No location details available."
        }
    }

}
