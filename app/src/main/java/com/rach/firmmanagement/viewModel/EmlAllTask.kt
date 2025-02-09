package com.rach.firmmanagement.viewModel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.rach.firmmanagement.dataClassImp.AddTaskDataClass
import com.rach.firmmanagement.dataClassImp.EmployeeHomeScreenData
import com.rach.firmmanagement.dataClassImp.GeofenceItems
import com.rach.firmmanagement.dataClassImp.LocationData
import com.rach.firmmanagement.dataClassImp.OutForWork
import com.rach.firmmanagement.dataClassImp.PunchInPunchOut
import com.rach.firmmanagement.dataClassImp.Remark
import com.rach.firmmanagement.repository.EmployeeRepository
import com.rach.firmmanagement.repository.GeofenceRepository
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

    var taskList = mutableStateOf<List<AddTaskDataClass>>(emptyList())
        private set

    var isLoading = mutableStateOf(false)
        private set

    val database = FirebaseFirestore.getInstance()
    val currentUserNumber = FirebaseAuth.getInstance().currentUser?.phoneNumber.toString()


    val employeeNumber = when {
        currentUserNumber.startsWith("+91") -> currentUserNumber.removePrefix("+91")
        else ->
            currentUserNumber
    }

    fun loadAllTask(
        adminPhoneNumber: String
    ) {
        viewModelScope.launch {
            isLoading.value = true
            repository.loadTask(
                adminPhoneNumber = adminPhoneNumber,
                onSuccess = { task ->
                    taskList.value = task
                    isLoading.value = false

                },
                onFailure = {
                    taskList.value = emptyList()
                    isLoading.value = false
                }
            )
        }
    }

    fun addRemark(
        adminPhoneNumber: String,
        taskId: String,
        isCommon: Boolean,
        newRemark: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        viewModelScope.launch {
            repository.addRemark(
                adminPhoneNumber = adminPhoneNumber,
                employeePhone = employeeNumber,
                taskId = taskId,
                isCommon = isCommon,
                newRemark = Remark(
                    person = employeeNumber,
                    message = newRemark,
                    date = currentDate
                ),
                onSuccess = onSuccess,
                onFailure = onFailure
            )
        }
    }

    suspend fun fetchRemarks(
        adminPhoneNumber: String,
        taskId: String,
        isCommon: Boolean,

        ) : List<Remark> {
        return repository.fetchRemarks(
            adminPhoneNumber = adminPhoneNumber,
            employeePhone = employeeNumber,
            taskId = taskId,
            isCommon = isCommon,
        )

    }
    fun addRealtimeRemarksListener(
        adminPhoneNumber: String,
        employeePhone: String,
        taskId: String,
        onRemarksUpdated: (List<Remark>) -> Unit
    ) {
        val updateAdminNumber = if (adminPhoneNumber.startsWith("+91")) {
            adminPhoneNumber
        } else {
            "+91$adminPhoneNumber"
        }

        val taskRef = database.collection("Members")
            .document(updateAdminNumber)
            .collection("Employee")
            .document(employeePhone)
            .collection("Task")
            .document(taskId)

        taskRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("TAG", "Error listening for remarks updates: ${error.message}")
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val taskData = repository.parseAddTaskDataClass(snapshot)
                taskData.remarks.let { onRemarksUpdated(it) }
            }
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
        location: GeofenceItems,
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

    private val geofenceRepository: GeofenceRepository= GeofenceRepository()
    private val _geofences = MutableStateFlow<List<GeofenceItems>>(emptyList())
    val geofences: StateFlow<List<GeofenceItems>> = _geofences
    fun getGeofence(
        firmName: String
    ){
        viewModelScope.launch {
            geofenceRepository.getAllGeofences(
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
        firmName: String,
        adminPhoneNumber: String,
        newValue:Int,
        name: String,
        phoneNumber:String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ){
        viewModelScope.launch {
            _gola.value = true
            try {
                punchRepo.setOutForWork(
                    firmName = firmName,
                    adminPhoneNumber = adminPhoneNumber,
                    newValue = newValue,
                    name = name,
                    phoneNumber = phoneNumber,
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