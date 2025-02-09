package com.rach.firmmanagement.viewModel

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.rach.firmmanagement.HomeScreen.getCurrentDate
import com.rach.firmmanagement.dataClassImp.AdvanceMoneyData
import com.rach.firmmanagement.dataClassImp.EmployeeLeaveData
import com.rach.firmmanagement.dataClassImp.Expense
import com.rach.firmmanagement.dataClassImp.ExpenseItem
import com.rach.firmmanagement.dataClassImp.PunchInPunchOut
import com.rach.firmmanagement.dataClassImp.ViewAllEmployeeDataClass
import com.rach.firmmanagement.repository.EmployeeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
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
        firmName: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        viewModelScope.launch {

            _circularBarState.value = true

            repository.raiseALeave(
                adminPhoneNumber = adminPhoneNumber,
                firmName = firmName,
                employeeleaveData = EmployeeLeaveData(
                    type = _leaveType.value,
                    startingDate = _startingDate.value,
                    endDate = _endDate.value,
                    reason = _reason.value,
                    status = 0,
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

    private val _selectedLeaveMonth = MutableStateFlow(getCurrentMonth())
    val selectedLeaveMonth: StateFlow<String> = _selectedLeaveMonth
    fun onSelectedLeaveMonthChange(newMonth: String) {
        _selectedLeaveMonth.value = newMonth
    }
    private val _selectedLeaveFromDate = MutableStateFlow("")
    val selectedLeaveFromDate: StateFlow<String> = _selectedLeaveFromDate
    fun onSelectedFromDateChange(newDate: String) {
        _selectedLeaveFromDate.value = newDate
    }

    private val _selectedLeaveToDate = MutableStateFlow("")
    val selectedLeaveToDate: StateFlow<String> = _selectedLeaveToDate
    fun onSelectedLeaveToDateChange(newDate: String) {
        _selectedLeaveToDate.value = newDate
    }

    private val _leaves = MutableStateFlow<List<EmployeeLeaveData>>(emptyList())
    val leaves: StateFlow<List<EmployeeLeaveData>> = _leaves

    fun getLeaves(
        adminPhoneNumber: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
        selectedMonth: String,
        from: String,
        to: String,
    ){
        viewModelScope.launch {
            _circularBarState.value = true
            repository.getLeaves(
                adminPhoneNumber = adminPhoneNumber,
                onSuccess = {
                    _circularBarState.value = false
                    _leaves.value = it
                    onSuccess()
                },
                onFailure = {
                    _circularBarState.value = false
                    onFailure()
                },
                selectedMonth = selectedMonth,
                from = from,
                to = to
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
        firmName: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {

        viewModelScope.launch {
            _circularBarState.value = true
            repository.raiseAdvanceMoney(
                adminPhoneNumber = adminPhoneNumber,
                firmName = firmName,
                advanceMoneyData = AdvanceMoneyData(
                    reason = _reasonAdavnce.value,
                    amount = _amount.value,
                    date = currentDate.replace('/', '-'),
                    emplPhoneNumber = currentUserPhoneNumber,
                    status = 0,
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

    private val _advanceMoney = MutableStateFlow<List<AdvanceMoneyData>>(emptyList())
    val advanceMoney: StateFlow<List<AdvanceMoneyData>> = _advanceMoney
    private val _selectedFromDateAdvance = MutableStateFlow(getTodayDate())
    val selectedFromDateAdvance: StateFlow<String> = _selectedFromDateAdvance
    fun onSelectedFromDateAdvanceChange(newDate: String) {
        _selectedFromDateAdvance.value = newDate
    }
    private val _selectedToDateAdvance = MutableStateFlow(getTodayDate())
    val selectedToDateAdvance: StateFlow<String> = _selectedToDateAdvance
    fun onSelectedToDateAdvanceChange(newDate: String) {
        _selectedToDateAdvance.value = newDate
    }
    private val _selectedAdvanceMonth = MutableStateFlow(getCurrentMonth())
    val selectedAdvanceMonth: StateFlow<String> = _selectedAdvanceMonth
    fun onSelectedAdvanceMonthChange(newMonth: String) {
        _selectedAdvanceMonth.value = newMonth
    }
    fun getAdvance(
        adminPhoneNumber: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
        selectedMonth: String,
        from: String,
        to: String,
    ){
        viewModelScope.launch {
            _circularBarState.value = true
            repository.getAdvance(
                adminPhoneNumber = adminPhoneNumber,
                onSuccess = {
                    _circularBarState.value = false
                    _advanceMoney.value = it
                    onSuccess()
                },
                onFailure = {
                    _circularBarState.value = false
                    onFailure()
                },
                selectedMonth = selectedMonth,
                from = from,
                to = to
            )
        }
    }



    var moneyRaise: String by mutableStateOf("")
        private set

    fun onMoneyRaiseChange(newMoneyRaise: String) {
        moneyRaise = newMoneyRaise
    }

    var items: List<ExpenseItem> by mutableStateOf(listOf())
        private set

    fun onItemsChange(newItems: List<ExpenseItem>) {
        items = newItems
    }

    private val _remaining = MutableStateFlow("")
    val remaining :StateFlow<String> = _remaining


    fun onRemainingChange(newRemaining: String) {
        Log.d("Remaining", "onRemainingChange: $newRemaining")
        _remaining.value = newRemaining
    }

    var selectedDate: String by mutableStateOf("")
        private set
    fun onDateChange(newDate: String) {
        selectedDate = newDate
    }

    fun raiseExpense(
        adminPhoneNumber: String,
        firmName:String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ){
        viewModelScope.launch {
            _circularBarState.value = true
            repository.raiseExpense(
                adminPhoneNumber = adminPhoneNumber,
                expense= Expense(
                    moneyRaise = moneyRaise,
                    items = items,
                    remaining = remaining.value,
                    selectedDate = selectedDate,
                    employeeNumber = currentUserPhoneNumber
                ),
                firmName = firmName,
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

    var expenses: List<Expense> by mutableStateOf(emptyList())
        private set

    var selectedYear: String by mutableStateOf("")
        private set

    fun onYearChange(newYear: String) {
        selectedYear = newYear
    }

    var selectedMonth: String by mutableStateOf("")
        private set

    fun onMonthChange(newMonth: Int) {
        // Get the month in "MMM" format (0-based month)
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, newMonth)  // Set the calendar to the selected month
        selectedMonth = SimpleDateFormat("MMM", Locale.getDefault()).format(calendar.time)  // Format month as "MMM"
    }

    var isLoading: Boolean by mutableStateOf(false)
        private set

    fun getExpensesForMonth(
        adminPhoneNumber: String,
        onSuccess: () -> Unit = {},
        onFailure: () -> Unit = {}
    ){
        viewModelScope.launch {
            isLoading = true
            try {
                repository.getExpensesForMonth(
                    adminPhoneNumber = adminPhoneNumber,
                    year = selectedYear,
                    month = selectedMonth,
                    onSuccess = { fetchedExpenses ->
                        expenses = fetchedExpenses
                        onSuccess()
                        Log.d("ExpensesData", "$fetchedExpenses")
                    },
                    onFailure = {
                        onFailure()
                    }
                )
            } catch (e: Exception) {
                expenses = emptyList()
                onFailure()
            } finally {
                isLoading = false
            }
        }
    }


    // new change:
    private val _fromDate = MutableStateFlow(getTodayDate())
    val fromDate: StateFlow<String> = _fromDate

    fun onChangeFromDate(newDate: String) {
        Log.d("Attendance", "FromDateChange: $newDate")
        _fromDate.value = newDate
    }

    private val _toDate = MutableStateFlow(getTodayDate())
    val toDate: StateFlow<String> = _toDate

    fun onChangeToDate(newDate: String) {
        Log.d("Attendance", "ToDateChange: $newDate")
        _toDate.value = newDate
    }

    private val _selectedMonth = MutableStateFlow(getCurrentMonth())
    val selectedMonth1: StateFlow<String> = _selectedMonth
    fun onSelectedMonthChange(newMonth: String) {
        _selectedMonth.value = newMonth
    }

    private val _employeeExpense=MutableStateFlow<List<Expense>>(emptyList())
    val employeeExpense:StateFlow<List<Expense>> = _employeeExpense

    private var _loading = MutableStateFlow(false)
    val expenseLoading: StateFlow<Boolean> get() = _loading
    fun fetchExpense(
        adminPhoneNumber:String,
        from: String,
        to: String,
        selectedMonth: String
    ) {
        viewModelScope.launch {
            _loading.value = true
            repository.getEmployeeExpense(
                adminPhoneNumber = adminPhoneNumber,
                from = from,
                to = to,
                selectedMonth = selectedMonth,
                onSuccess = { expenses ->
                    _employeeExpense.value = expenses
                    _loading.value = false
                },
                onFailure = {
                    _employeeExpense.value = emptyList()
                    _loading.value = false
                }
            )
        }
    }



    // Employee Attendance
    private val _fromDateAttendance = MutableStateFlow(getTodayDate())
    val fromDateAttendance: StateFlow<String> = _fromDateAttendance

    fun onChangeAttendanceFromDate(newDate: String) {
        Log.d("Attendance", "FromDateChange: $newDate")
        _fromDateAttendance.value = newDate
    }

    private val _toDateAttendance = MutableStateFlow(getTodayDate())
    val toDateAttendance: StateFlow<String> = _toDateAttendance

    fun onChangeAttendanceToDate(newDate: String) {
        Log.d("Attendance", "ToDateChange: $newDate")
        _toDateAttendance.value = newDate
    }

    private val _selectedMonthAttendance = MutableStateFlow(getCurrentMonth())
    val selectedMonthAttendance: StateFlow<String> = _selectedMonthAttendance

    fun onChangeSelectedMonthAttendance(newMonth: String) {
        Log.d("Attendance", "MonthChange: $newMonth")
        _selectedMonthAttendance.value = newMonth
    }

    private val _attendance=MutableStateFlow<List<PunchInPunchOut>>(emptyList())
    val attendance:StateFlow<List<PunchInPunchOut>> = _attendance

    @SuppressLint("DefaultLocale", "SimpleDateFormat")
    private fun getCurrentMonth(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val monthFormat = SimpleDateFormat("MMM") // Formats month as "Jan", "Feb", etc.
        val month = monthFormat.format(calendar.time)

        return "$month $year"
    }

    @SuppressLint("DefaultLocale")
    fun getTodayDate(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1 // Month is 0-indexed
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        return String.format("%02d-%02d-%04d", day, month, year)
    }


    fun fetchAttendance(
        adminPhoneNumber: String,
        from: String,
        to: String,
        selectedMonth: String
    ){
        viewModelScope.launch {
            isLoading = true
            repository.fetchAttendance(
                adminPhoneNumber=adminPhoneNumber,
                from = from,
                to = to,
                selectedMonth = selectedMonth,
                onSuccess = {
                    _attendance.value = it
                    isLoading = false
                },
                onFailure = {
                    _attendance.value = emptyList()
                    isLoading = false
                }
            )
        }
    }


}