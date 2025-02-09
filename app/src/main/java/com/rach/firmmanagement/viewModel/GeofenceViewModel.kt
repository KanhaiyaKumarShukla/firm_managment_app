package com.rach.firmmanagement.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.rach.firmmanagement.dataClassImp.AddStaffDataClass
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

    fun saveGeofence(
        title:String, latitude: String, longitude: String, radius: String, employeeIdentity: AddStaffDataClass,
        onSuccess: (GeofenceItems) -> Unit,
        onFailure: () -> Unit
    ) {

        val geofenceData = GeofenceItems(
            title=title,
            longitude = longitude,
            latitude = latitude,
            radius = radius,
            adminNo = employeeIdentity.adminNumber,
            firmName = employeeIdentity.firmName,
        )

        repository.saveGeofence(geofenceData, onSuccess, onFailure)
    }


    private val _geofences = MutableStateFlow<List<GeofenceItems>>(emptyList())
    val geofences: StateFlow<List<GeofenceItems>> = _geofences
    fun addGeofence(geofence: GeofenceItems) {
        geofence.adminNo = adminPhoneNumber
        _geofences.value = _geofences.value + geofence
    }
    fun fetchAllGeofences(firmName:String) {

        viewModelScope.launch {
            repository.getAllGeofences(
                firmName,
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