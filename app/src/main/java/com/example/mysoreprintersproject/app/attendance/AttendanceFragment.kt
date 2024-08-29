package com.example.mysoreprintersproject.app.attendance

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.mysoreprintersproject.R
import com.example.mysoreprintersproject.app.CollectionSummaryReport.CollectionSummaryReportActivity
import com.example.mysoreprintersproject.app.DailyWorkSummaryActivity
import com.example.mysoreprintersproject.app.SplashScreenActivity
import com.example.mysoreprintersproject.app.collection_performance.CollectionPerformanceActivity
import com.example.mysoreprintersproject.app.dailycollections.DailyCollectionActivity
import com.example.mysoreprintersproject.app.dailyworkingsummryfragment.DailyWorkingSummaryActivity
import com.example.mysoreprintersproject.app.homecontainer.HomeContainerActivity
import com.example.mysoreprintersproject.app.homefragment.HomeActivity
import com.example.mysoreprintersproject.app.netsale.NetSaleActivity
import com.example.mysoreprintersproject.app.supplyreport.SupplyReportActivity
import com.example.mysoreprintersproject.databinding.FragmentAttendanceBinding
import com.example.mysoreprintersproject.network.APIManager
import com.example.mysoreprintersproject.network.BaseFragment
import com.example.mysoreprintersproject.network.CheckInViewModel
import com.example.mysoreprintersproject.network.DataSource
import com.example.mysoreprintersproject.network.Resource
import com.example.mysoreprintersproject.network.SessionManager
import com.example.mysoreprintersproject.repository.AuthRepository
import com.example.mysoreprintersproject.responses.CheckInRequest
import com.example.mysoreprintersproject.responses.CheckOutRequest
import com.example.mysoreprintersproject.responses.ProfileResponses
import com.example.mysoreprintersproject.responses.UserRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Response

class AttendanceFragment :
    BaseFragment<CheckInViewModel, FragmentAttendanceBinding, UserRepository>() {

        private lateinit var sessionManager: SessionManager

    private lateinit var drawerLayout: DrawerLayout

    private lateinit var navigationView: NavigationView

    private var isCheckedIn = false // To track the check-in state



    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        sessionManager = SessionManager(requireActivity())
        super.onActivityCreated(savedInstanceState)

        drawerLayout = binding.drawerLayout

        val navigatioViewIcon: ImageView = binding.imageSettings
        navigatioViewIcon.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        val km=sessionManager.fetchKm()
        binding.totalkmtravelled.text="Kilometer Travelled :$km"
        navigationView = binding.navigationView

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

        getExecutiveProfile()


        val place=sessionManager.fetchPlace()
        val phase=sessionManager.fetchPhase()
        val city=sessionManager.fetchCity()
        val state=sessionManager.fetchState()
        val pinode=sessionManager.fetchPincode()

        binding.location.text="$phase, $place  \n $city, $state, $pinode"

        setInitialButtonStyles()

        val token = sessionManager.fetchAuthToken()
        val authorization = "Bearer $token"
        val id = sessionManager.fetchUserId()!!

        // Observing the Check-In Response
        viewModel.checkInResponse.observe(viewLifecycleOwner, Observer { checkInResult ->
            when (checkInResult) {
                is Resource.Success -> {
                    Toast.makeText(requireActivity(), "Checked In Successfully", Toast.LENGTH_SHORT).show()
                    toggleButtons(true)
                    binding.progressbar.visibility=View.GONE
                }
                is Resource.Failure -> {
                    Toast.makeText(requireActivity(), "Check-In failed!", Toast.LENGTH_SHORT).show()
                    binding.progressbar.visibility=View.GONE
                }
                else -> { /* Handle other cases if needed */ }
            }
        })

        // Observing the Check-Out Response
        viewModel.checkOutResponse.observe(viewLifecycleOwner, Observer { checkOutResult ->
            when (checkOutResult) {
                is Resource.Success -> {
                    Toast.makeText(requireActivity(), "Checked Out Successfully", Toast.LENGTH_SHORT).show()
                    toggleButtons(false)
                    binding.progressbar.visibility=View.GONE
                }
                is Resource.Failure -> {
                   if (checkOutResult.errorCode == 404) {
                        Toast.makeText(requireActivity(),  "No coordination found, but checked out successfully", Toast.LENGTH_SHORT).show()
                        sessionManager.saveCheckInState(false)
                       toggleButtons(false)
                       binding.progressbar.visibility = View.GONE
                    } else {

                       Toast.makeText(requireActivity(), "Check-Out failed!", Toast.LENGTH_SHORT).show()
                       binding.progressbar.visibility = View.GONE
                    }

                }
                else -> { /* Handle other cases if needed */ }
            }
        })

        // Set initial button styles based on check-in state
      //  toggleButtons(isCheckedIn)


        binding.checkIn.setOnClickListener {
            binding.progressbar.visibility=View.VISIBLE
            if (!isCheckedIn) {
                val sendingRequest = CheckInRequest(id.toInt(), sessionManager.getLatitude()!!, sessionManager.getLongitude()!!)
                viewModel.checkIn(authorization, sendingRequest)
                sessionManager.saveCheckInState(true) // Save check-in state
            } else {
                Toast.makeText(requireActivity(), "You are already checked in!", Toast.LENGTH_SHORT).show()
                binding.progressbar.visibility=View.GONE
            }
        }

        binding.checkout.setOnClickListener {
            binding.progressbar.visibility=View.VISIBLE
            if (isCheckedIn) {
                val sendingCheckOutRequest = CheckOutRequest(id.toInt())
                viewModel.checkOut(authorization, sendingCheckOutRequest)
                sessionManager.saveCheckInState(false) // Save check-out state
            } else {
                Toast.makeText(requireActivity(), "You need to check in first!", Toast.LENGTH_SHORT).show()
                binding.progressbar.visibility=View.GONE
            }
        }

    }



    private fun setInitialButtonStyles() {
        // Retrieve the check-in state from the SessionManager or ViewModel
        isCheckedIn = sessionManager.isCheckedIn()

        if (isCheckedIn) {
            binding.checkIn.isEnabled = false // Disable check-in button
            binding.checkout.isEnabled = true  // Enable check-out button

            binding.checkIn.setBackgroundResource(R.drawable.rectangle_for_chack_button)
            binding.checkIn.setTextAppearance(R.style.txtCheckButton)
            binding.checkout.setTextAppearance(R.style.txtCheckButton1)
            binding.checkout.setBackgroundResource(R.drawable.rectangle_bg_white_check_button)
        } else {
            binding.checkIn.isEnabled = true // Enable check-in button
            binding.checkout.isEnabled = false // Disable check-out button

            binding.checkIn.setBackgroundResource(R.drawable.rectangle_bg_white_check_button)
            binding.checkout.setBackgroundResource(R.drawable.rectangle_for_chack_button)
            binding.checkIn.setTextAppearance(R.style.txtCheckButton1)
            binding.checkout.setTextAppearance(R.style.txtCheckButton)
        }
    }




    private fun toggleButtons(isCheckedIn: Boolean) {
        this.isCheckedIn = isCheckedIn
        if (isCheckedIn) {
            binding.checkIn.isEnabled = false // Disable the check-in button after successful check-in
            binding.checkout.isEnabled = true  // Enable the check-out button

            binding.checkIn.setBackgroundResource(R.drawable.rectangle_for_chack_button)
            binding.checkIn.setTextAppearance(R.style.txtCheckButton)
            binding.checkout.setTextAppearance(R.style.txtCheckButton1)
            binding.checkout.setBackgroundResource(R.drawable.rectangle_bg_white_check_button)
        } else {
            binding.checkIn.isEnabled = true // Enable the check-in button after check-out
            binding.checkout.isEnabled = false // Disable the check-out button

            binding.checkIn.setBackgroundResource(R.drawable.rectangle_bg_white_check_button)
            binding.checkout.setBackgroundResource(R.drawable.rectangle_for_chack_button)
            binding.checkIn.setTextAppearance(R.style.txtCheckButton1)
            binding.checkout.setTextAppearance(R.style.txtCheckButton)
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
                    if (isAdded) {  // Check if fragment is attached
                        val profileResponses = response.body()

                        if (profileResponses != null) {
                            val profileImage: ImageView = binding.imageSettings2
                            val image = profileResponses.profileImage
                            val file = APIManager.getImageUrl(image!!)
                            Glide.with(requireActivity()).load(file).into(profileImage)
                        }
                    }
                }

                override fun onFailure(call: Call<ProfileResponses>, t: Throwable) {
                    if (isAdded) {  // Check if fragment is attached
                        Toast.makeText(requireActivity(), "Error fetching data", Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }






    override fun getViewModel()=CheckInViewModel::class.java
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    )= FragmentAttendanceBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): UserRepository {
        val token = runBlocking { userPreferences.authToken.first() }
        val api = remoteDateSource.buildApi(DataSource::class.java, token)
        return UserRepository(api, userPreferences)
    }

}