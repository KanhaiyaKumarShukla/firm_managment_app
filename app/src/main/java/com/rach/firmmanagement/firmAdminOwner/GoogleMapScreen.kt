package com.rach.firmmanagement.firmAdminOwner

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.rach.firmmanagement.geofencing.GeofenceHelper

@RequiresApi(Build.VERSION_CODES.Q)
@SuppressLint("MissingPermission", "UnrememberedMutableState") // We'll handle permissions manually
@Composable
fun GoogleMapScreen(activity: ComponentActivity) {
    // Define the starting location
    val startLocation = LatLng(37.7749, -122.4194) // San Francisco

    // State to handle permission status
    var hasLocationPermission by remember { mutableStateOf(false) }
    var hasBackgroundLocationPermission by remember { mutableStateOf(false) }

    val radius=20.0
    // Launcher for requesting permissions
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
            hasBackgroundLocationPermission =
                permissions[Manifest.permission.ACCESS_BACKGROUND_LOCATION] == true
            Log.d("PermissionCheck", "Fine location permission: $hasLocationPermission")
            Log.d("PermissionCheck", "Background location permission: $hasBackgroundLocationPermission")

        }
    )

    // Check and request location permissions
    LaunchedEffect(Unit) {
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            hasLocationPermission = true
            Log.d("PermissionCheck", "ACCESS_FINE_LOCATION granted")
        } else {
            Log.d("PermissionCheck", "ACCESS_FINE_LOCATION not granted, requesting permission")
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }

        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            hasBackgroundLocationPermission = true
            Log.d("PermissionCheck", "ACCESS_BACKGROUND_LOCATION granted")
        } else {
            Log.d("PermissionCheck", "ACCESS_BACKGROUND_LOCATION not granted, requesting permission")

            locationPermissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            )
        }
    }

    // Camera position state
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(startLocation, 16f)
    }

    val markers = remember { mutableStateListOf<LatLng>() }
    val geofenceHelper = remember { GeofenceHelper(activity) }

    if (hasLocationPermission) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(myLocationButtonEnabled = true),
            properties = MapProperties(isMyLocationEnabled = true),
            onMapLongClick = { latLng ->
                // Add a marker at the clicked position
                markers.add(latLng)
                geofenceHelper.addGeofence(
                    location = latLng,
                    radius = radius,
                    requestId = latLng.toString()
                )
            }
        ) {
            // Add a marker
            markers.forEach { position ->
                Marker(
                    state = MarkerState(position = position),
                    title = "Marker",
                    snippet = "Scheduled Geofence"
                )
                com.google.maps.android.compose.Circle(
                    center = position,
                    radius = radius, // Radius in meters
                    strokeColor = Color.Red,
                    strokeWidth = 2f,
                    fillColor = Color(0x44FF0000) // Semi-transparent red
                )
            }
        }
    } else {
        // Show some UI to indicate permissions are required
    }
}
