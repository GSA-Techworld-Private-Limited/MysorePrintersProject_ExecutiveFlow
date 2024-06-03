package com.example.mysoreprintersproject.app.profilefragment

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
import com.example.mysoreprintersproject.R
import com.example.mysoreprintersproject.app.attendance.AttendanceActivity
import com.example.mysoreprintersproject.app.dailycollections.DailyCollectionActivity
import com.example.mysoreprintersproject.app.dailyworkingsummryfragment.DailyWorkingSummaryActivity
import com.example.mysoreprintersproject.app.homecontainer.HomeContainerActivity
import com.example.mysoreprintersproject.app.homefragment.HomeActivity
import com.example.mysoreprintersproject.app.netsale.NetSaleActivity
import com.example.mysoreprintersproject.app.supplyreport.SupplyReportActivity
import com.google.android.material.navigation.NavigationView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


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
        return inflater.inflate(R.layout.fragment_profile, container, false).apply {
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
                        startActivity(Intent(requireActivity(),DailyCollectionActivity::class.java))
                    }
                    R.id.nav_collections_performance -> {
                        startActivity(Intent(requireActivity(),DailyCollectionActivity::class.java))
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
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}