package com.rach.firmmanagement.geofencing

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.rach.firmmanagement.notification.MyNotification

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        Toast.makeText(context, "Geofence triggered...", Toast.LENGTH_SHORT).show();
        Log.d("GeofenceReceiver", "Geofence triggered...")
        if (geofencingEvent?.hasError() == true) {
            Log.e("GeofenceReceiver", "Error in geofencing event: ${geofencingEvent.errorCode}")
            return
        }

        // Get the geofence transition type
        val geofenceTransition = geofencingEvent?.geofenceTransition
        val triggeringGeofences = geofencingEvent?.triggeringGeofences

        val geofenceIds = triggeringGeofences?.joinToString(", ") { it.requestId }

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