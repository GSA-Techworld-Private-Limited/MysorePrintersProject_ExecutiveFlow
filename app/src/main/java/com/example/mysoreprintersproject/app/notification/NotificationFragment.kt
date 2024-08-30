package com.example.mysoreprintersproject.app.notification

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mysoreprintersproject.R
import com.example.mysoreprintersproject.app.CollectionSummaryReport.CollectionSummaryReportActivity
import com.example.mysoreprintersproject.app.CollectionSummaryReport.CollectionSummaryReportAdapter
import com.example.mysoreprintersproject.app.DailyWorkSummaryActivity
import com.example.mysoreprintersproject.app.SplashScreenActivity
import com.example.mysoreprintersproject.app.attendance.AttendanceActivity
import com.example.mysoreprintersproject.app.collection_performance.CollectionPerformanceActivity
import com.example.mysoreprintersproject.app.dailycollections.DailyCollectionActivity
import com.example.mysoreprintersproject.app.dailyworkingsummryfragment.DailyWorkingSummaryActivity
import com.example.mysoreprintersproject.app.homecontainer.HomeContainerActivity
import com.example.mysoreprintersproject.app.netsale.NetSaleActivity
import com.example.mysoreprintersproject.app.supplyreport.SupplyReportActivity
import com.example.mysoreprintersproject.network.APIManager
import com.example.mysoreprintersproject.network.SessionManager
import com.example.mysoreprintersproject.responses.CollectionSummaryReportResponses
import com.example.mysoreprintersproject.responses.NotificationResponses
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class NotificationFragment : Fragment() {


    private lateinit var sessionManager:SessionManager

    private lateinit var summaryResponses: List<NotificationResponses>
    private lateinit var recyclerView: RecyclerView

    private lateinit var searchBar: EditText
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sessionManager=SessionManager(requireActivity())
        recyclerView=requireView().findViewById(R.id.recyclerview)

        drawerLayout = requireView().findViewById(R.id.drawer_layout)
        navigationView = requireView().findViewById(R.id.navigationView)
        setupNavigationView()
        getNotification()

        searchBar=requireView().findViewById(R.id.search_bar)


        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchCollectionSummary(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
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
                R.id.nav_daily_work_summary -> startActivity(
                    Intent(requireActivity(),
                        DailyWorkSummaryActivity::class.java)
                )
                R.id.nav_collections_report -> startActivity(Intent(requireActivity(), DailyCollectionActivity::class.java))
                R.id.nav_supply_reports -> startActivity(Intent(requireActivity(), SupplyReportActivity::class.java))
                R.id.nav_net_sales_report -> startActivity(Intent(requireActivity(), NetSaleActivity::class.java))
                R.id.nav_notifications -> startActivity(Intent(requireActivity(),NotificationActivity::class.java))
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
    private fun getNotification() {
        val serviceGenerator = APIManager.apiInterface
        val accessToken = sessionManager.fetchAuthToken()
        val authorization = "Bearer $accessToken"
        val id = sessionManager.fetchUserId()!!

        serviceGenerator.getNotifications(authorization)
            .enqueue(object : Callback<List<NotificationResponses>> {
                override fun onResponse(
                    call: Call<List<NotificationResponses>>,
                    response: Response<List<NotificationResponses>>
                ) {
                    if (response.isSuccessful) {
                        summaryResponses = response.body()!!
                        recyclerView.layoutManager = LinearLayoutManager(
                            requireActivity(),
                            LinearLayoutManager.VERTICAL,
                            false
                        )
                        recyclerView.adapter = NotificationAdapter(summaryResponses,sessionManager)
                    } else {
                        Toast.makeText(requireActivity(), "Failed to retrieve data", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<NotificationResponses>>, t: Throwable) {
                    Log.e("CollectionSummaryReport", "Error fetching data: ${t.message}", t)
                    Toast.makeText(requireActivity(), "Error fetching data: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }


    private fun searchCollectionSummary(query: String) {
        if (!::summaryResponses.isInitialized) {
            // Toast.makeText(requireActivity(), "", Toast.LENGTH_SHORT).show()
            return
        }

        val filteredList = summaryResponses.filter { summary ->
            summary.DateTime!!.contains(query, ignoreCase = true) ||
                    summary.title!!.contains(query, ignoreCase = true) ||
                    summary.content!!.contains(query, ignoreCase = true) ||
                    summary.id.toString().contains(query, ignoreCase = true) ||
                    summary.notificationTime.toString().contains(query, ignoreCase = true)||
                    summary.role.toString().contains(query, ignoreCase = true) ||
                    summary.status.toString().contains(query, ignoreCase = true)
        }

        recyclerView.adapter = NotificationAdapter(filteredList,sessionManager)
    }

}