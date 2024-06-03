package com.example.mysoreprintersproject.app.netsale

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.mysoreprintersproject.R
import com.example.mysoreprintersproject.app.attendance.AttendanceActivity
import com.example.mysoreprintersproject.app.dailycollections.DailyCollectionActivity
import com.example.mysoreprintersproject.app.dailyworkingsummryfragment.DailyWorkingSummaryActivity
import com.example.mysoreprintersproject.app.homecontainer.HomeContainerActivity
import com.example.mysoreprintersproject.app.homefragment.HomeActivity
import com.example.mysoreprintersproject.app.supplyreport.SupplyReportActivity
import com.example.mysoreprintersproject.app.supplyreport.SupplyReportAdapter
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.navigation.NavigationView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [NetSaleFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NetSaleFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var barChart: BarChart

    private lateinit var drawerLayout: DrawerLayout

    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_net_sale, container, false).apply {

            drawerLayout = findViewById(R.id.drawer_layout)

            val navigatioViewIcon: ImageView =findViewById(R.id.imageSettings)
            navigatioViewIcon.setOnClickListener {
                drawerLayout.openDrawer(GravityCompat.START)
            }

            navigationView=findViewById(R.id.navigationView)

            navigationView.setNavigationItemSelectedListener { item ->
                when (item.itemId) {

                    R.id.nav_dashboard->{
                        startActivity(Intent(requireActivity(), HomeContainerActivity::class.java))

                    }

                    R.id.nav_attendance -> {
                        startActivity(Intent(requireActivity(), AttendanceActivity::class.java))

                    }


                    R.id.nav_daily_work_summary -> {
                        startActivity(Intent(requireActivity(), DailyWorkingSummaryActivity::class.java))
                    }
                    R.id.nav_collections_performance -> {
                        startActivity(
                            Intent(requireActivity(),
                                DailyCollectionActivity::class.java)
                        )
                    }
                    R.id.nav_collections_report -> {
                        startActivity(Intent(requireActivity(), DailyCollectionActivity::class.java))
                    }
                    R.id.nav_supply_reports -> {
                        startActivity(Intent(requireActivity(), SupplyReportActivity::class.java))
                    }
                    R.id.nav_net_sales_report -> {
                        startActivity(Intent(requireActivity(), NetSaleActivity::class.java))
                    }
                    // Add other cases for different activities
                    else -> {
                        Log.d("NavigationDrawer", "Unhandled item clicked: ${item.itemId}")
                    }
                }
                drawerLayout.closeDrawers()
                true
            }


            barChart=findViewById(R.id.bar_chart)
            setupBarChart()

            val recyclerView: RecyclerView = findViewById(R.id.recyclerview)
            recyclerView.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
            recyclerView.adapter = NetSaleAdapter()


            // Attach LinearSnapHelper
            val snapHelper = LinearSnapHelper()
            snapHelper.attachToRecyclerView(recyclerView)
        }
    }

    private fun setupBarChart() {
        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(0f, 80f))
        entries.add(BarEntry(1f, 60f))
        entries.add(BarEntry(2f, 30f))
        entries.add(BarEntry(3f, 50f))
        entries.add(BarEntry(4f, 70f))
        entries.add(BarEntry(5f, 90f))
        entries.add(BarEntry(6f, 50f))

        val barDataSet = BarDataSet(entries, "Net Sale Amount")
        barDataSet.color = resources.getColor(R.color.netshadecolor, null)
        barDataSet.valueTextColor = android.graphics.Color.BLACK
        barDataSet.valueTextSize = 16f

        val barData = BarData(barDataSet)
        // Set the width of the bars
        barData.barWidth = 0.1f
        barChart.data = barData
        barChart.description.isEnabled = false
        barChart.setDrawGridBackground(false) // Remove grid background
        barChart.setDrawBorders(false)

        // Customize X-axis labels
        val xAxis = barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul"))
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.setDrawGridLines(false)

        // Customize Y-axis
        val leftAxis = barChart.axisLeft
        leftAxis.axisMinimum = 0f // Minimum value for the Y-axis
        leftAxis.axisMaximum = 100f // Maximum value for the Y-axis
        leftAxis.setDrawGridLines(false)
        leftAxis.setDrawAxisLine(false)

        val rightAxis = barChart.axisRight
        rightAxis.isEnabled = false

        barChart.animateY(1000)
        barChart.invalidate()
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment NetSaleFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NetSaleFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}