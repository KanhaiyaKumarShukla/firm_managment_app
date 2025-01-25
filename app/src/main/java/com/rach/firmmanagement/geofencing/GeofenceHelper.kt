package com.rach.firmmanagement.geofencing

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

/*
import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import android.os.Handler
import android.os.Looper
import android.util.Log

class GeofenceHelper(private val context: Context) {
    private val geofencingClient: GeofencingClient =
        LocationServices.getGeofencingClient(context)
    private val geofencePendingIntent: PendingIntent by lazy { createGeofencePendingIntent() }

    private val handler = Handler(Looper.getMainLooper())

    // Add a geofence at the specified location
    fun addGeofence(location: LatLng, radius: Double, requestId: String) {
        val geofence = Geofence.Builder()
            .setRequestId(requestId)
            .setCircularRegion(location.latitude, location.longitude, radius.toFloat())
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .build()

        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent)
        }
    }

    // Remove a geofence
    fun removeGeofence(requestId: String) {
        geofencingClient.removeGeofences(listOf(requestId))
    }

    fun scheduleGeofence(
        location: LatLng,
        radius: Double,
        requestId: String,
        startDelay: Long,
        duration: Long
    ) {
        handler.postDelayed({
            addGeofence(location, radius, requestId)
        }, startDelay)

        handler.postDelayed({
            removeGeofence(requestId)
        }, startDelay + duration)
    }
    // Create a PendingIntent for geofence transitions
    private fun createGeofencePendingIntent(): PendingIntent {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        Log.d("TAG", "createGeofencePendingIntent: ")
        return PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    companion object {
        const val GEOFENCE_CHANNEL = "geofence_channel"
    }
}

 */

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng

class GeofenceHelper(private val context: Context) {
    private val geofencingClient: GeofencingClient = LocationServices.getGeofencingClient(context)
    private var pendingIntent: PendingIntent? = null

    // Add a geofence
    fun addGeofence(location: LatLng, radius: Double, requestId: String) {
        Log.d("GeofenceHelper", "Attempting to add geofence at: $location with radius: $radius")
        val geofence = buildGeofence(location, radius, requestId, Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_DWELL or Geofence.GEOFENCE_TRANSITION_EXIT)
        val geofencingRequest = buildGeofencingRequest(geofence)
        val pendingIntent=getGeofencePendingIntent()

        if (hasLocationPermission()) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener {
                    Log.d("GeofenceHelper", "Geofence successfully added for: $requestId")
                }
                .addOnFailureListener { exception ->
                    Log.e("GeofenceHelper", "Failed to add geofence: ${exception.message}", exception)
                }
        } else {
            Log.e("GeofenceHelper", "Location permission not granted.")
        }
    }

    // Remove a geofence by request ID
    fun removeGeofence(requestId: String) {
        Log.d("GeofenceHelper", "Attempting to remove geofence for: $requestId")
        geofencingClient.removeGeofences(listOf(requestId))
            .addOnSuccessListener {
                Log.d("GeofenceHelper", "Geofence successfully removed for: $requestId")
            }
            .addOnFailureListener { exception ->
                Log.e("GeofenceHelper", "Failed to remove geofence: ${exception.message}", exception)
            }
    }

    // Build the geofence
    private fun buildGeofence(location: LatLng, radius: Double, requestId: String, transitionTypes:Int): Geofence {
        Log.d("GeofenceHelper", "Building geofence for: $requestId, $transitionTypes")
        return Geofence.Builder()
            .setRequestId(requestId)
            .setCircularRegion(location.latitude, location.longitude, radius.toFloat())
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(transitionTypes)
            .setLoiteringDelay(10000)
            .build()
    }

    // Build the geofencing request
    private fun buildGeofencingRequest(geofence: Geofence): GeofencingRequest {
        Log.d("GeofenceHelper", "Building geofencing request")
        return GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()
    }

    // Get or create a pending intent for geofence transitions
    @SuppressLint("UnspecifiedImmutableFlag")
    private fun getGeofencePendingIntent(): PendingIntent {
        if (pendingIntent == null) {
            Log.d("GeofenceHelper", "Creating new PendingIntent for geofence transitions")
            val intent = Intent(context, GeofenceBroadcastReceiver::class.java).apply {
                action = GeofenceBroadcastReceiver.ACTION_GEOFENCE_EVENT
            }

            pendingIntent = PendingIntent.getBroadcast(
                context,
                2607,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            Log.d("GeofenceHelper", "Reusing existing PendingIntent")
        }
        return pendingIntent!!
    }

    fun getErrorString(e: Exception): String {
        return if (e is ApiException) {
            when (e.statusCode) {
                GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE -> "GEOFENCE_NOT_AVAILABLE"
                GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES -> "GEOFENCE_TOO_MANY_GEOFENCES"
                GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS -> "GEOFENCE_TOO_MANY_PENDING_INTENTS"
                else -> e.localizedMessage
            }
        } else {
            e.localizedMessage
        }
    }

    // Helper method to check location permission
    private fun hasLocationPermission(): Boolean {
        val fineLocationPermission = android.Manifest.permission.ACCESS_FINE_LOCATION
        val permissionState = context.checkSelfPermission(fineLocationPermission)
        return permissionState == android.content.pm.PackageManager.PERMISSION_GRANTED
    }
}
