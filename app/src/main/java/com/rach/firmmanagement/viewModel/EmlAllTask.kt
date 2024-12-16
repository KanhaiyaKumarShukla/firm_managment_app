package com.rach.firmmanagement.viewModel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.rach.firmmanagement.dataClassImp.AddTaskDataClass
import com.rach.firmmanagement.dataClassImp.EmployeeHomeScreenData
import com.rach.firmmanagement.dataClassImp.LocationData
import com.rach.firmmanagement.dataClassImp.OutForWork
import com.rach.firmmanagement.dataClassImp.PunchInPunchOut
import com.rach.firmmanagement.repository.EmployeeRepository
import com.rach.firmmanagement.repository.NoAdminRepository
import com.rach.firmmanagement.repository.PunchInPunchOutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EmlAllTask(
    private val repository: EmployeeRepository = EmployeeRepository()
) : ViewModel() {

    var employeeList = mutableStateOf<List<AddTaskDataClass>>(emptyList())
        private set

    var isLoading = mutableStateOf(false)
        private set


    fun loadAllTask(
        adminPhoneNumber: String
    ) {
        viewModelScope.launch {
            isLoading.value = true
            repository.loadTask(
                adminPhoneNumber = adminPhoneNumber,
                onSuccess = { employee ->
                    employeeList.value = employee
                    isLoading.value = false

                },
                onFailure = {
                    isLoading.value = false
                }
            )
        }
    }

    //Location

    private val _location = MutableStateFlow<LocationData?>(null)
    val location: StateFlow<LocationData?> = _location

    fun onChangeLocation(newLocationData: LocationData) {
        _location.value = newLocationData
    }


    // Employee HomeScreen

    private val _progressState = MutableStateFlow(false)
    val progressBarState: StateFlow<Boolean> = _progressState

    fun onChangeProgressBarState(newState: Boolean) {
        _progressState.value = newState
    }

    private val _employees = MutableStateFlow<List<EmployeeHomeScreenData>>(emptyList())
    val employees: StateFlow<List<EmployeeHomeScreenData>> = _employees

    val repositoryHai = NoAdminRepository()

    fun loadEmployeeData(adminPhoneNumber: String) {

        viewModelScope.launch {

            _progressState.value = true
            repositoryHai.loadEmployee(
                adminPhoneNumber = adminPhoneNumber,
                onSuccess = { employee ->
                    _employees.value = employee
                    _progressState.value = false
                },
                onFailure = {
                    _progressState.value = false
                }
            )

        }

    }


    // PunchInOuTImpious

    val punchRepo = PunchInPunchOutRepository()

    private val phoneNumberCurrent = FirebaseAuth.getInstance().currentUser?.phoneNumber.toString()
    private val formatTime = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
    private val currentTime: String
        get() = formatTime.format(Date())

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val currentDate = dateFormat.format(Date())


    private val _gola = MutableStateFlow(false)
    val gola: StateFlow<Boolean> = _gola

    fun onChangeGola(newGola: Boolean) {
        _gola.value = newGola

    }
    /*
    * change punchin form:
    fun punchIn(
        adminPhoneNumber: String,
        name: String,
        location: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        viewModelScope.launch {
            _gola.value = true
            punchRepo.functionPunchIn(
                adminPhoneNumber = adminPhoneNumber,
                punchInData = PunchInPunchOut(
                    currentTime = currentTime,
                    absent = "Present",
                    date = currentDate,
                    punchTime = currentTime,
                    punchOutTime = null,
                    locationPunchTime = location,
                    name = name,
                    phoneNumberString = phoneNumberCurrent,
                ),
                onSuccess = {
                    _gola.value = false
                    onSuccess()
                },
                onFailure = {
                    _gola.value = false
                    onFailure()
                },
            )
        }
    }
    * to:
    * */


    fun punchIn(
        adminPhoneNumber: String,
        name: String,
        location: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        viewModelScope.launch {
            _gola.value = true
            try {
                // Check if punch-in is possible
                val isPossible = punchRepo.isPunchInPossible(adminPhoneNumber)
                Log.d("Att", "$isPossible, $currentTime")
                if (isPossible) {
                    // Call functionPunchIn if punch-in is possible
                    punchRepo.functionPunchIn(
                        adminPhoneNumber = adminPhoneNumber,
                        punchInData = PunchInPunchOut(
                            currentTime = currentTime,
                            absent = "Present",
                            date = currentDate,
                            punchTime = currentTime,
                            punchOutTime = null,
                            locationPunchTime = location,
                            name = name,
                            phoneNumberString = phoneNumberCurrent,
                        ),
                        onSuccess = {
                            _gola.value = false
                            onSuccess()
                        },
                        onFailure = {
                            _gola.value = false
                            onFailure()
                        },
                    )
                } else {
                    // Update most recent punch-in if punch-in is not possible
                    punchRepo.updateMostRecentPunchIn(
                        adminPhoneNumber = adminPhoneNumber,
                        punchInData = PunchInPunchOut(
                            currentTime = currentTime,
                            absent = "Present",
                            date = currentDate,
                            punchTime = currentTime,
                            punchOutTime = null,
                            locationPunchTime = location,
                            name = name,
                            phoneNumberString = phoneNumberCurrent,
                        ),
                        onSuccess = {
                            _gola.value = false // Reset loading state
                            onSuccess()
                        },
                        onFailure = {
                            _gola.value = false // Reset loading state
                            onFailure()
                        }
                    )
                }
            } catch (e: Exception) {
                _gola.value = false // Reset loading state in case of error
                Log.d("Att", "Error in punchIn: ${e.message}")
                onFailure()
            }
        }
    }

    fun punchOut(
        adminPhoneNumber: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        viewModelScope.launch {
            _gola.value = true
            try {
                punchRepo.punchOut(
                    adminPhoneNumber = adminPhoneNumber,
                    punchOutTime = currentTime,
                    onSuccess = {
                        _gola.value = false // Reset loading state
                        onSuccess()
                    },
                    onFailure = {
                        _gola.value = false // Reset loading state
                        onFailure()
                    }
                )
            } catch (e: Exception) {
                _gola.value = false // Reset loading state in case of error
                Log.d("Att", "Error in punchOut: ${e.message}")
                onFailure()
            }
        }
    }

    private val _attendanceDetails = MutableStateFlow<List<PunchInPunchOut>>(emptyList())
    val attendanceDetails:StateFlow<List<PunchInPunchOut>?> = _attendanceDetails

    fun loadDayWiseByAttendance(
        adminPhoneNumber: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ){
        viewModelScope.launch {
            _gola.value = true
            punchRepo.loadDaysWiseAttendance(
                adminPhoneNumber = adminPhoneNumber,
                onSuccess = {details ->
                    _attendanceDetails.value = details
                    _gola.value = false
                    onSuccess()
                },
                onFailure = {
                    _gola.value = false
                    onFailure()
                }

            )
        }
    }

    private val _outOfWorkData=MutableStateFlow<List<OutForWork>>(emptyList())
    val outOfWorkData: StateFlow<List<OutForWork>?> = _outOfWorkData

    fun loadOutOfWorkData(
        adminPhoneNumber: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ){
        viewModelScope.launch {
            _gola.value = true
            punchRepo.loadOutOfWorkData(
                adminPhoneNumber = adminPhoneNumber,
                onSuccess = {details ->
                    _outOfWorkData.value = details
                    _gola.value = false
                    onSuccess()
                },
                onFailure = {
                    _gola.value = false
                    onFailure()
                }

            )
        }
    }

    fun setOutForWork(
        adminPhoneNumber: String,
        newValue:Int,
        name: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ){
        viewModelScope.launch {
            _gola.value = true
            try {
                punchRepo.setOutForWork(
                    adminPhoneNumber = adminPhoneNumber,
                    newValue = newValue,
                    name = name,
                    onSuccess = {
                        _gola.value = false // Reset loading state
                        onSuccess()
                    },
                    onFailure = {
                        _gola.value = false // Reset loading state
                        onFailure()
                    }
                )
            } catch (e: Exception) {
                _gola.value = false // Reset loading state in case of error
                Log.d("Att", "Error in Out of Work: ${e.message}")
                onFailure()
            }
        }
    }

}