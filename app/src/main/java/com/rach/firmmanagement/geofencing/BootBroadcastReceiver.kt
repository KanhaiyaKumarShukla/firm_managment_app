package com.rach.firmmanagement.geofencing

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.rach.firmmanagement.repository.GeofenceRepository

class BootBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            val repository = GeofenceRepository()
            Log.d("GeofenceBootReceiver", "Boot completed received.")
            // Fetch all geofences for the current admin (adjust logic as needed)
            val adminPhoneNumber = FirebaseAuth.getInstance().currentUser?.phoneNumber.toString()
            repository.getAllGeofences(
                adminPhoneNumber,
                onSuccess = { geofenceList ->
                    val geofenceHelper = GeofenceHelper(context)

                    // Add all geofences back
                    geofenceList.forEach { geofence ->
                        val location = LatLng(
                            geofence.latitude!!.toDouble(),
                            geofence.longitude!!.toDouble()
                        )
                        geofenceHelper.addGeofence(
                            location = location,
                            radius = geofence.radius!!.toDouble() ,
                            requestId = geofence.title.toString()
                        )
                    }
                },
                onFailure = {
                    Log.e("GeofenceBootReceiver", "Failed to fetch geofences on boot.")
                }
            )
        }
    }
}
