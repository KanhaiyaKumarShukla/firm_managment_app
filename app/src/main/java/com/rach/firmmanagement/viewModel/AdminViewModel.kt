package com.rach.firmmanagement.viewModel

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.rach.firmmanagement.dataClassImp.AddStaffDataClass
import com.rach.firmmanagement.dataClassImp.AddTaskDataClass
import com.rach.firmmanagement.dataClassImp.Expense
import com.rach.firmmanagement.dataClassImp.GeofenceItems
import com.rach.firmmanagement.dataClassImp.HolidayAndHoursDataClass
import com.rach.firmmanagement.dataClassImp.PunchInPunchOut
import com.rach.firmmanagement.dataClassImp.Remark
import com.rach.firmmanagement.dataClassImp.ViewAllEmployeeDataClass
import com.rach.firmmanagement.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AdminViewModel() : ViewModel() {

    // currentAdminPhoneNumber

    val currentUser = FirebaseAuth.getInstance().currentUser?.phoneNumber.toString()

    //progressbar State

    private val _progressbarState = MutableStateFlow(false)
    val progressBarState: StateFlow<Boolean> = _progressbarState

    fun onChangeProgressState(newState: Boolean) {
        _progressbarState.value = newState
    }

    //onButtonClicked


    private val _onButtonClicked = MutableStateFlow(false)
    val onButtonClicked: StateFlow<Boolean> = _onButtonClicked

    fun onButtonStateChange(newState: Boolean) {

        _onButtonClicked.value = newState

    }

    // name
    private val _empName = MutableStateFlow("")
    val empName: StateFlow<String> = _empName

    fun onChangeEmpName(newName: String) {
        _empName.value = newName
    }

    // phoneNumber

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber: StateFlow<String> = _phoneNumber

    fun onChangePhoneNumber(newNumber: String) {
        _phoneNumber.value = newNumber
    }

    // employee role

    private val _role = MutableStateFlow("")
    val role: StateFlow<String> = _role

    fun onChangeRole(newRole: String) {
        _role.value = newRole
    }

    // salary

    private val _salary = MutableStateFlow("")
    val salary: StateFlow<String> = _salary

    fun onChangeSalary(newSalary: String) {
        _salary.value = newSalary
    }

    // registration Date

    val dateFormat = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
    val currentDate: String = dateFormat.format(Date())

    private val _registrationDate = MutableStateFlow(currentDate)
    val registrationDate = _registrationDate

    fun onChangeRegistrationDate(newDate: String) {
        _registrationDate.value = newDate
    }

    private val _timeVariation = MutableStateFlow("")
    val timeVariation: StateFlow<String> = _timeVariation

    fun onChangeTimeVariation(newTime: String){
        _timeVariation.value = newTime
    }

    private val _leaveDays = MutableStateFlow("")
    val leaveDays: StateFlow<String> = _leaveDays

    fun onChangeLeaveDays(newDays: String){
        _leaveDays.value = newDays
    }

    private val _selectedGeoFence=MutableStateFlow(GeofenceItems())
    val selectedGeoFence:StateFlow<GeofenceItems> = _selectedGeoFence
    fun onGeoFenceChanged(newGeoFence: GeofenceItems){
        _selectedGeoFence.value = newGeoFence
    }

    val adminPhoneNumber = FirebaseAuth.getInstance().currentUser?.phoneNumber.toString()

    val repository = AdminRepository()


    fun addEmployee(
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        viewModelScope.launch {
            repository.addStaff(
                addStaffDataClass = AddStaffDataClass(
                    name = _empName.value,
                    phoneNumber = _phoneNumber.value,
                    newPhoneNumber = _phoneNumber.value,
                    role = _role.value,
                    salary = _salary.value,
                    registrationDate = _registrationDate.value,
                    timeVariation = _timeVariation.value,
                    leaveDays = _leaveDays.value,
                    workPlace = _selectedGeoFence.value
                ),
                employeePhoneNumber = _phoneNumber.value,
                adminPhoneNumber = adminPhoneNumber,
                onSuccess = onSuccess,
                onFailure = onFailure

            )
        }
    }

    /// Add Holiday

    private val _holidayName = MutableStateFlow("")
    val holidayName: StateFlow<String> = _holidayName

    fun onChangeHolidayName(newDay: String) {
        _holidayName.value = newDay
    }

    // holidayDate
    private val _selectedDate = MutableStateFlow("")
    val selectedDate: StateFlow<String> = _selectedDate

    fun onChangeDate(newDate: String) {
        _selectedDate.value = newDate
    }

    private val _progressbarState2 = MutableStateFlow(false)
    val progressBarState2: StateFlow<Boolean> = _progressbarState

    fun onChangeProgressState2(newState: Boolean) {
        _progressbarState.value = newState
    }

    fun addHoliday(
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
        date: String
    ) {
        viewModelScope.launch {
            repository.addHoliday(
                addHolidayDataClass = HolidayAndHoursDataClass(
                    holidayName = _holidayName.value,
                    holidayDate = _selectedDate.value
                ),
                adminPhoneNumber = currentUser,
                date = date,
                onSuccess = onSuccess,
                onFailure = onFailure

            )
        }
    }

    ///  Add Task

    private val _assignTaskDate = MutableStateFlow("")
    val assignTaskDate: StateFlow<String> = _assignTaskDate

    fun onChangeAssignTaskDate(newDate: String) {
        _assignTaskDate.value = newDate
    }
    private val _submitionTaskDate = MutableStateFlow("")
    val submitionTaskDate: StateFlow<String> = _submitionTaskDate

    fun onChangeSubmitionTaskDate(newDate: String) {
        _submitionTaskDate.value = newDate
    }

    private val _task = MutableStateFlow("")
    val task: StateFlow<String> = _task

    fun onChangeTask(newTask: String) {
        _task.value = newTask
    }

    private val _state = MutableStateFlow(false)
    val state: StateFlow<Boolean> = _state

    fun onStateChange(newState: Boolean) {
        _state.value = newState
    }


    fun addTask(
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        viewModelScope.launch {
            repository.addTask(
                addTaskDataClass = AddTaskDataClass(
                    assignDate = _registrationDate.value,
                    task = _task.value
                ),
                adminPhoneNumber = currentUser,
                onSuccess = onSuccess,
                onFailure = onFailure
            )
        }
    }

    fun assignTask(
        selectedEmployees: Set<ViewAllEmployeeDataClass>,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ){
        viewModelScope.launch{
            repository.assignTask(
                selectedEmployees = selectedEmployees,
                addTaskDataClass = AddTaskDataClass(
                    assignDate = _assignTaskDate.value,
                    task = _task.value,
                    submitDate = _submitionTaskDate.value
                ),
                onSuccess = onSuccess,
                onFailure = onFailure
            )
        }
    }

    private val _startingDate = MutableStateFlow(currentDate)
    val startingDate : StateFlow<String> = _startingDate

    fun onChangeStartingDate(newDate: String){
        _startingDate.value = newDate
    }

    private val _tasks = MutableStateFlow<List<AddTaskDataClass>>(emptyList())
    val tasks: StateFlow<List<AddTaskDataClass>> get() = _tasks

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    fun loadTasks(allEmployees:List<ViewAllEmployeeDataClass>) {
        viewModelScope.launch {
            _loading.value = true
            Log.d("Task", "load Tasks")
            try {

                Log.d("Task", allEmployees.toString())
                val taskList = repository.loadTasks(adminPhoneNumber, allEmployees)
                _tasks.value = taskList
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("Task", e.message.toString())
                _tasks.value = emptyList() // Handle error case
            } finally {
                _loading.value = false
            }
        }
    }
    private val _oneEmployeeTask = MutableStateFlow<List<AddTaskDataClass>>(emptyList())
    val oneEmployeeTask: StateFlow<List<AddTaskDataClass>> get() = _oneEmployeeTask
    fun loadOneEmployeeTask(employee: ViewAllEmployeeDataClass){
        viewModelScope.launch {
            _loading.value = true
            try {
                val taskList = repository.loadOneEmployeeTask(adminPhoneNumber, employee)
                _oneEmployeeTask.value = taskList
            } catch (e: Exception) {
                e.printStackTrace()
                _oneEmployeeTask.value = emptyList() // Handle error case
            } finally {
                _loading.value = false
            }
        }
    }

    fun deleteTask( task: AddTaskDataClass) {
        viewModelScope.launch {
            try {
                repository.deleteTask(adminPhoneNumber, task)
                _tasks.value = _tasks.value.filterNot { it == task } // Update UI state
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle error if needed
            }
        }
    }

    fun addRemark(
        taskId: String,
        isCommon: Boolean,
        employeePhone: String = "",
        newRemark: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        viewModelScope.launch {
            repository.addRemark(
                adminPhoneNumber = adminPhoneNumber,
                employeePhone = employeePhone,
                taskId = taskId,
                isCommon = isCommon,
                newRemark = Remark(
                    person = adminPhoneNumber,
                    message = newRemark,
                    date = currentDate
                ),
                onSuccess = onSuccess,
                onFailure = onFailure
            )
        }
    }

    suspend fun fetchRemarks(
        taskId: String,
        isCommon: Boolean,
        employeePhone: String = "",

    ) : List<Remark> {
        return repository.fetchRemarks(
            adminPhoneNumber = adminPhoneNumber,
            employeePhone = employeePhone,
            taskId = taskId,
            isCommon = isCommon,
        )

    }

    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses
    private val _loadingExpenses = MutableStateFlow(false)
    val loadingExpenses: StateFlow<Boolean> = _loadingExpenses

    fun getExpensesForMonth(
        employeeNumber:String,
        year: String,
        month: String
    ) {
        viewModelScope.launch {
            repository.getExpensesForMonth(
                adminPhoneNumber = adminPhoneNumber,
                employeeNumber = employeeNumber,
                year = year,
                month = month,
                onSuccess = { expenses ->
                    _expenses.value = expenses
                },
                onFailure = {
                    _expenses.value = emptyList() // Handle failure case
                }
            )
        }

    }


    // Employee Attendance
    private val _fromDate = MutableStateFlow(getTodayDate())
    val fromDate: StateFlow<String> = _fromDate

    fun onChangeAttendanceFromDate(newDate: String) {
        Log.d("Attendance", "FromDateChange: $newDate")
        _fromDate.value = newDate
    }

    private val _toDate = MutableStateFlow(getTodayDate())
    val toDate: StateFlow<String> = _toDate

    fun onChangeAttendanceToDate(newDate: String) {
        Log.d("Attendance", "ToDateChange: $newDate")
        _toDate.value = newDate
    }

    private val _selectedMonth = MutableStateFlow("")
    val selectedMonth: StateFlow<String> = _selectedMonth

    fun onChangeSelectedMonth(newMonth: String) {
        Log.d("Attendance", "MonthChange: $newMonth")
        _selectedMonth.value = newMonth
    }

    private val _attendance=MutableStateFlow<List<PunchInPunchOut>>(emptyList())
    val attendance:StateFlow<List<PunchInPunchOut>> = _attendance

    @SuppressLint("DefaultLocale")
    fun getTodayDate(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1 // Month is 0-indexed
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        return String.format("%02d-%02d-%04d", day, month, year)
    }

    fun fetchAttendance(
        selectedEmployees: List<ViewAllEmployeeDataClass>,
        from: String,
        to: String,
        selectedMonth: String
    ){
        viewModelScope.launch {
            _loading.value = true
            repository.fetchAttendance(
                adminPhoneNumber=adminPhoneNumber,
                selectedEmployees = selectedEmployees,
                from = from,
                to = to,
                selectedMonth = selectedMonth,
                onSuccess = {
                    _attendance.value = it
                    _loading.value = false
                },
                onFailure = {
                    _attendance.value = emptyList()
                    _loading.value = false
                }
            )
        }
    }

    // employee profile
    var isProfileLoading = mutableStateOf(false)
        private set

    private val _Staffname = MutableStateFlow("")
    val staffName: StateFlow<String> = _Staffname

    fun onChangeStaffName(newName: String) {
        _Staffname.value = newName
    }

    private val _staffOldPhoneNumber = MutableStateFlow("")
    val staffOldPhoneNumber: StateFlow<String> = _staffOldPhoneNumber
    private val _staffNewPhoneNumber = MutableStateFlow("")
    val staffNewPhoneNumber: StateFlow<String> = _staffNewPhoneNumber

    fun onChangeStaffPhoneNumber(newPhoneNumber: String) {
        _staffNewPhoneNumber.value = newPhoneNumber
    }

    private val _staffRole = MutableStateFlow("")
    val staffRole: StateFlow<String> = _staffRole

    fun onChangeStaffRole(newRole: String) {
        _staffRole.value = newRole
    }

    private val _staffSalary = MutableStateFlow("")
    val staffSalary: StateFlow<String> = _staffSalary

    fun onChangeStaffSalary(newSalary: String) {
        _staffSalary.value = newSalary
    }

    private val _staffRegistrationDate = MutableStateFlow("")
    val staffRegistrationDate: StateFlow<String> = _staffRegistrationDate

    fun onChangeStaffRegistrationDate(newDate: String) {
        _staffRegistrationDate.value = newDate
    }

    private val _staffTimeVariation = MutableStateFlow("")
    val staffTimeVariation: StateFlow<String> = _staffTimeVariation

    fun onChangeStaffTimeVariation(newTimeVariation: String) {
        _staffTimeVariation.value = newTimeVariation
    }

    private val _staffLeaveDays = MutableStateFlow("")
    val staffLeaveDays: StateFlow<String> = _staffLeaveDays

    fun onChangeStaffLeaveDays(newLeaveDays: String) {
        _staffLeaveDays.value = newLeaveDays
    }

    private val _staffWorkPlace = MutableStateFlow(GeofenceItems())
    val staffWorkPlace: StateFlow<GeofenceItems> = _staffWorkPlace

    fun onChangeStaffWorkPlace(newWorkPlace: GeofenceItems) {
        _staffWorkPlace.value = newWorkPlace
    }

    // Load staff data
    fun loadStaff(employeePhoneNumber: String) {
        viewModelScope.launch {
            isProfileLoading.value = true
            repository.getStaff(
                adminPhoneNumber = adminPhoneNumber,
                employeePhoneNumber = employeePhoneNumber,
                onSuccess = { staff ->
                    _Staffname.value = staff.name ?: ""
                    _staffOldPhoneNumber.value = staff.phoneNumber ?: ""
                    _staffNewPhoneNumber.value = staff.newPhoneNumber ?: ""
                    _staffRole.value = staff.role ?: ""
                    _staffSalary.value = staff.salary ?: ""
                    _staffRegistrationDate.value = staff.registrationDate ?: ""
                    _staffTimeVariation.value = staff.timeVariation ?: ""
                    _staffLeaveDays.value = staff.leaveDays ?: ""
                    _staffWorkPlace.value = staff.workPlace
                    isProfileLoading.value = false
                },
                onFailure = {
                    isProfileLoading.value = false
                }
            )
        }
    }

    // Update staff data
    fun updateStaff(employeePhoneNumber: String) {
        viewModelScope.launch {
            isProfileLoading.value = true
            val updatedStaff = AddStaffDataClass(
                name = _Staffname.value,
                phoneNumber = _staffOldPhoneNumber.value,
                newPhoneNumber = _staffNewPhoneNumber.value,
                role = _staffRole.value,
                salary = _staffSalary.value,
                registrationDate = _staffRegistrationDate.value,
                timeVariation = _staffTimeVariation.value,
                leaveDays = _staffLeaveDays.value,
                workPlace = _staffWorkPlace.value
            )
            repository.updateStaff(
                adminPhoneNumber = adminPhoneNumber,
                employeePhoneNumber = employeePhoneNumber,
                updatedStaff = updatedStaff,
                onSuccess = {
                    loadStaff(employeePhoneNumber)
                    isProfileLoading.value = false
                },
                onFailure = {
                    isProfileLoading.value = false
                }
            )
        }
    }

    // employee expense:

    private val _employeeExpense = MutableStateFlow<List<Expense>>(emptyList())
    val employeeExpense: StateFlow<List<Expense>> = _employeeExpense

    private val _loadingEmployeeExpense = MutableStateFlow(false)
    val loadingEmployeeExpense: StateFlow<Boolean> = _loadingEmployeeExpense

    fun getEmployeeExpense(
        employee: List<ViewAllEmployeeDataClass>,
        from: String,
        to: String,
        selectedMonth: String
    ) {
        viewModelScope.launch {
            _loadingEmployeeExpense.value = true
            repository.getEmployeeExpense(
                selectedEmployees = employee,
                adminPhoneNumber = adminPhoneNumber,
                from = from,
                to = to,
                selectedMonth = selectedMonth,
                onSuccess = { expenses ->
                    _employeeExpense.value = expenses
                    _loadingEmployeeExpense.value = false
                },
                onFailure = {
                    _employeeExpense.value = emptyList()
                    _loadingEmployeeExpense.value = false
                }
            )
        }
    }

}