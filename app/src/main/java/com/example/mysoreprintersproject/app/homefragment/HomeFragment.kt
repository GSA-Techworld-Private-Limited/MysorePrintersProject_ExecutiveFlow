package com.example.mysoreprintersproject.app.homefragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.mysoreprintersproject.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var barChart: BarChart

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
//
//
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false).apply {
            barChart = findViewById(R.id.bar_chart)
            setupBarChart()

            val navigatioViewIcon:ImageView=findViewById(R.id.imageSettings)
            navigatioViewIcon.setOnClickListener {

            }
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

        val barDataSet = BarDataSet(entries, "Average Attendance")
        barDataSet.color = resources.getColor(R.color.shade_blue, null)
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
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}