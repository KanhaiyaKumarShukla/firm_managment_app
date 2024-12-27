package com.rach.firmmanagement.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.rach.firmmanagement.dataClassImp.GeofenceItems
import com.rach.firmmanagement.repository.GeofenceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GeofenceViewModel() : ViewModel() {

    val adminPhoneNumber = FirebaseAuth.getInstance().currentUser?.phoneNumber.toString()

    private val _onButtonClicked = MutableStateFlow(false)
    val onButtonClicked: StateFlow<Boolean> = _onButtonClicked
    private val repository: GeofenceRepository= GeofenceRepository()
    fun onButtonStateChange(newState: Boolean) {

        _onButtonClicked.value = newState

    }

    fun saveGeofence(title:String, latitude: String, longitude: String, radius: String, employeeNo: String) {
        val updateAdminNumber = if (adminPhoneNumber.startsWith("+91")) {
            adminPhoneNumber
        } else {
            "+91$adminPhoneNumber"
        }
        val geofenceData = GeofenceItems(
            title=title,
            longitude = longitude,
            latitude = latitude,
            radius = radius,
            adminNo = updateAdminNumber,
            empNo = employeeNo
        )

        repository.saveGeofence(geofenceData)
    }


    private val _geofences = MutableStateFlow<List<GeofenceItems>>(emptyList())
    val geofences: StateFlow<List<GeofenceItems>> = _geofences
    fun addGeofence(geofence: GeofenceItems) {
        geofence.adminNo = adminPhoneNumber
        _geofences.value = _geofences.value + geofence
    }
    fun fetchAllGeofences() {

        viewModelScope.launch {
            repository.getAllGeofences(
                adminPhoneNumber,
                onSuccess = { geofenceList ->
                    _geofences.value = geofenceList
                },
                onFailure = {
                    _geofences.value=emptyList()
                }
            )
        }
    }
}