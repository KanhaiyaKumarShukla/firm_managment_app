package com.rach.firmmanagement.firmAdminOwner

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.ui.graphics.Color
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.maps.android.compose.*
import com.rach.firmmanagement.dataClassImp.GeofenceItems
import com.rach.firmmanagement.geofencing.GeofenceHelper
import com.rach.firmmanagement.viewModel.GeofenceViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.Q)
@SuppressLint("MissingPermission", "UnrememberedMutableState")
@Composable
fun AddGeofenceByMap(activity: ComponentActivity, viewModel: GeofenceViewModel = viewModel()) {
    var hasLocationPermission by remember { mutableStateOf(false) }
    var hasBackgroundLocationPermission by remember { mutableStateOf(false) }
    var isBatteryOptimizationIgnored by remember { mutableStateOf(false) }
    var mapFocusLocation by remember { mutableStateOf<LatLng?>(null) }
    val powerManager = activity.getSystemService(Context.POWER_SERVICE) as PowerManager
    val context = LocalContext.current
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
    val geofences by viewModel.geofences.collectAsState()
    val cameraPositionState = rememberCameraPositionState {
        Log.d("Geofence", "Geofences camera: $geofences")
        position = CameraPosition.fromLatLngZoom(
            LatLng(28.6139, 77.2090) // New Delhi
            , 18f
        )
    }
    val geofenceHelper = remember { GeofenceHelper(activity) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedLatLng by remember { mutableStateOf<LatLng?>(null) }
    var showBatteryOptimizationDialog by remember { mutableStateOf(false) }
    // Check and request permissions
    LaunchedEffect(Unit) {
        viewModel.fetchAllGeofences()
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            hasLocationPermission = true
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

        // Check if battery optimization is ignored
        isBatteryOptimizationIgnored = powerManager.isIgnoringBatteryOptimizations(activity.packageName)
        if (!isBatteryOptimizationIgnored) {
            showBatteryOptimizationDialog = true
        }

    }

    if (showBatteryOptimizationDialog) {
        AlertDialog(
            onDismissRequest = { showBatteryOptimizationDialog = false },
            title = { Text("Allow Background Access") },
            text = { Text("For geofences to work correctly, please allow unrestricted background access for this app.") },
            confirmButton = {
                Button(onClick = {
                    val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                    activity.startActivity(intent)
                    showBatteryOptimizationDialog = false
                }) {
                    Text("Open Settings")
                }
            },
            dismissButton = {
                Button(onClick = { showBatteryOptimizationDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    LaunchedEffect(geofences) {
        if (geofences.isNotEmpty()) {
            val firstGeofence = geofences.first()
            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                LatLng(firstGeofence.latitude!!.toDouble(), firstGeofence.longitude!!.toDouble()),
                18f
            )
        }
    }
    // we can use google map search bar if places api  is enabled which require billing
    /*
    Scaffold(
        topBar = {
            GoogleMapSearchBar(onPlaceSelected = { latLng ->
                mapFocusLocation = latLng
                cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 12f)
            })
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {

     */
            if (hasLocationPermission) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    uiSettings = MapUiSettings(myLocationButtonEnabled = true),
                    properties = MapProperties(isMyLocationEnabled = true),
                    onMapLongClick = { latLng ->
                        selectedLatLng = latLng
                        showDialog = true
                    }
                ) {
                    geofences.forEach { geofence ->
                        Log.d("Geofence", "Geofence: $geofence")
                        val position = LatLng(geofence.latitude!!.toDouble(), geofence.longitude!!.toDouble())
                        Marker(
                            state = MarkerState(position = position),
                            title = geofence.title,
                            snippet = "Radius: ${geofence.radius} meters"
                        )
                        com.google.maps.android.compose.Circle(
                            center = position,
                            radius = geofence.radius!!.toDouble(), // Radius in meters
                            strokeColor = Color.Red,
                            strokeWidth = 2f,
                            fillColor = Color(0x44FF0000) // Semi-transparent red
                        )
                        geofenceHelper.addGeofence(
                            location = position,
                            radius = geofence.radius.toDouble(),
                            requestId = geofence.title.toString()
                        )
                        activity.lifecycleScope.launch {
                            saveGeofenceSharedPreference(
                                context,
                                geofence
                            )
                        }
                    }
                }
            }

            if (showDialog) {
                AddGeofenceDialog(
                    latitude = selectedLatLng?.latitude.toString(),
                    longitude = selectedLatLng?.longitude.toString(),
                    onDismiss = { showDialog = false },
                    onSave = { title, radius ->
                        selectedLatLng?.let { latLng ->
                            viewModel.onButtonStateChange(true)
                            if (title.isNotEmpty() && radius.isNotEmpty()) {
                                viewModel.saveGeofence(
                                    latitude = latLng.latitude.toString(),
                                    longitude = latLng.longitude.toString(),
                                    radius = radius,
                                    title = title,
                                    employeeNo = "All"
                                )

                                viewModel.addGeofence(
                                    GeofenceItems(
                                        title = title,
                                        latitude = latLng.latitude.toString(),
                                        longitude = latLng.longitude.toString(),
                                        radius = radius,
                                        adminNo = "",
                                        empNo = ""
                                    )
                                )
                                geofenceHelper.addGeofence(
                                    location = latLng,
                                    radius = radius.toDouble(),
                                    requestId = title
                                )
                                activity.lifecycleScope.launch {

                                    saveGeofenceSharedPreference(
                                        context,
                                        GeofenceItems(
                                            title = title,
                                            latitude = latLng.latitude.toString(),
                                            longitude = latLng.longitude.toString(),
                                            radius = radius,
                                            adminNo = "",
                                            empNo = ""
                                        )
                                    )
                                }
                            } else {
                                Toast.makeText(
                                    activity,
                                    "Please fill all fields",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        showDialog = false
                    }
                )
            }
    //    }
    //}
}

suspend fun saveGeofenceSharedPreference(context: Context, geofenceData: GeofenceItems) {
    withContext(Dispatchers.IO) {
        val prefs = context.getSharedPreferences("Geofences", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString(
            geofenceData.title,
            "${geofenceData.latitude},${geofenceData.longitude},${geofenceData.radius}, ${geofenceData.adminNo}, ${geofenceData.empNo}, ${geofenceData.title}"
        )
        editor.apply()
    }
}

fun getSavedGeofences(context: Context): List<GeofenceItems> {
    val prefs = context.getSharedPreferences("Geofences", Context.MODE_PRIVATE)
    return prefs.all.map { entry ->
        val parts = entry.value.toString().split(",")
        GeofenceItems(
            title = entry.key,
            latitude = parts[0],
            longitude = parts[1],
            radius = parts[2],
            adminNo = parts[3],
            empNo = parts[4]
        )
    }
}
fun restoreGeofences(context: Context) {
    val savedGeofences = getSavedGeofences(context)
    val geofenceHelper = GeofenceHelper(context as Activity)
    Log.d("geofenceRestored" , savedGeofences.toString())
    savedGeofences.forEach { geofenceData ->
        geofenceHelper.addGeofence(
            location = LatLng(geofenceData.latitude!!.toDouble(), geofenceData.longitude!!.toDouble()),
            radius = geofenceData.radius!!.toDouble(),
            requestId = geofenceData.title.toString()
        )
    }
}


@Composable
fun GoogleMapSearchBar(onPlaceSelected: (LatLng) -> Unit) {
    val context = LocalContext.current
    var query by remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val place = Autocomplete.getPlaceFromIntent(result.data!!)
            place.latLng?.let { onPlaceSelected(it) }
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.weight(1f),
            label = { Text("Search location") },
            singleLine = true
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = {
            val intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY,
                listOf(Place.Field.LAT_LNG)
            ).build(context)
            launcher.launch(intent)
        }) {
            Text("Search")
        }
    }
}

@Composable
fun AddGeofenceDialog(
    latitude: String,
    longitude: String,
    onDismiss: () -> Unit,
    onSave: (title: String, radius: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var radius by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = { onSave(title, radius) }) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text("Add New Work Center") },
        text = {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "Please Fill Your Details", style = MaterialTheme.typography.body1)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = latitude,
                    onValueChange = {},
                    label = { Text("Latitude") },
                    readOnly = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = longitude,
                    onValueChange = {},
                    label = { Text("Longitude") },
                    readOnly = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = radius,
                    onValueChange = { radius = it },
                    label = { Text("Radius") },
                    singleLine = true
                )
            }
        }
    )
}
