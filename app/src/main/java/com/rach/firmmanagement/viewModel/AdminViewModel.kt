package com.rach.firmmanagement.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.rach.firmmanagement.dataClassImp.AddStaffDataClass
import com.rach.firmmanagement.dataClassImp.AddTaskDataClass
import com.rach.firmmanagement.dataClassImp.Expense
import com.rach.firmmanagement.dataClassImp.HolidayAndHoursDataClass
import com.rach.firmmanagement.dataClassImp.Remark
import com.rach.firmmanagement.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdminViewModel : ViewModel() {

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
                    role = _role.value,
                    salary = _salary.value,
                    registrationDate = _registrationDate.value
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
                    date = _registrationDate.value,
                    task = _task.value
                ),
                adminPhoneNumber = currentUser,
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

    fun loadTasks() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val taskList = repository.loadTasks(adminPhoneNumber)
                _tasks.value = taskList
            } catch (e: Exception) {
                e.printStackTrace()
                _tasks.value = emptyList() // Handle error case
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


}