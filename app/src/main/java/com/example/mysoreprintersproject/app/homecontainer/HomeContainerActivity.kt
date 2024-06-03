package com.example.mysoreprintersproject.app.homecontainer


import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
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
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView


class HomeContainerActivity : AppCompatActivity() {

    private lateinit var frameBottomBar: BottomNavigationView

    private lateinit var drawerLayout: DrawerLayout

    private lateinit var navigationView: NavigationView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_container)

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
                // Add other cases for different activities
                else -> {
                    Log.d("NavigationDrawer", "Unhandled item clicked: ${item.itemId}")
                }
            }
            drawerLayout.closeDrawers()
            true
        }


        window.statusBarColor= ContextCompat.getColor(this,R.color.shade_blue)
    }


    private fun setupNavigationDrawer() {

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
