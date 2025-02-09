package com.rach.firmmanagement.viewModel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.rach.firmmanagement.dataClassImp.AddStaffDataClass
import com.rach.firmmanagement.dataClassImp.AddTaskDataClass
import com.rach.firmmanagement.dataClassImp.OutForWork
import com.rach.firmmanagement.dataClassImp.PunchInPunchOut
import com.rach.firmmanagement.dataClassImp.ViewAllEmployeeDataClass
import com.rach.firmmanagement.repository.ViewAllEmployeeRepos
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


data class EmployeeHistory(
    val label:String,
    val title:String = ""
)

class AllEmployeeViewModel(
    private val repository: ViewAllEmployeeRepos = ViewAllEmployeeRepos()
) :ViewModel(){


    var employeeList = mutableStateOf<List<AddStaffDataClass>>(emptyList())
        private set

    var adminList= mutableStateOf<List<AddStaffDataClass>>(emptyList())
    private set

    var isEmployeeLoading = mutableStateOf(true)
        private set
    var isAdminLoading = mutableStateOf(true)
        private set

    var isAssigningTask = mutableStateOf(false)

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val currentDate: String = dateFormat.format(Date())

    private val _registrationDate = MutableStateFlow(currentDate)
    val registrationDate = _registrationDate

    private val _selectedDateForEmployeeAttendance = MutableStateFlow(currentDate.replace("/", "-"))
    val selectedDate: StateFlow<String> = _selectedDateForEmployeeAttendance

    fun onChangeSelectedDate(newDate: String) {
        _selectedDateForEmployeeAttendance.value = newDate
        Log.d("TAG", "new data: $newDate, ${selectedDate.value}")
    }


     fun loadAllEmployee(firmName:String){
        viewModelScope.launch {
            isEmployeeLoading.value = true
            repository.viewAllEmployee(
                firmName=firmName,
                onSuccess = {employee ->
                    employeeList.value = employee
                    isEmployeeLoading.value = false

                },
                onFailure = {
                    isEmployeeLoading.value = false

                }
            )

            repository.viewAllAdmin(
                firmName=firmName,
                onSuccess = {employee ->
                    adminList.value = employee
                    isAdminLoading.value = false

                },
                onFailure = {
                    isAdminLoading.value = false

                }
            )

        }
    }

    fun deleteEmployee(phoneNumber: String, firmName: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        viewModelScope.launch {
            try {
                repository.deleteEmployee(
                    phoneNumber,
                    onSuccess = {
                        loadAllEmployee(firmName)
                        onSuccess()
                    },
                    onFailure = onFailure
                )
                 // Refresh the list after deletion
            } catch (e: Exception) {
                // Handle exception
            }
        }
    }
    fun updateEmployeesToAdmin(
        selectedEmployees: List<AddStaffDataClass>,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
        firmName: String
    ){
        viewModelScope.launch{
            try {
                repository.updateEmployeesToAdmin(
                    selectedEmployees,
                    onSuccess = {
                        loadAllEmployee(firmName)
                        onSuccess()
                    },
                    onFailure = {
                        onFailure()
                    }
                )
            }catch (e:Exception){
                onFailure()
            }
        }
    }

    /*
    fun updateEmployeeSelection(phoneNumber: String, isSelected: Boolean) {
        val updatedEmployees = employeeList.value.map { employee ->
            if (employee.phoneNumber == phoneNumber) {
                employee.copy(isSelected = isSelected)
            } else {
                employee
            }
        }
        employeeList.value = updatedEmployees
    }

     */

    /*
    fun assignTaskToSelectedEmployees(task: String,context: Context) {

        isAssigningTask.value = true
        val firestore = FirebaseFirestore.getInstance()
        val adminPhoneNumber = FirebaseAuth.getInstance().currentUser?.phoneNumber.toString()

        val selectedEmployees = employeeList.value.filter { it.isSelected }
        Log.d("Hins", "SelectedEmployees: $selectedEmployees")
        selectedEmployees.forEach { employee ->
            val employeePhone = employee.phoneNumber ?: return@forEach
            val taskRef = firestore
                .collection("Members")
                .document(adminPhoneNumber)
                .collection("Employee")
                .document(employeePhone)  // Corrected this line
                .collection("tasks")
                .document()
            /*

            change

            val taskData = hashMapOf(
                "task" to task,
                "timestamp" to FieldValue.serverTimestamp()
            )

             */

            val taskData= AddTaskDataClass(task=task, assignDate = _registrationDate.value, isCommon = false, id = taskRef.id)

            taskRef.set(taskData)
                .addOnSuccessListener {

                    Toast.makeText(context,"Task Uploaded",Toast.LENGTH_LONG).show()

                }
                .addOnFailureListener {
                    Toast.makeText(context,"Task Uploaded",Toast.LENGTH_LONG).show()

                }
        }
        isAssigningTask.value = false
    }

     */

    private val _punchInOutAttendanceDetails = MutableStateFlow<List<PunchInPunchOut>>(emptyList())
    val punchInOutAttendanceDetails:StateFlow<List<PunchInPunchOut>?> = _punchInOutAttendanceDetails

    private val _gola = MutableStateFlow(false)
    val gola: StateFlow<Boolean> = _gola

    fun getPunchInOutAttendanceForAllEmployees(
        targetYear: String,
        targetMonth: String,
        targetDate: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ){
        viewModelScope.launch {
            repository.getPunchInOutAttendanceForAllEmployees(
                targetYear,
                targetMonth,
                targetDate,
                onSuccess = {details ->
                    _punchInOutAttendanceDetails.value = details
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

    private val _outForWorkAttendanceDetails = MutableStateFlow<List<OutForWork>>(emptyList())
    val outForWorkAttendanceDetails:StateFlow<List<OutForWork>?> = _outForWorkAttendanceDetails

    fun getOutForWorkAttendanceForAllEmployees(
        targetYear: String,
        targetMonth: String,
        targetDate: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ){
        viewModelScope.launch {
            repository.getOutOfWorkAttendanceForAllEmployees(
                targetYear,
                targetMonth,
                targetDate,
                onSuccess = {details ->
                    _outForWorkAttendanceDetails.value = details
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



    private val _EmployeeHistoryFields = MutableStateFlow(
        listOf(
            EmployeeHistory("Employee Name","vishal"),
            EmployeeHistory("Employee Phone Number","87899009"),
            EmployeeHistory("Salary","8668"),
            EmployeeHistory("Working Hours","8")
        )
    )

    val employeeHistoryFields :StateFlow<List<EmployeeHistory>> = _EmployeeHistoryFields



}



