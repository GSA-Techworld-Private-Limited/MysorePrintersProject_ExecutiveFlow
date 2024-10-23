package com.example.mysoreprintersproject.app.attendance

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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
import com.example.mysoreprintersproject.app.lprmanagement.LPRManagementActivity
import com.example.mysoreprintersproject.app.netsale.NetSaleActivity
import com.example.mysoreprintersproject.app.notification.NotificationActivity
import com.example.mysoreprintersproject.app.supplyreport.SupplyReportActivity
import com.example.mysoreprintersproject.databinding.FragmentAttendanceBinding
import com.example.mysoreprintersproject.network.APIManager
import com.example.mysoreprintersproject.network.BaseFragment
import com.example.mysoreprintersproject.network.CheckInViewModel
import com.example.mysoreprintersproject.network.DataSource
import com.example.mysoreprintersproject.network.LocationUpdateService
import com.example.mysoreprintersproject.network.Resource
import com.example.mysoreprintersproject.network.SessionManager
import com.example.mysoreprintersproject.repository.AuthRepository
import com.example.mysoreprintersproject.responses.CheckInRequest
import com.example.mysoreprintersproject.responses.CheckOutRequest
import com.example.mysoreprintersproject.responses.FinalLocations
import com.example.mysoreprintersproject.responses.ProfileResponses
import com.example.mysoreprintersproject.responses.SupplyReportResponse
import com.example.mysoreprintersproject.responses.UserRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import retrofit2.Call
import retrofit2.Response
import java.io.File

class AttendanceFragment :
    BaseFragment<CheckInViewModel, FragmentAttendanceBinding, UserRepository>() {

        private lateinit var sessionManager: SessionManager

    private lateinit var drawerLayout: DrawerLayout

    private lateinit var navigationView: NavigationView

    private var isCheckedIn = false // To track the check-in state

    private lateinit var database: DatabaseReference

    private lateinit var mapView: MapView

    private val locationsList = mutableListOf<GeoPoint>()


    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        sessionManager = SessionManager(requireActivity())
        super.onActivityCreated(savedInstanceState)

        drawerLayout = binding.drawerLayout

        val navigatioViewIcon: ImageView = binding.imageSettings
        navigatioViewIcon.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        database = FirebaseDatabase.getInstance().reference.child("coordinate")

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
                R.id.nav_lprmanagement -> startActivity(Intent(requireActivity(),
                    LPRManagementActivity::class.java))
                R.id.nav_daily_work_summary -> startActivity(Intent(requireActivity(),
                    DailyWorkSummaryActivity::class.java))
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

        binding.imageSettings1.setOnClickListener {
            val i=Intent(requireActivity(),NotificationActivity::class.java)
            startActivity(i)
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

        val lattitude=sessionManager.getLatitude()
        val longitude=sessionManager.getLongitude()
        // Observing the Check-In Response
        viewModel.checkInResponse.observe(viewLifecycleOwner, Observer { checkInResult ->
            when (checkInResult) {
                is Resource.Success -> {
                    Toast.makeText(requireActivity(), "Checked In Successfully", Toast.LENGTH_SHORT).show()
                    toggleButtons(true)
                    sendLocationToFirebase(lattitude!!,longitude!!)
                    sessionManager.saveCheckInState(true) // Save check-in state
                    startLocationService()
                    mapView.visibility=View.GONE
                    binding.progressbar.visibility=View.GONE
                }
                is Resource.Failure -> {
                    Toast.makeText(requireActivity(), "Check-In failed!", Toast.LENGTH_SHORT).show()
                    mapView.visibility=View.GONE
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
                    sessionManager.saveCheckInState(false) // Save check-out state
                    stopLocationService()
                    mapView.visibility=View.VISIBLE
                    getFinalLocations()
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
            } else {
                Toast.makeText(requireActivity(), "You need to check in first!", Toast.LENGTH_SHORT).show()
                binding.progressbar.visibility=View.GONE
            }
        }


        // Initialize Osmdroid configuration
        val osmdroidBasePath = File(
            Environment.getExternalStorageDirectory().absolutePath + "/osmdroid"
        )
        if (!osmdroidBasePath.exists()) {
            osmdroidBasePath.mkdirs()
        }
        Configuration.getInstance().osmdroidBasePath = osmdroidBasePath
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID

        mapView = binding.mapView


        //getFinalLocations()
       // setupOfflineMap()

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



    private fun startLocationService() {
        val intent = Intent(requireContext(), LocationUpdateService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireContext().startForegroundService(intent)
        } else {
            requireContext().startService(intent)
        }
    }

    private fun stopLocationService() {
        val intent = Intent(requireContext(), LocationUpdateService::class.java)
        requireContext().stopService(intent)
    }


    private fun sendLocationToFirebase(latitude: Double, longitude: Double) {
        // Retrieve the user ID
        val userId = sessionManager.fetchUserId()!!

        // Reference to the user's location data in Firebase
        val userLocationRef = database.child(userId)

        // Get the current number of children to determine the next location key
        userLocationRef.get().addOnSuccessListener { snapshot ->
            val locationCount = snapshot.childrenCount
            val newLocationKey = "location_${locationCount + 1}"

            val locationData = mapOf(
                "latitude" to latitude,
                "longitude" to longitude,
                "timestamp" to System.currentTimeMillis()
            )

            // Store the new location data under the new key
            userLocationRef.child(newLocationKey).setValue(locationData)
                .addOnSuccessListener {
                    Log.d("Firebase", "Location data sent successfully.")
                }
                .addOnFailureListener { e ->
                    Log.e("Firebase", "Failed to send location data.", e)
                }
        }
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


    private fun setupOfflineMap() {
        // Set tile source for offline use
        mapView.setTileSource(TileSourceFactory.MAPNIK)

        // Enable zoom controls and gestures
        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(15.0)
        mapView.controller.setCenter(GeoPoint(37.7749, -122.4194)) // Center on a specific location (San Francisco)

        // Add a marker to the map
        val marker = Marker(mapView)
        marker.position = GeoPoint(37.7749, -122.4194)
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        mapView.overlays.add(marker)
    }



    private fun getFinalLocations() {
        val serviceGenerator = APIManager.apiInterface
        val accessToken = sessionManager.fetchAuthToken()
        val authorization = "Bearer $accessToken"
        val id = sessionManager.fetchUserId()!!

        serviceGenerator.getFinalCordinations(authorization, id)
            .enqueue(object : retrofit2.Callback<List<FinalLocations>> {
                override fun onResponse(
                    call: Call<List<FinalLocations>>,
                    response: Response<List<FinalLocations>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val locations = response.body()!!
                        // Clear existing markers before adding new ones
                        mapView.overlays.clear()
                        locationsList.clear() // Clear previous location list

                        // Iterate through locations and add markers
                        for (i in locations.indices) {
                            val location = locations[i]
                            val latitude = location.latitude
                            val longitude = location.longitude

                            // Check for null values
                            if (latitude != null && longitude != null) {
                                addMarkerToMap(latitude.toDouble(), longitude.toDouble(), i + 1) // Pass the index for labeling
                            }
                        }
                        mapView.invalidate() // Refresh the map view
                    } else {
                        Toast.makeText(requireActivity(), "Failed to retrieve data", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<FinalLocations>>, t: Throwable) {
                    Log.e("SupplyReportFragment", "Error fetching data: ${t.message}", t)
                    Toast.makeText(requireActivity(), "Error fetching data: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun addMarkerToMap(latitude: Double, longitude: Double, index: Int) {
        val geoPoint = GeoPoint(latitude, longitude)
        locationsList.add(geoPoint) // Add to locations list

        val marker = Marker(mapView)
        marker.position = geoPoint
        marker.title = "$index" // Set title to 1st, 2nd, etc.
        marker.icon = resources.getDrawable(R.drawable.ic_location, null) // Use your marker icon drawable
        mapView.overlays.add(marker)

        // Optional: Center the map on the first marker added
        if (mapView.overlays.size == 1) {
            mapView.controller.setZoom(15.0) // Set zoom level
            mapView.controller.setCenter(geoPoint) // Center map
        }

        // Draw the polyline connecting the locations
        if (locationsList.size > 1) {
            drawPolyline()
        }

        mapView.invalidate() // Refresh the map view
    }

    private fun drawPolyline() {
        val polyline = Polyline(mapView)
        polyline.setPoints(locationsList) // Set the list of GeoPoints for the polyline
        polyline.color = Color.BLUE // Set the polyline color to blue
        polyline.width = 5f // Set the width of the polyline
        mapView.overlays.add(polyline) // Add the polyline to the map's overlays
    }




}