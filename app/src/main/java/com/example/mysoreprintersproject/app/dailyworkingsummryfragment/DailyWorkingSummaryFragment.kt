package com.example.mysoreprintersproject.app.dailyworkingsummryfragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mysoreprintersproject.R
import com.example.mysoreprintersproject.app.CollectionSummaryReport.CollectionSummaryReportActivity
import com.example.mysoreprintersproject.app.SplashScreenActivity
import com.example.mysoreprintersproject.app.attendance.AttendanceActivity
import com.example.mysoreprintersproject.app.collection_performance.CollectionPerformanceActivity
import com.example.mysoreprintersproject.app.dailycollections.DailyCollectionActivity
import com.example.mysoreprintersproject.app.dailycollections.DayCollectionAdapter
import com.example.mysoreprintersproject.app.homecontainer.HomeContainerActivity
import com.example.mysoreprintersproject.app.homefragment.HomeActivity
import com.example.mysoreprintersproject.app.netsale.NetSaleActivity
import com.example.mysoreprintersproject.app.supplyreport.SupplyReportActivity
import com.example.mysoreprintersproject.network.APIManager
import com.example.mysoreprintersproject.network.SessionManager
import com.example.mysoreprintersproject.responses.CollectionResponses
import com.example.mysoreprintersproject.responses.DailyWorkingSummaryResponses
import com.example.mysoreprintersproject.responses.ProfileResponses
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DailyWorkingSummaryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DailyWorkingSummaryFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var sessionManager: SessionManager
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
        sessionManager=SessionManager(requireActivity())
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_daily_working_summary, container, false).apply {

            drawerLayout = findViewById(R.id.drawer_layout)

            val navigatioViewIcon: ImageView =findViewById(R.id.imageSettings)
            navigatioViewIcon.setOnClickListener {
                drawerLayout.openDrawer(GravityCompat.START)
            }

            getDailyWorkingSumary()

            getExecutiveProfile()
            navigationView=findViewById(R.id.navigationView)

            navigationView.setNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.nav_dashboard -> startActivity(Intent(requireActivity(), HomeContainerActivity::class.java))
                    R.id.nav_attendance -> startActivity(Intent(requireActivity(), AttendanceActivity::class.java))
                    R.id.nav_work_summary -> startActivity(Intent(requireActivity(), DailyWorkingSummaryActivity::class.java))
                    R.id.nav_collections_performance -> startActivity(Intent(requireActivity(), CollectionPerformanceActivity::class.java))
                    R.id.nav_collection_summary -> startActivity(Intent(requireActivity(),
                        CollectionSummaryReportActivity::class.java))
                    R.id.nav_collections_report -> startActivity(Intent(requireActivity(), DailyCollectionActivity::class.java))
                    R.id.nav_supply_reports -> startActivity(Intent(requireActivity(), SupplyReportActivity::class.java))
                    R.id.nav_net_sales_report -> startActivity(Intent(requireActivity(), NetSaleActivity::class.java))
                    R.id.nav_logout ->{
                        sessionManager.logout()
                        sessionManager.clearSession()
                        startActivity(Intent(requireActivity(), SplashScreenActivity::class.java))
                        requireActivity().finish()
                    }
                    else -> Log.d("NavigationDrawer", "Unhandled item clicked: ${item.itemId}")
                }
                drawerLayout.closeDrawers()
                true
            }


            val fromSpinner: Spinner = findViewById(R.id.spinner_from)
            val toSpinner: Spinner =findViewById(R.id.spinner_to)


            val fromAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.from_dates,
                R.layout.spinner_item
            )
            fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            fromSpinner.adapter = fromAdapter

            val toAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.to_dates,
                R.layout.spinner_item
            )
            toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            toSpinner.adapter = toAdapter
//            val recyclerView: RecyclerView = findViewById(R.id.recyclerview)
//            recyclerView.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
//            recyclerView.adapter = DailyWorkingSummaryAdapter()

//
//            // Attach LinearSnapHelper
//            val snapHelper = LinearSnapHelper()
//            snapHelper.attachToRecyclerView(recyclerView)
        }
    }



    private fun getDailyWorkingSumary() {
        val serviceGenerator = APIManager.apiInterface
        val accessToken = sessionManager.fetchAuthToken()
        val authorization = "Bearer $accessToken"
        val id = sessionManager.fetchUserId()!!

        val period="6"

        serviceGenerator.getDailyWorkingSumary(authorization, id)
            .enqueue(object : retrofit2.Callback<List<DailyWorkingSummaryResponses>> {
                override fun onResponse(call: Call<List<DailyWorkingSummaryResponses>>, response: Response<List<DailyWorkingSummaryResponses>>) {
                    val summaryResponses = response.body()!!
//                    // setupRecyclerView(summaryResponses.reversed())
//
                    val recyclerView: RecyclerView = requireView().findViewById(R.id.recyclerview)
                    recyclerView.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
                    recyclerView.adapter = DailyWorkingSummaryAdapter(summaryResponses)
                }

                override fun onFailure(call: Call<List<DailyWorkingSummaryResponses>>, t: Throwable) {
                    Log.e("SupplyReportFragment", "Error fetching data: ${t.message}", t)
                    Toast.makeText(requireActivity(), "Error fetching data: ${t.message}", Toast.LENGTH_SHORT).show()
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DailyWorkingSummaryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DailyWorkingSummaryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}