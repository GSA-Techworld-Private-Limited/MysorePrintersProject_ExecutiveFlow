package com.example.mysoreprintersproject.app.homecontainer


import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.mysoreprintersproject.R
import com.example.mysoreprintersproject.app.SplashScreenActivity
import com.example.mysoreprintersproject.app.attendance.AttendanceActivity
import com.example.mysoreprintersproject.app.attendance.AttendanceFragment
import com.example.mysoreprintersproject.app.dailycollections.DailyCollectionActivity
import com.example.mysoreprintersproject.app.dailyworkingsummryfragment.DailyWorkingSummaryActivity
import com.example.mysoreprintersproject.app.homefragment.HomeActivity
import com.example.mysoreprintersproject.app.homefragment.HomeFragment
import com.example.mysoreprintersproject.app.netsale.NetSaleActivity
import com.example.mysoreprintersproject.app.profilefragment.ProfileFragment
import com.example.mysoreprintersproject.app.report.ReportFragment
import com.example.mysoreprintersproject.app.supplyreport.SupplyReportActivity
import com.example.mysoreprintersproject.network.SessionManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.android.gms.location.*
import android.location.Location
import android.os.Looper
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ResolvableApiException
import java.io.IOException


class HomeContainerActivity : AppCompatActivity() {

    private lateinit var frameBottomBar: BottomNavigationView

    private lateinit var drawerLayout: DrawerLayout

    private lateinit var navigationView: NavigationView


    private  val REQUEST_CHECK_SETTINGS = 100

    private lateinit var sessionManager: SessionManager


    private lateinit var database: DatabaseReference



    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        sessionManager= SessionManager(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_container)



        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        database = FirebaseDatabase.getInstance().reference.child("coordinate")

        if (isLocationPermissionGranted()) {
            checkLocationSettingsAndStartUpdates()
        } else {
            requestLocationPermission()
        }


        drawerLayout=findViewById(R.id.drawerlayout)

        navigationView=findViewById(R.id.navigationView)

        navigationView.itemIconTintList=null



        replaceFragment(HomeFragment())

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFagment) as NavHostFragment?

        val navController = navHostFragment!!.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            Log.d("NavController", "Navigated to ${destination.label}")
        }


        frameBottomBar = findViewById(R.id.frameBottombar)

        NavigationUI.setupWithNavController(navigationView,navController)


        NavigationUI.setupWithNavController(frameBottomBar, navController)

        frameBottomBar.setOnNavigationItemSelectedListener {it ->
            when(it.itemId){

                R.id.home -> {
                    replaceFragment(HomeFragment())
                    true
                }

                R.id.attendance -> {
                   replaceFragment(AttendanceFragment())
                    true
                }

                R.id.report -> {
                    replaceFragment(ReportFragment())
                    true
                }

                R.id.account -> {
                   replaceFragment(ProfileFragment())
                    true
                }

                else -> {
                    TODO("Hello")
                }
            }

        }

        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {

                R.id.nav_dashboard->{
                   startActivity(Intent(this,HomeContainerActivity::class.java))

                }

                R.id.nav_attendance -> {
                    startActivity(Intent(this,AttendanceActivity::class.java))

                }


                R.id.nav_daily_work_summary -> {
                    startActivity(Intent(this, DailyWorkingSummaryActivity::class.java))
                }

                R.id.nav_collections_performance -> {
                    startActivity(Intent(this,DailyCollectionActivity::class.java))
                }
                R.id.nav_collections_report -> {
                    startActivity(Intent(this, DailyCollectionActivity::class.java))
                }
                R.id.nav_supply_reports -> {
                    startActivity(Intent(this, SupplyReportActivity::class.java))
                }
                R.id.nav_net_sales_report -> {
                    startActivity(Intent(this, NetSaleActivity::class.java))
                }

                R.id.nav_logout -> {
//                    startActivity(Intent(this,SplashScreenActivity::class.java))
//                    finishAffinity()
//                    Log.d("logoutbuttontag","Tag is hitting")
//                    sessionManager.logout()
//                    sessionManager.clearSession()

                }
                // Add other cases for different activities
                else -> {
                    Log.d("NavigationDrawer", "Unhandled item clicked: ${item.itemId}")
                }
            }
            drawerLayout.closeDrawers()
            true
        }


        //startLocationUpdates()
        window.statusBarColor= ContextCompat.getColor(this,R.color.shade_blue)
    }




    private fun isLocationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }


    private fun requestLocationPermission() {
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                checkLocationSettingsAndStartUpdates()
            } else {
                Toast.makeText(this, "Location permission is required.", Toast.LENGTH_SHORT).show()
            }
        }


    private fun checkLocationSettingsAndStartUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = 150000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(this)

        client.checkLocationSettings(builder.build()).addOnSuccessListener {
            startLocationUpdates()
        }.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                try {
                    e.startResolutionForResult(this, REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }


    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = 20000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation ?: return
                sessionManager.storeLatitude(location.latitude )
                sessionManager.storeLongitude(location.longitude)
                Log.d("Location", "Lat: ${location.latitude}, Lng: ${location.longitude}")

                // Send location to Firebase
                sendLocationToFirebase(location.latitude, location.longitude)
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }



    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK) {
                startLocationUpdates()
            } else {
                Toast.makeText(this, "Please enable location services.", Toast.LENGTH_SHORT).show()
            }
        }
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


    private fun setUpLogout() {
        val i=Intent(this,SplashScreenActivity::class.java)
        startActivity(i)
        finish()
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        // Check if the fragment is already in the back stack
        val existingFragment = fragmentManager.findFragmentByTag(fragment.javaClass.simpleName)

        if (existingFragment == null) {
            fragmentTransaction.replace(R.id.navHostFagment, fragment, fragment.javaClass.simpleName)
            fragmentTransaction.addToBackStack(fragment.javaClass.simpleName)
            fragmentTransaction.commit()
        } else {
            // If the fragment already exists, simply pop the back stack up to it
            fragmentManager.popBackStackImmediate(existingFragment.javaClass.simpleName, 0)
        }
    }

    override fun onBackPressed() {
        val fragmentManager = supportFragmentManager

        if (fragmentManager.backStackEntryCount == 1) {
            showExitDialog()
        } else {
            if (fragmentManager.backStackEntryCount > 1) {
                fragmentManager.popBackStackImmediate(
                    fragmentManager.getBackStackEntryAt(1).id,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )

                var selectedFragment: Fragment? = null
                val fragments = supportFragmentManager.fragments
                for (fragment in fragments) {
                    if (fragment != null && fragment.isVisible) {
                        selectedFragment = fragment
                        break
                    }
                }

                selectedFragment?.let {
                    when (it) {
                        is HomeFragment -> frameBottomBar.selectedItemId = R.id.home
                        is AttendanceFragment -> frameBottomBar.selectedItemId = R.id.attendance
                        is ReportFragment -> frameBottomBar.selectedItemId = R.id.report
                    }
                } ?: super.onBackPressed()
            } else {
                super.onBackPressed()
            }
        }
    }

    private fun showExitDialog() {
        AlertDialog.Builder(this)
            .setMessage("Are you sure you want to exit?")
            .setCancelable(false)
            .setPositiveButton("Yes", DialogInterface.OnClickListener { _, _ -> finish() })
            .setNegativeButton("No", null)
            .show()
    }
}
