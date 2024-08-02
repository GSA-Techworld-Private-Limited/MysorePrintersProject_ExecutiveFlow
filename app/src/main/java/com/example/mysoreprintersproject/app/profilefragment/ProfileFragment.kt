package com.example.mysoreprintersproject.app.profilefragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.mysoreprintersproject.R
import com.example.mysoreprintersproject.app.attendance.AttendanceActivity
import com.example.mysoreprintersproject.app.dailycollections.DailyCollectionActivity
import com.example.mysoreprintersproject.app.dailyworkingsummryfragment.DailyWorkingSummaryActivity
import com.example.mysoreprintersproject.app.homecontainer.HomeContainerActivity
import com.example.mysoreprintersproject.app.netsale.NetSaleActivity
import com.example.mysoreprintersproject.app.supplyreport.SupplyReportActivity
import com.example.mysoreprintersproject.databinding.FragmentProfileBinding
import com.example.mysoreprintersproject.network.BaseFragment
import com.example.mysoreprintersproject.network.DataSource
import com.example.mysoreprintersproject.network.Resource
import com.example.mysoreprintersproject.network.SessionManager
import com.example.mysoreprintersproject.responses.UserRepository
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class ProfileFragment : BaseFragment<ProfileViewModel, FragmentProfileBinding, UserRepository>() {




    private lateinit var drawerLayout: DrawerLayout

    private lateinit var navigationView: NavigationView

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.featchUserProfile()


        viewModel.login.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    val events = resource.data
                    binding.nameEdit.setText(events.name)
                    binding.editPhoneNumber.setText(events.phonenumber)
                    binding.editEmail.setText(events.email)
                    binding.editLocation.setText(events.userLocation)

                    val image=events.profileImage
                    val file=remoteDateSource.getImageUrl(image!!)


                   // Glide.with(requireActivity()).load(file).into()

                    //binding.progressbar.visibility=View.GONE
                    //listAdapter.updateData(events)
                    //listAdapter2.updateData(events)
                    //binding.progressbar.visibility= View.GONE
                }

                is Resource.Loading -> {
                    //binding.progressbar.visibility = View.VISIBLE
                }

                is Resource.Failure -> {
                    // Handle error state if needed
                   // binding.progressbar.visibility = View.GONE
                }

                else -> {}
            }
        }
    }





    override fun getViewModel()= ProfileViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    )= FragmentProfileBinding.inflate(inflater,container,false)

    override fun getFragmentRepository(): UserRepository {
        val token = runBlocking {   userPreferences.authToken.first()}
        val api=remoteDateSource.buildApi(DataSource::class.java,token)
        return UserRepository(api,userPreferences)
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}