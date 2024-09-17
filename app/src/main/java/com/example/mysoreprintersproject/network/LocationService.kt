package com.example.mysoreprintersproject.network

import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.mysoreprintersproject.R
import com.example.mysoreprintersproject.app.homecontainer.HomeContainerActivity
import com.google.android.gms.location.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LocationUpdateService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var sessionManager: SessionManager
    private lateinit var database: DatabaseReference

    private val notificationId = 1
    private val notificationChannelId = "LocationServiceChannel"
    private val notificationChannelName = "Location Service"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        sessionManager = SessionManager(this)
        database = FirebaseDatabase.getInstance().reference.child("coordinate")

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Register receiver to listen for notification dismissals
        registerReceiver(notificationReceiver, IntentFilter("com.example.ACTION_REPOST_NOTIFICATION"),
            RECEIVER_NOT_EXPORTED
        )

        // Start the service with a persistent notification
        startForeground(notificationId, createPersistentNotification())

        startLocationUpdates()
    }

    private fun createPersistentNotification(): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(notificationChannelId, notificationChannelName, NotificationManager.IMPORTANCE_LOW)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val notificationIntent = Intent(this, HomeContainerActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, notificationChannelId)
            .setContentTitle("Location Service Active")
            .setContentText("Your location is being tracked.")
            .setSmallIcon(R.drawable.ic_location)
            .setContentIntent(pendingIntent)
            .setOngoing(true) // Make the notification persistent
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = 900000 // 15 minutes in milliseconds
            fastestInterval = 900000 // 15 minutes in milliseconds
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }


        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation ?: return
                sessionManager.storeLatitude(location.latitude)
                sessionManager.storeLongitude(location.longitude)
                Log.d("Location Lat and Log", "Lat: ${location.latitude}, Lng: ${location.longitude}")

                if (sessionManager.isCheckedIn()) {
                    sendLocationToFirebase(location.latitude, location.longitude)
                } else {
                    stopSelf() // Stop the service if the user is checked out
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
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

    private val notificationReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "com.example.ACTION_REPOST_NOTIFICATION") {
                Log.d("Location Service", "Notification removed, reposting...")
                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(notificationId, createPersistentNotification())
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(notificationReceiver)
        fusedLocationClient.removeLocationUpdates(locationCallback)
        Log.d("Location Service", "Service destroyed and location updates stopped.")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
