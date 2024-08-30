package com.example.mysoreprintersproject.app

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.mysoreprintersproject.R
import com.example.mysoreprintersproject.app.CollectionSummaryReport.CollectionSummaryReportActivity
import com.example.mysoreprintersproject.app.CollectionSummaryReport.CollectionSummaryReportAdapter
import com.example.mysoreprintersproject.app.attendance.AttendanceActivity
import com.example.mysoreprintersproject.app.collection_performance.CollectionPerformanceActivity
import com.example.mysoreprintersproject.app.dailycollections.DailyCollectionActivity
import com.example.mysoreprintersproject.app.dailyworkingsummryfragment.DailyWorkingSummaryActivity
import com.example.mysoreprintersproject.app.homecontainer.HomeContainerActivity
import com.example.mysoreprintersproject.app.netsale.NetSaleActivity
import com.example.mysoreprintersproject.app.netsale.NetSaleAdapter
import com.example.mysoreprintersproject.app.notification.NotificationActivity
import com.example.mysoreprintersproject.app.supplyreport.SupplyReportActivity
import com.example.mysoreprintersproject.network.APIManager
import com.example.mysoreprintersproject.network.DataSource
import com.example.mysoreprintersproject.network.SessionManager
import com.example.mysoreprintersproject.responses.AgentNameResponses
import com.example.mysoreprintersproject.responses.CollectionReport
import com.example.mysoreprintersproject.responses.CollectionSummaryReportResponses
import com.example.mysoreprintersproject.responses.LocationNameResponse
import com.example.mysoreprintersproject.responses.NetSalesResponse
import com.example.mysoreprintersproject.responses.ProfileResponses
import com.example.mysoreprintersproject.responses.SendDailyWorkSummary
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class DailyWorkSummaryFragment : Fragment() {


    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var spinnerName: Spinner
    private lateinit var spinnerMarket: Spinner
    private lateinit var editTextSelectedAgents: EditText
    private lateinit var editTextSelectedPlaces: EditText
    private lateinit var sessionManager: SessionManager
    private lateinit var apiService: DataSource

    // Variables to store selected agent names and places
    private val selectedAgentNames = mutableListOf<String>()
    private val selectedPlaces = mutableListOf<String>()

    private lateinit var selectedAgentsString: String
    private lateinit var selectedPlacesString: String

    private lateinit var profileImage:ImageView

    private lateinit var btnSubmit:AppCompatButton
    private lateinit var dateEditText: EditText
    private var selectedDate: String? = null

    private lateinit var editInstitutions:EditText
    private lateinit var editTotalAccomplished:EditText
    private lateinit var editWhatAppNumber:EditText
    private lateinit var editEmail:EditText
    private lateinit var notificationIcon:ImageView

    private lateinit var progressBar:ProgressBar
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sessionManager = SessionManager(requireActivity())
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_daily_work_summary, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        spinnerName = requireView().findViewById(R.id.spinner_agents)
        spinnerMarket = requireView().findViewById(R.id.spinner_market)
        editTextSelectedAgents = requireView().findViewById(R.id.editTextSelectedAgents)
        editTextSelectedPlaces = requireView().findViewById(R.id.editTextSelectedPlaces)

        editInstitutions=requireView().findViewById(R.id.editInstitutions)
        editTotalAccomplished=requireView().findViewById(R.id.editTotalAccomplished)
        editWhatAppNumber=requireView().findViewById(R.id.editWhatAppNumber)
        editEmail=requireView().findViewById(R.id.editEmail)
        progressBar=requireView().findViewById(R.id.progressBar)

        btnSubmit=requireView().findViewById(R.id.btnSubmit)
        dateEditText=requireView().findViewById(R.id.editDate)
        apiService=APIManager.apiInterface
        // Initially hide the EditTexts
        editTextSelectedAgents.visibility = View.GONE
        editTextSelectedPlaces.visibility = View.GONE

        profileImage= requireView().findViewById(R.id.imageSettings2)
        getAgentName()
        getPlacesVisited()

        // Set up DatePicker
        dateEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val dateSetListener = DatePickerDialog.OnDateSetListener { view: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                val selectedDate = "$year-${month + 1}-$dayOfMonth"
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                this.selectedDate = dateFormat.format(calendar.time)
                dateEditText.setText(this.selectedDate)
            }
            DatePickerDialog(requireContext(), dateSetListener, calendar.get(Calendar.YEAR), calendar.get(
                Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
        initializeSpinners()

        drawerLayout = requireView().findViewById(R.id.drawer_layout)
        val navigatioViewIcon: ImageView = requireView().findViewById(R.id.imageSettings)
        navigationView = requireView().findViewById(R.id.navigationView)
        notificationIcon=requireView().findViewById(R.id.imageSettings1)

        navigatioViewIcon.setOnClickListener {
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

        btnSubmit.setOnClickListener {
            val institute_visited=editInstitutions.text.toString().trim()
            val task_accomplished=editTotalAccomplished.text.toString().trim()
            val whatsapp_num=editWhatAppNumber.text.toString().trim()
            val emailID=editEmail.text.toString().trim()

            postDailyWorkSummary(selectedPlacesString,selectedAgentsString,institute_visited,task_accomplished,whatsapp_num,emailID,selectedDate!!)
            progressBar.visibility=View.VISIBLE
        }

        notificationIcon.setOnClickListener {
            val i=Intent(requireActivity(),NotificationActivity::class.java)
            startActivity(i)
        }
    }

    private fun initializeSpinners() {
        // Set initial prompt for both spinners
        val initialAgentPrompt = listOf("Select Agent")
        val initialPlacePrompt = listOf("Select Place")

        // Set the adapter with the initial prompt
        val agentAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, initialAgentPrompt)
        agentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerName.adapter = agentAdapter

        val placeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, initialPlacePrompt)
        placeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMarket.adapter = placeAdapter
    }

    private fun getAgentName() {
        val serviceGenerator = APIManager.apiInterface
        val accessToken = sessionManager.fetchAuthToken()
        val authorization = "Bearer $accessToken"

        serviceGenerator.getAgentName(authorization)
            .enqueue(object : Callback<List<AgentNameResponses>> {
                override fun onResponse(
                    call: Call<List<AgentNameResponses>>,
                    response: Response<List<AgentNameResponses>>
                ) {
                    if (response.isSuccessful) {
                        val agentNames = response.body()?.map { it.name ?: "" } ?: emptyList()

                        // Combine the initial prompt with the agent names
                        val allAgentNames = listOf("Select Agent") + agentNames

                        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, allAgentNames)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                        spinnerName.adapter = adapter

                        spinnerName.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                                // Ignore the first item ("Select Agent")
                                if (position == 0) return

                                val selectedName = allAgentNames[position]

                                if (!selectedAgentNames.contains(selectedName)) {
                                    selectedAgentNames.add(selectedName)
                                }

                                if (selectedAgentNames.size > 1) {
                                    editTextSelectedAgents.visibility = View.VISIBLE
                                    selectedAgentsString = selectedAgentNames.joinToString(", ")
                                    editTextSelectedAgents.setText(selectedAgentsString)
                                } else {
                                    editTextSelectedAgents.visibility = View.GONE
                                }
                            }

                            override fun onNothingSelected(parent: AdapterView<*>) {
                                // No action needed
                            }
                        }
                    } else {
                        Toast.makeText(requireActivity(), "Failed to retrieve agent data", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<AgentNameResponses>>, t: Throwable) {
                    Log.e("DailyWorkSummary", "Error fetching agent data: ${t.message}", t)
                    Toast.makeText(requireActivity(), "Error fetching agent data: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun getPlacesVisited() {
        val serviceGenerator = APIManager.apiInterface
        val accessToken = sessionManager.fetchAuthToken()
        val authorization = "Bearer $accessToken"
        val id = sessionManager.fetchUserId()!!

        serviceGenerator.getplacesVisited(authorization, id)
            .enqueue(object : Callback<LocationNameResponse> {
                override fun onResponse(call: Call<LocationNameResponse>, response: Response<LocationNameResponse>) {
                    if (response.isSuccessful) {
                        val places = response.body()?.locations ?: emptyList()

                        // Combine the initial prompt with the places
                        val allPlaces = listOf("Select Place") + places

                        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, allPlaces)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                        spinnerMarket.adapter = adapter

                        spinnerMarket.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                                // Ignore the first item ("Select Place")
                                if (position == 0) return

                                val selectedPlace = allPlaces[position]

                                if (!selectedPlaces.contains(selectedPlace)) {
                                    selectedPlaces.add(selectedPlace)
                                }

                                if (selectedPlaces.size > 1) {
                                    editTextSelectedPlaces.visibility = View.VISIBLE
                                    selectedPlacesString = selectedPlaces.joinToString(", ")
                                    editTextSelectedPlaces.setText(selectedPlacesString)
                                } else {
                                    editTextSelectedPlaces.visibility = View.GONE
                                }
                            }

                            override fun onNothingSelected(parent: AdapterView<*>) {
                                // No action needed
                            }
                        }
                    } else {
                        Toast.makeText(requireActivity(), "Failed to retrieve places data", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LocationNameResponse>, t: Throwable) {
                    Log.e("DailyWorkSummary", "Error fetching places data: ${t.message}", t)
                    Toast.makeText(requireActivity(), "Error fetching places data: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }




    private fun postDailyWorkSummary(markets_visited:String,agent_visited:String,institute_visited:String,task_accomplished:String,whatsapp_num:String,emailID:String,Date:String){
        val id=sessionManager.fetchUserId()!!
        val token=sessionManager.fetchAuthToken()
        val authorization="Bearer $token"
        val postRequests= SendDailyWorkSummary(id,markets_visited,agent_visited,institute_visited, task_accomplished, whatsapp_num, emailID, Date)
        val call = apiService.sendDailyWorkSummary(authorization,postRequests)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                progressBar.visibility=View.GONE
                if (response.isSuccessful) {

                    Toast.makeText(requireActivity(),"Daily Work Summary Successfully",Toast.LENGTH_SHORT).show()
                } else {
                    // Handle other HTTP error codes if needed
                    Toast.makeText(requireActivity(), "failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Handle failure (e.g., network failure, timeout)
                Toast.makeText(requireActivity(), "Failed", Toast.LENGTH_SHORT).show()
                progressBar.visibility=View.GONE
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


