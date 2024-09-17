package com.example.mysoreprintersproject.network

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LocationWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    private val sessionManager = SessionManager(context)
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference.child("coordinate")

    override fun doWork(): Result {
        val latitude = sessionManager.getLatitude()
        val longitude = sessionManager.getLongitude()

        if (sessionManager.isCheckedIn()) {
            sendLocationToFirebase(latitude!!, longitude!!)
        }

        return Result.success()
    }

    private fun sendLocationToFirebase(latitude: Double, longitude: Double) {
        val userId = sessionManager.fetchUserId()!!
        val userLocationRef = database.child(userId)

        userLocationRef.get().addOnSuccessListener { snapshot ->
            val locationCount = snapshot.childrenCount
            val newLocationKey = "location_${locationCount + 1}"

            val locationData = mapOf(
                "latitude" to latitude,
                "longitude" to longitude,
                "timestamp" to System.currentTimeMillis()
            )

            userLocationRef.child(newLocationKey).setValue(locationData)
                .addOnSuccessListener {
                    Log.d("Firebase", "Location data sent successfully.")
                }
                .addOnFailureListener { e ->
                    Log.e("Firebase", "Failed to send location data.", e)
                }
        }
    }
}
