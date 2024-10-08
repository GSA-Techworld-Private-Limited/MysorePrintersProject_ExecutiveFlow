package com.example.mysoreprintersproject.app.homefragment

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
import com.example.mysoreprintersproject.responses.AttendanceGraph
import com.example.mysoreprintersproject.responses.ExecutiveDashboard
import com.example.mysoreprintersproject.responses.ProfileResponses
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Response

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var barChart: BarChart
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var periodSpinner: Spinner
    private lateinit var chartSpinne: Spinner
    private lateinit var sessionManager: SessionManager
    private var selectedPeriod: String = ""

    private lateinit var txthours:TextView
    private lateinit var visits:TextView
    private lateinit var kilomeres:TextView
    private lateinit var totalhoursworked:TextView

    private lateinit var profileImage:ImageView

    private lateinit var donutChartView:DonutChartView
    private lateinit var notificationIcon:ImageView

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        sessionManager = SessionManager(requireActivity())

        barChart = requireView().findViewById(R.id.bar_chart)
        //setupBarChart()

         donutChartView = requireView().findViewById(R.id.donutChart)
        drawerLayout = requireView().findViewById(R.id.drawer_layout)
        navigationView = requireView().findViewById(R.id.navigationView)
        periodSpinner = requireView().findViewById(R.id.spinnerMonth)
        chartSpinne= requireView().findViewById(R.id.spinner)

        profileImage= requireView().findViewById(R.id.imageSettings2)

        txthours = requireView().findViewById(R.id.hours)
        visits = requireView().findViewById(R.id.locations)
        kilomeres = requireView().findViewById(R.id.kilometers)
        totalhoursworked = requireView().findViewById(R.id.totalhoursworked)

        notificationIcon=requireView().findViewById(R.id.imageSettings1)

        setupNavigationView()
        setupSpinner()
        getExecutiveDashboard() // Initially fetch data

        getExecutiveProfile()

        notificationIcon.setOnClickListener {
            val i=Intent(requireActivity(),NotificationActivity::class.java)
            startActivity(i)
        }

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
                R.id.nav_collection_summary -> startActivity(Intent(requireActivity(),CollectionSummaryReportActivity::class.java))
                R.id.nav_lprmanagement -> startActivity(Intent(requireActivity(),LPRManagementActivity::class.java))
                R.id.nav_daily_work_summary -> startActivity(Intent(requireActivity(),DailyWorkSummaryActivity::class.java))
                R.id.nav_collections_report -> startActivity(Intent(requireActivity(), DailyCollectionActivity::class.java))
                R.id.nav_supply_reports -> startActivity(Intent(requireActivity(), SupplyReportActivity::class.java))
                R.id.nav_net_sales_report -> startActivity(Intent(requireActivity(), NetSaleActivity::class.java))
                R.id.nav_notifications -> startActivity(Intent(requireActivity(),NotificationActivity::class.java))
                R.id.nav_logout ->{
                    sessionManager.logout()
                    sessionManager.clearSession()
                    startActivity(Intent(requireActivity(),SplashScreenActivity::class.java))
                    requireActivity().finishAffinity()
                }
                else -> Log.d("NavigationDrawer", "Unhandled item clicked: ${item.itemId}")
            }
            drawerLayout.closeDrawers()
            true
        }
    }

    private fun setupSpinner() {
        // Set the default selection to 6 months
        periodSpinner.setSelection(0) // Assuming the first item in the Spinner is "6 months"

        chartSpinne.setSelection(0)
        periodSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (view == null) return // Handle the null case

                val selectedItem = parent.getItemAtPosition(position).toString()
                selectedPeriod = selectedItem.filter { it.isDigit() }

                // Fetch the dashboard data based on the selected period
                getExecutiveDashboard()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Optionally handle this case
            }
        }


        chartSpinne.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (view == null) return // Handle the null case

                val selectedItem = parent.getItemAtPosition(position).toString()
                selectedPeriod = selectedItem.filter { it.isDigit() }

                // Fetch the dashboard data based on the selected period
                getExecutiveDashboard()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Optionally handle this case
            }
        }
    }


    private fun setupBarChart(attendanceGraph: List<AttendanceGraph>) {
        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()

        // Populate the entries and labels from the attendanceGraph list
        attendanceGraph.forEachIndexed { index, data ->
            entries.add(BarEntry(index.toFloat(), data.attendancePercentage?.toFloat() ?: 0f))
            labels.add(data.month ?: "")
        }

        val barDataSet = BarDataSet(entries, "Average Attendance").apply {
            color = resources.getColor(R.color.shade_blue, null)
            valueTextColor = android.graphics.Color.BLACK
            valueTextSize = 16f
        }

        val barData = BarData(barDataSet).apply {
            barWidth = 0.4f // Adjust bar width as needed
        }

        barChart.apply {
            data = barData
            description.isEnabled = false
            setDrawGridBackground(false)
            setDrawBorders(false)

            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(labels)
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setDrawGridLines(false)
            }

            axisLeft.apply {
                axisMinimum = 0f
                axisMaximum = 100f
                setDrawGridLines(false)
                setDrawAxisLine(false)
            }

            axisRight.isEnabled = false

            animateY(1000)
            invalidate() // Refresh the chart
        }
    }


    private fun getExecutiveDashboard() {
        val serviceGenerator = APIManager.apiInterface
        val accessToken = sessionManager.fetchAuthToken()
        val authorization = "Bearer $accessToken"
        val id = sessionManager.fetchUserId()!!

        serviceGenerator.getExecutiveDashboard(authorization, id, selectedPeriod)
            .enqueue(object : retrofit2.Callback<ExecutiveDashboard> {
                override fun onResponse(call: Call<ExecutiveDashboard>, response: Response<ExecutiveDashboard>) {

                    val dashboardResponses = response.body()
                    if (dashboardResponses != null) {
                        val totalHoursWorked = dashboardResponses.totalHoursWorked
                        sessionManager.saveKm(dashboardResponses.totalDistance!!)
                        if (totalHoursWorked != null) {
                            // Split the string into hours and minutes
                            val timeParts = totalHoursWorked.split(":")
                            val hours = timeParts[0].toInt()
                            val minutes = timeParts[1].toInt()

                            // Create a string builder to construct the final output
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

                            // If neither hours nor minutes, handle edge case
                            if (hours == 0 && minutes == 0) {
                                formattedTime.append("Less than a minute")
                            }

                            // Display the formatted time in the TextView
                            txthours.text = formattedTime.toString()

                            kilomeres.text = dashboardResponses.totalDistance
                            visits.text = dashboardResponses.locationsVisitedCount.toString()
                            totalhoursworked.text = formattedTime.toString()

                            // Calculate total possible hours for the selected period
                            val monthsSelected = selectedPeriod.toIntOrNull() ?: 0
                            val totalPossibleHours =
                                monthsSelected * 24 * 30 // Assuming 24 hours a day and 30 days a month

                            // Calculate worked hours as a float
                            val workedHours =
                                hours + (minutes / 60.0) // Convert minutes to fractional hours

                            // Calculate the percentage of worked hours
                            val possibleHours = totalPossibleHours.toFloat()
                            val workedPercentage = (workedHours / possibleHours) * 100

                            // Update the donut chart with the calculated percentage

                            donutChartView.setCompletedPercentage(workedPercentage.toFloat())
                        } else {
                            // Handle the null case appropriately
                            txthours.text = "No Work"
                            totalhoursworked.text = "No Work"
                        }


                        // Update the bar chart with the attendance graph
                        val attendanceGraph = dashboardResponses.attendanceGraph
                        if (attendanceGraph.isNotEmpty()) {
                            setupBarChart(attendanceGraph)
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "No Attendance Data, Available",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
//                    else{
//                        Toast.makeText(requireContext(),"No Data, Available",Toast.LENGTH_SHORT).show()
//                    }

                }

                override fun onFailure(call: Call<ExecutiveDashboard>, t: Throwable) {
                    Toast.makeText(requireActivity(), "Error fetching data", Toast.LENGTH_SHORT).show()
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
}
