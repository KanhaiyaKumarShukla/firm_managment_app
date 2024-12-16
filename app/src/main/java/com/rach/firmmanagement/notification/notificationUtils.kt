package com.rach.firmmanagement.notification

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.rach.firmmanagement.dataClassImp.LocationData
import com.rach.firmmanagement.viewModel.EmlAllTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class NotificationUtils(
    private val context: Context,
    private val scope: CoroutineScope
) {

    private val _fusedLocationServerClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)


    @SuppressLint("MissingPermission")
    fun requestLocationUpdates(viewModel: EmlAllTask) {

        scope.launch(Dispatchers.IO) {
            val locationCallback = object : LocationCallback() {

                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    locationResult.lastLocation?.let {
                        val location = LocationData(
                            latitude = it.latitude,
                            longitude = it.longitude
                        )
                        viewModel.onChangeLocation(location)
                    }
                }

            }

            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 1000
            ).build()

            _fusedLocationServerClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    fun hasPermissionNotification(context: Context): Boolean {

        // For Android 13+
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) ==
                    PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED

        } else {
            TODO("VERSION.SDK_INT < TIRAMISU")
        }

    }

    suspend fun geoLocationConverter(locationData: LocationData, context: Context): String  {
        return withContext(Dispatchers.IO) {
            val geocoder = Geocoder(context, Locale.getDefault())
            val coordinates = LatLng(locationData.latitude, locationData.longitude)
            val addresses: MutableList<Address>? =
                geocoder.getFromLocation(coordinates.latitude, coordinates.longitude, 1)

            if (addresses?.isNotEmpty() == true) {
                addresses[0].getAddressLine(0)
            } else {
                "Address Not Found"
            }
        }
    }


}

