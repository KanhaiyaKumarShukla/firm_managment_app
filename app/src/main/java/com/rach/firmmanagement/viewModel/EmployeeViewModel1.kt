package com.rach.firmmanagement.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.rach.firmmanagement.dataClassImp.AdvanceMoneyData
import com.rach.firmmanagement.dataClassImp.EmployeeSectionData
import com.rach.firmmanagement.repository.EmployeeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EmployeeViewModel1 : ViewModel() {

    val dateFormat = SimpleDateFormat("d/MM/yyyy", Locale.getDefault())
    val currentDate: String = dateFormat.format(Date())

    val currentUserPhoneNumber = FirebaseAuth.getInstance().currentUser?.phoneNumber.toString()

    val timeFormat = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
    val currentTime: String = timeFormat.format(Date())

    // state
    private val _progressBarState = MutableStateFlow(false)
    val progressBarState: StateFlow<Boolean> = _progressBarState

    fun onChangeState(newState: Boolean) {
        _progressBarState.value = newState
    }

    private val _circularBarState = MutableStateFlow(false)
    val circularBarState: StateFlow<Boolean> = _circularBarState

    fun onChangeCircularBarState(newState: Boolean) {
        _circularBarState.value = newState
    }

    // leave type
    private val _leaveType = MutableStateFlow("")
    val leaveType: StateFlow<String> = _leaveType

    fun onChangeLeaveType(newType: String) {
        _leaveType.value = newType
    }

    // starting Date

    private val _startingDate = MutableStateFlow("")
    val startingDate: StateFlow<String> = _startingDate

    fun onChangeStartingDate(newDate: String) {
        _startingDate.value = newDate
    }

    //endDate

    private val _endDate = MutableStateFlow("")
    val endDate: StateFlow<String> = _endDate

    fun onChangeEndDate(newDate: String) {
        _endDate.value = newDate
    }

    private val _onChangeDate = MutableStateFlow("")
    val onChangeDate: StateFlow<String> = _onChangeDate

    fun onChangeDateFunc(newHai: String) {
        _onChangeDate.value = newHai
    }

    // reason

    private val _reason = MutableStateFlow("")
    val reason: StateFlow<String> = _reason

    fun onChangeReason(newReason: String) {
        _reason.value = newReason
    }

    val repository = EmployeeRepository()

    fun raiseALeave(
        adminPhoneNumber: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        viewModelScope.launch {

            _circularBarState.value = true

            repository.raiseALeave(
                adminPhoneNumber = adminPhoneNumber,
                employeeSectionData = EmployeeSectionData(
                    type = _leaveType.value,
                    startingDate = _startingDate.value,
                    endDate = _endDate.value,
                    reason = _reason.value,
                    status = false,
                    currentDate = currentDate,
                    emlPhoneNumber = currentUserPhoneNumber
                ),
                onSuccess = {
                    _circularBarState.value = false
                    onSuccess()
                },
                onFailure = {
                    _circularBarState.value = false
                    onFailure()
                }
            )

        }
    }

    // Advance Money

    private val _reasonAdavnce = MutableStateFlow("")
    val reasonAdvance: StateFlow<String> = _reasonAdavnce

    fun onChangeResAdvance(newReason: String) {
        _reasonAdavnce.value = newReason
    }

    private val _amount = MutableStateFlow("")
    val amount: StateFlow<String> = _amount

    fun onChangeAmount(newAmount: String) {
        _amount.value = newAmount
    }

    fun advanceMoney(
        adminPhoneNumber: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {

        viewModelScope.launch {
            _circularBarState.value = true
            repository.raiseAdvanceMoney(
                adminPhoneNumber = adminPhoneNumber,
                advanceMoneyData = AdvanceMoneyData(
                    reason = _reasonAdavnce.value,
                    amount = _amount.value,
                    date = currentDate,
                    emplPhoneNumber = currentUserPhoneNumber,
                    status = false,
                    time = currentTime


                ),
                onSuccess = {
                    _circularBarState.value = false
                    onSuccess()
                },
                onFailure = {
                    _circularBarState.value = false
                    onFailure()
                }
            )
        }


    }


}