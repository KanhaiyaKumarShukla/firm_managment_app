package com.rach.firmmanagement.geofencing

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import com.rach.firmmanagement.notification.MyNotification
import android.Manifest

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    companion object {
        const val ACTION_GEOFENCE_EVENT = "android.intent.action.GEOFENCE_EVENT"
    }
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("GeofenceReceiver", "Broadcast received")
        if (intent.action == ACTION_GEOFENCE_EVENT) {
            Log.d("GeofenceReceiver", "Geofence event received")
        } else {
            Log.e("GeofenceReceiver", "Unexpected intent action: ${intent.action}")
        }
        Log.d("GeofenceReceiver", "Intent extras: ${intent.extras}")
        Log.d("GeofenceReceiver", "Intent action: ${intent.action}")


        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        Toast.makeText(context, "Geofence triggered...", Toast.LENGTH_SHORT).show();
        Log.d("GeofenceReceiver", "Geofence triggered...")
        if (geofencingEvent == null || geofencingEvent.hasError() == true) {
            val errorMessage = geofencingEvent?.errorCode?.let {
                GeofenceStatusCodes.getStatusCodeString(it)
            } ?: "Unknown error"
            Log.e("GeofenceReceiver", "GeofencingEvent error: $errorMessage")
            return
        }else{
            Log.d("GeofenceReceiver", "Geofencing event received: $geofencingEvent")
            Log.d("GeofenceReceiver", "Transition: ${geofencingEvent.geofenceTransition}")
            geofencingEvent.triggeringGeofences?.forEach { geofence ->
                Log.d("GeofenceReceiver", "Geofence ID: ${geofence.requestId}")
            }
        }

        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("GeofenceReceiver", "Location permission not granted.")
            return
        }


        // Get the geofence transition type
        val geofenceTransition = geofencingEvent.geofenceTransition
        val triggeringGeofences = geofencingEvent.triggeringGeofences

        val geofenceIds = triggeringGeofences?.joinToString(", ") { it.requestId }
        Log.d("GeofenceReceiver", "Transition: $geofenceTransition, Geofence IDs: $geofenceIds")

        when (geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                Log.d("GeofenceReceiver", "Entered geofence(s): $geofenceIds")
            }
            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                Log.d("GeofenceReceiver", "Exited geofence(s): $geofenceIds")
            }
            else -> {
                Log.e("GeofenceReceiver", "Unknown geofence transition: $geofenceTransition")
            }
        }
        val notification = MyNotification(
            context = context,
            title = "Firm Management App",
            message = when (geofenceTransition) {
                Geofence.GEOFENCE_TRANSITION_ENTER -> "Entered Geofence"
                Geofence.GEOFENCE_TRANSITION_EXIT -> "Exited Geofence"
                else -> "Unknown Geofence Event"
            }
        )
        notification.fireNotification()
    }
}