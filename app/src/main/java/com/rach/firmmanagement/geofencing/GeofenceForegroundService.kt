package com.rach.firmmanagement.geofencing

import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.rach.firmmanagement.R
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log

class GeofenceForegroundService : Service() {

    private lateinit var geofenceHelper: GeofenceHelper

    override fun onCreate() {
        super.onCreate()
        geofenceHelper = GeofenceHelper(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            stopSelf() // Stop service if permissions are missing
            return START_NOT_STICKY
        }
        // Extract geofence data from the intent
        val geofenceId = intent?.getStringExtra("geofence_id") ?: return START_NOT_STICKY
        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)
        val radius = intent.getFloatExtra("radius", 100f)

        Log.d("Geofence", "Received geofence data: $geofenceId, $latitude, $longitude, $radius")
        val channelId = "com.rach.firmmanagement.CHANNEL_ID"
        val channelName = "Foreground Service"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        // Start foreground notification
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Geofence Service Running")
            .setContentText("Monitoring geofence: $geofenceId")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        try {
            startForeground(1, notification)
        } catch (e: Exception) {
            Log.e("ForegroundService", "Failed to start foreground: ${e.message}")
        }


        // Add the geofence
        geofenceHelper.addGeofence(
            location = LatLng(latitude, longitude),
            radius = radius.toDouble(),
            requestId = geofenceId
        )

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null
}

