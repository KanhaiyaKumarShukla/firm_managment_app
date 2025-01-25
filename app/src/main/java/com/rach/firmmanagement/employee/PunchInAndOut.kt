package com.rach.firmmanagement.employee

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.LatLng
import com.rach.firmmanagement.HomeScreen.EditDialog
import com.rach.firmmanagement.MainActivity
import com.rach.firmmanagement.dataClassImp.GeofenceItems
import com.rach.firmmanagement.geofencing.GeofenceForegroundService
import com.rach.firmmanagement.geofencing.GeofenceHelper
import com.rach.firmmanagement.notification.NotificationUtils
import com.rach.firmmanagement.ui.theme.blueAcha
import com.rach.firmmanagement.ui.theme.fontBablooBold
import com.rach.firmmanagement.ui.theme.progressBarBgColor
import com.rach.firmmanagement.viewModel.EmlAllTask
import com.rach.firmmanagement.viewModel.GeofenceViewModel
import com.rach.firmmanagement.viewModel.LoginViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
/*
@Composable
fun PunchInOutApp(
    viewModel: EmlAllTask,
    loginViewModel: LoginViewModel,
    navigateToEmployeeAttendence: () -> Unit
) {

    val context = LocalContext.current

    val gola by viewModel.gola.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        if (gola) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = progressBarBgColor.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

        } else {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFFf0f0f0)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CurrentTimeDisplay()
                    PunchInOutButtons(
                        context = context,
                        viewModel = viewModel,
                        loginViewModel = loginViewModel
                    )
                    ViewHistorySection( navigateToEmployeeAttendence)
                }
            }
        }
    }
}
*/

@Composable
fun CurrentTimeDisplay() {
    val currentTime = remember { mutableStateOf(getCurrentTime()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime.value = getCurrentTime()
            delay(1000L)
        }
    }

    Text(
        text = currentTime.value,
        fontSize = 40.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(16.dp),
        textAlign = TextAlign.Center
    )
}
/*
@Composable
fun PunchInOutButtons(
    context: Context,
    viewModel: EmlAllTask,
    loginViewModel: LoginViewModel
) {
    val adminPhoneNumber by loginViewModel.firmOwnerNumber.collectAsState()
    val name by viewModel.employees.collectAsState()

    val nameHai = name.firstOrNull()?.name


    val scope = rememberCoroutineScope()
    val locationUtils = NotificationUtils(context, scope)

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            // Request location updates if permissions are granted
        }
    )

    val notification = NotificationUtils(context = context, scope = scope)
    val progressState by viewModel.progressBarState.collectAsState()
    val location by viewModel.location.collectAsState()

    var isDialogOpen by remember { mutableStateOf(false) }
    var fieldToEdit ="Add Out for Work"
    var updatedValue by remember { mutableStateOf("") }
    var currentOutForWork =0  // have to update


    var address by remember { mutableStateOf("Fetching location...") }

    LaunchedEffect(location) {
        location?.let {
            address = locationUtils.geoLocationConverter(it, context)
        }
    }
    if (isDialogOpen) {
        EditDialog(
            fieldName = fieldToEdit,
            initialValue = updatedValue,
            onConfirm = {
                // call for out for work update.
                isDialogOpen = false
            },
            onDismiss = {
                isDialogOpen = false
            }
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (progressState) {
            CircularProgressIndicator(
                modifier = Modifier.padding(16.dp)
            )
        }

        location?.let {
            Text(
                text = "Location: ${it.latitude}, ${it.longitude}, $address $nameHai",
                modifier = Modifier.padding(16.dp)
            )
            viewModel.onChangeProgressBarState(false)
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Punch In Button
            Button(
                onClick = {
                    scope.launch {
                        if (notification.hasPermissionNotification(context)) {
                            Log.d("Att", "permission granted")
                            viewModel.onChangeProgressBarState(true)
                            locationUtils.requestLocationUpdates(viewModel = viewModel)
                            // change address != "Location not available" to
                            Log.d("Att", "$adminPhoneNumber, $address, $nameHai, $location")
                            if (location != null && address != "Address Not Found") {
                                if (nameHai != null) {
                                    viewModel.punchIn(
                                        adminPhoneNumber = adminPhoneNumber,
                                        name = nameHai, // Use the passed employee name
                                        location = address.toString(),
                                        onSuccess = {
                                            Toast.makeText(
                                                context,
                                                "Mark Present",
                                                Toast.LENGTH_LONG
                                            )
                                                .show()
                                        },
                                        onFailure = {
                                            Toast.makeText(context, "Absent", Toast.LENGTH_LONG)
                                                .show()
                                        }
                                    )
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Waiting for location data...",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Log.d("Att", "permission not granted")
                            requestPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                )
                            )
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(Color(0xFF6C63FF)),
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
                    .height(60.dp),
                shape = RoundedCornerShape(30.dp)
            ) {
                Text("Punch In", fontSize = 18.sp, color = Color.White)
            }

            // Punch Out Button
            Button(
                onClick = {
                    scope.launch {
                        viewModel.punchOut(
                            adminPhoneNumber = adminPhoneNumber,
                            onSuccess = {
                                Toast.makeText(context, "Punch Out Successfully", Toast.LENGTH_LONG)
                                    .show()
                            },
                            onFailure = {
                                Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_LONG)
                                    .show()
                            }
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(Color(0xFFFF6F61)),
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
                    .height(60.dp),
                shape = RoundedCornerShape(30.dp)
            ) {
                Text("Punch Out", fontSize = 18.sp, color = Color.White)
            }
        }

        OutOfWorkPortion(viewModel,adminPhoneNumber, nameHai.toString(), context)

    }
}
*/

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun PunchInOutApp(
    viewModel: EmlAllTask,
    loginViewModel: LoginViewModel,
    navigateToEmployeeAttendence: () -> Unit,
) {
    val context = LocalContext.current
    val activity = context as Activity
    val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager

    val gola by viewModel.gola.collectAsState()
    var hasLocationPermission by remember { mutableStateOf(false) }
    var hasBackgroundLocationPermission by remember { mutableStateOf(false) }
    var isBatteryOptimizationIgnored by remember { mutableStateOf(false) }
    var showBatteryOptimizationDialog by remember { mutableStateOf(false) }

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

    LaunchedEffect(Unit) {

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
        } else {
            locationPermissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            )
        }

        isBatteryOptimizationIgnored = powerManager.isIgnoringBatteryOptimizations(activity.packageName)
        if (!isBatteryOptimizationIgnored) {
            showBatteryOptimizationDialog = true
            Log.d("PermissionCheck", "Battery optimization not ignored")
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (gola) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = progressBarBgColor.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFFf0f0f0)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CurrentTimeDisplay()
                    PunchInOutButtons(
                        context = context,
                        viewModel = viewModel,
                        loginViewModel = loginViewModel,
                        hasLocationPermission = hasLocationPermission,
                        hasBackgroundLocationPermission = hasBackgroundLocationPermission
                    )
                    ViewHistorySection(navigateToEmployeeAttendence)
                }
            }
        }
    }
}
@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun PunchInOutButtons(
    context: Context,
    viewModel: EmlAllTask,
    loginViewModel: LoginViewModel,
    hasLocationPermission: Boolean,
    hasBackgroundLocationPermission: Boolean
) {
    val scope = rememberCoroutineScope()
    val geofenceHelper = remember { GeofenceHelper(context) }
    val geofences by viewModel.geofences.collectAsState()
    val currentLocation by viewModel.location.collectAsState()
    var showAlertDialog by remember { mutableStateOf(false) }
    var alertMessage by remember { mutableStateOf("") }
    val adminPhoneNumber by loginViewModel.firmOwnerNumber.collectAsState()
    val nameHai by viewModel.employees.collectAsState()
    var address by remember { mutableStateOf("Fetching location...") }
    var nearByGeofence by remember { mutableStateOf<GeofenceItems?>(null) }
    val progressState by viewModel.progressBarState.collectAsState()
    val locationUtils = NotificationUtils(context, scope)
    val notification = NotificationUtils(context = context, scope = scope)

    LaunchedEffect(currentLocation) {
        currentLocation?.let {
            address = locationUtils.geoLocationConverter(it, context)
        }
    }
    LaunchedEffect(nearByGeofence) {
        Log.d("Punch", "Geofence: $nearByGeofence")
    }
    LaunchedEffect(Unit) {
        Log.d("Punch", "Fetching geofences...")
        viewModel.getGeofence(adminPhoneNumber)
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.e("GeofenceHelper", "Location services are disabled.")
        }else{
            Log.d("GeofenceHelper", "Location services are enabled.")
        }

    }
    /*
    LaunchedEffect(currentLocation) {
        Log.d("Punch", "Location: $currentLocation")
        currentLocation?.let { location ->
            geofences.firstOrNull { geofence ->
                val distance = FloatArray(1)
                Location.distanceBetween(
                    location.latitude,
                    location.longitude,
                    geofence.latitude?.toDouble() ?: 0.0,
                    geofence.longitude?.toDouble() ?: 0.0,
                    distance
                )
                distance[0] <= (geofence.radius?.toDouble() ?: 0.0)
            }?.let { nearbyGeofence ->
                nearByGeofence=nearbyGeofence
                alertMessage = "You are punching in from ${nearbyGeofence.title}. Are you sure?"
            } ?: run {
                alertMessage = "UNKNOWN LOCATION: ${location.latitude}, ${location.longitude}"
            }
        }
    }

     */


    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        if (progressState) {
            CircularProgressIndicator(
                modifier = Modifier.padding(16.dp)
            )
        }

        currentLocation?.let {
            Text(
                text = "Location: ${it.latitude}, ${it.longitude}, $address",
                modifier = Modifier.padding(16.dp)
            )
            viewModel.onChangeProgressBarState(false)
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Punch In Button
            Button(
                onClick = {
                    scope.launch {
                        if (notification.hasPermissionNotification(context)) {
                            Log.d("Att", "permission granted")
                            viewModel.onChangeProgressBarState(true)
                            locationUtils.requestLocationUpdates(viewModel = viewModel)
                            if (hasLocationPermission && hasBackgroundLocationPermission) {
                                if (currentLocation != null) {
                                    showAlertDialog = true
                                    nearByGeofence=getGeofenceForCurrentLocation(geofences,currentLocation!!.latitude,currentLocation!!.longitude)
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Fetching location. Please wait.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Location permissions are required.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(Color(0xFF6C63FF)),
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
                    .height(60.dp),
                shape = RoundedCornerShape(30.dp)
            ) {
                Text("Punch In")
            }

            // Punch Out Button
            Button(
                onClick = {
                    scope.launch {
                        viewModel.punchOut(
                            adminPhoneNumber = adminPhoneNumber,
                            onSuccess = {
                                Toast.makeText(context, "Punch Out Successfully", Toast.LENGTH_LONG)
                                    .show()
                            },
                            onFailure = {
                                Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_LONG)
                                    .show()
                            }
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(Color(0xFFFF6F61)),
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
                    .height(60.dp),
                shape = RoundedCornerShape(30.dp)
            ) {
                Text("Punch Out", fontSize = 18.sp, color = Color.White)
            }
        }

        OutOfWorkPortion(viewModel,adminPhoneNumber, nameHai.toString(), context)

        if (showAlertDialog) {

            alertMessage = if(nearByGeofence!=null){
                Log.d("Punch", "Location geofence: $nearByGeofence")
                "You are punching in from ${nearByGeofence!!.title}. Are you sure?"
            }else {
                Log.d("Punch", "Location--: ${currentLocation?.latitude}, ${currentLocation?.longitude}")
                "UNKNOWN LOCATION: ${currentLocation?.latitude}, ${currentLocation?.longitude}"
            }
            AlertDialog(
                onDismissRequest = { showAlertDialog = false },
                title = { Text("Punch In Confirmation") },
                text = { Text(alertMessage) },
                confirmButton = {
                    Button(onClick = {
                        showAlertDialog = false
                        val geofence=if(nearByGeofence!=null){
                            nearByGeofence!!
                        }else{
                            GeofenceItems(

                                latitude = currentLocation!!.latitude.toString(),
                                longitude = currentLocation!!.longitude.toString(),
                                radius = "",
                                title = "Unknown: $address",
                                adminNo = adminPhoneNumber,
                            )
                        }
                        val intent = Intent(context, GeofenceForegroundService::class.java).apply {
                            putExtra("geofence_id", geofence.title!!)
                            putExtra("latitude", geofence.latitude!!.toDouble())
                            putExtra("longitude", geofence.longitude!!.toDouble())
                            putExtra("radius", geofence.radius!!.toFloat())
                        }
                        ContextCompat.startForegroundService(context, intent)

                        Toast.makeText(context, "Punch In Successful", Toast.LENGTH_SHORT).show()
                        viewModel.punchIn(
                            adminPhoneNumber = adminPhoneNumber,
                            name = nameHai.firstOrNull()?.name.toString()  , // Use the passed employee name
                            location = geofence,
                            onSuccess = {
                                Toast.makeText(
                                    context,
                                    "Mark Present",
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                            },
                            onFailure = {
                                Toast.makeText(context, "Absent", Toast.LENGTH_LONG)
                                    .show()
                            }
                        )
                    }) {
                        Text("Punch In")
                    }
                },
                dismissButton = {
                    Button(onClick = { showAlertDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutOfWorkPortion(
    viewModel: EmlAllTask,
    adminPhoneNumber:String,
    name: String,
    context: Context
) {
    var textFieldValue by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // TextField
        OutlinedTextField(
            value = textFieldValue,
            onValueChange = { textFieldValue = it },
            label = { Text("Add Out For Work Time in Hours") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            //colors = TextFieldDefaults.outlinedTextFieldColors(),
            shape = RoundedCornerShape(8.dp),
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Button
        Button(
            onClick = {
                scope.launch {
                    viewModel.setOutForWork(
                        adminPhoneNumber = adminPhoneNumber,
                        newValue = textFieldValue.toInt(),
                        name = name,
                        onSuccess = {
                            Toast.makeText(context, "Added Successfully", Toast.LENGTH_LONG)
                                .show()
                        },
                        onFailure = {
                            Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_LONG)
                                .show()
                        }
                    )
                }
            },
            modifier = Modifier.width(120.dp),
            colors = ButtonDefaults.buttonColors(
                blueAcha
            )
        ) {

            Text(
                text = "Save",
                color = Color.White,
                style = fontBablooBold
            )

        }
    }
}


fun getGeofenceForCurrentLocation(
    geofences: List<GeofenceItems>,
    currentUserLatitude: Double,
    currentUserLongitude: Double
): GeofenceItems? {
    Log.d("Punch", "Geofences: $geofences")
    return geofences.find { geofence ->
        val geofenceLatitude = geofence.latitude?.toDoubleOrNull()
        val geofenceLongitude = geofence.longitude?.toDoubleOrNull()
        val geofenceRadius = geofence.radius?.toFloatOrNull()

        if (geofenceLatitude != null && geofenceLongitude != null && geofenceRadius != null) {
            val distance = FloatArray(1)
            Location.distanceBetween(
                currentUserLatitude,
                currentUserLongitude,
                geofenceLatitude,
                geofenceLongitude,
                distance
            )
            Log.d("Punch", "Distance: ${distance[0]}")
            Log.d("Punch", "Radius: $geofenceRadius")
            distance[0] <= geofenceRadius
        } else {
            false
        }
    }
}



@Composable
fun ViewHistorySection(navigateToEmployeeAttendence: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        Text(
            text = "View Attendance History",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Button(
            onClick = {
                /* Navigate to History Screen */
                navigateToEmployeeAttendence()
            },
            colors = ButtonDefaults.buttonColors(
                blueAcha
            )
        ) {

            Text(
                text = "View History",
                color = Color.White,
                style = fontBablooBold
            )

        }
    }
}
fun isFineLocationPermissionGranted(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

/**
 * Checks if the background location permission is granted.
 * @param context The application context.
 * @return True if the permission is granted, otherwise false.
 */
@RequiresApi(Build.VERSION_CODES.Q)
fun isBackgroundLocationPermissionGranted(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

fun getCurrentTime(): String {
    return SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(Date())
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview2() {
    //PunchInOutApp(viewModel = EmlAllTask(), loginViewModel = LoginViewModel(), {}, )
}
