package com.example.mysoreprintersproject.app.collection_performance

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.example.mysoreprintersproject.R
import com.example.mysoreprintersproject.network.APIManager
import com.example.mysoreprintersproject.network.DataSource
import com.example.mysoreprintersproject.network.SessionManager
import com.example.mysoreprintersproject.responses.CollectionReport
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class Collection_Performance_Fragment : Fragment(R.layout.fragment_collection__performance_) {

    private lateinit var periodSpinner: Spinner
    private lateinit var apiService:DataSource
    private lateinit var sessionManager: SessionManager
    private lateinit var paymentMethod: String
    private lateinit var submitButton:AppCompatButton
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sessionManager=SessionManager(requireActivity())

        apiService=APIManager.apiInterface
        periodSpinner = requireView().findViewById(R.id.spinnerCash)
        submitButton=requireView().findViewById(R.id.btnSubmit)

        val agentCode:EditText=requireView().findViewById(R.id.editCode)
        val agentName:EditText=requireView().findViewById(R.id.editName)
        val instrumentNumber:EditText=requireView().findViewById(R.id.editinstumental)
        val amountCollected:EditText=requireView().findViewById(R.id.editAmountCollected)
        // Set the default selection to 6 months
        periodSpinner.setSelection(0) // Assuming the first item in the Spinner is "6 months"

        periodSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (view == null) return // Handle the null case

                 paymentMethod = parent.getItemAtPosition(position).toString()

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Optionally handle this case
            }
        }


        submitButton.setOnClickListener {
            val name = agentName.text.toString().trim()
            val code = agentCode.text.toString().trim()
            val method = paymentMethod.toString().trim()
            val number = instrumentNumber.text.toString().trim()
            val amount = amountCollected.text.toString().trim()

            if (name.isNotEmpty() && code.isNotEmpty() && method.isNotEmpty() && number.isNotEmpty() && amount.isNotEmpty()) {
                postCollectionReport(code, name, method, number, amount)
            } else {
                Toast.makeText(requireActivity(), "All Fields are Mandatory", Toast.LENGTH_SHORT).show()
            }
        }




    }





    private fun postCollectionReport(agendCode:String,agentName:String,paymentMethod:String,instrumentNumber:String,amount:String){
        val id=sessionManager.fetchUserId()!!
        val token=sessionManager.fetchAuthToken()
        val authorization="Bearer $token"
        val postRequests= CollectionReport(agentName,agendCode,paymentMethod,instrumentNumber,amount,id.toInt())
        val call = apiService.sendCollectionReport(authorization,postRequests)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {

                if (response.isSuccessful) {

                    Toast.makeText(requireActivity(),"Collection Report Sent Successfully",Toast.LENGTH_SHORT).show()
                    submitButton.text="Edit"
                } else {
                    // Handle other HTTP error codes if needed
                    Toast.makeText(requireActivity(), "SignUp failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Handle failure (e.g., network failure, timeout)
                Toast.makeText(requireActivity(), "User Already Registered, Please Login!", Toast.LENGTH_SHORT).show()
            }
        })
    }

}