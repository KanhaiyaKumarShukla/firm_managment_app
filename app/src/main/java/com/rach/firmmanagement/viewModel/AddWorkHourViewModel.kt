package com.rach.firmmanagement.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rach.firmmanagement.dataClassImp.AddWorkingHourDataClass
import com.rach.firmmanagement.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddWorkHourViewModel(private val repository: AdminRepository = AdminRepository()) :
    ViewModel() {


    private val _isLoading = MutableStateFlow(false)
    val isLoading:StateFlow<Boolean> = _isLoading

    fun onChangeIsLoading(newState:Boolean){
        _isLoading.value = newState
    }

    private val _starttime = MutableStateFlow("09:30 AM")
    val startTime: StateFlow<String> = _starttime

    fun onChangeStartTime(newTime: String) {

        _starttime.value = newTime

    }

    private val _endTime = MutableStateFlow("06:40 PM")
    val endTime: StateFlow<String> = _endTime

    fun onChangeEndTime(newTime2: String) {
        _endTime.value = newTime2
    }

//    val dateFormat = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
//    val currentDate: String = dateFormat.format(Date())

    val dateFormat = SimpleDateFormat("d/MM/yyyy", Locale.getDefault())
    val currentDate: String = dateFormat.format(Date())

    private val _date = MutableStateFlow(currentDate)
    val date: StateFlow<String> = _date

    fun onChangeDate(newDate: String) {
        _date.value = newDate
    }

    // Current Time
    //     return SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(Date())

    val timeFormat = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
    val currentTime: String = timeFormat.format(Date())


    fun addWorkHours(
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        viewModelScope.launch {

            repository.addWorkingHour(
                addWorkingHourDataClass = AddWorkingHourDataClass(
                    date = currentDate,
                    startingTime = _starttime.value,
                    endTime = _endTime.value,
                    currentTime = currentTime
                ),
                date = currentDate,
                onSuccess = onSuccess,
                onFailure = onFailure

            )

        }
    }

}