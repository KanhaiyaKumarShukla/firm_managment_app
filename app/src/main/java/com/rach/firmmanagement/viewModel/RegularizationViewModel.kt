package com.rach.firmmanagement.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.rach.firmmanagement.dataClassImp.AddStaffDataClass
import com.rach.firmmanagement.dataClassImp.AdvanceMoneyData
import com.rach.firmmanagement.dataClassImp.EmployeeLeaveData
import com.rach.firmmanagement.dataClassImp.Expense
import com.rach.firmmanagement.dataClassImp.OutForWork
import com.rach.firmmanagement.login.DataClassRegister
import com.rach.firmmanagement.repository.RegularizationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class RegularizationViewModel() : ViewModel() {

    private val repository= RegularizationRepository()
    private val _employeeRequests = MutableStateFlow<List<DataClassRegister>>(emptyList())
    val employeeRequests: StateFlow<List<DataClassRegister>> = _employeeRequests

    private val _expenseRequests = MutableStateFlow<List<Expense>>(emptyList())
    val expenseRequests: StateFlow<List<Expense>> = _expenseRequests

    private val _leaveRequests = MutableStateFlow<List<EmployeeLeaveData>>(emptyList())
    val leaveRequests: StateFlow<List<EmployeeLeaveData>> = _leaveRequests

    private val _advanceRequests = MutableStateFlow<List<AdvanceMoneyData>>(emptyList())
    val advanceRequests: StateFlow<List<AdvanceMoneyData>> = _advanceRequests

    fun fetchRequests(collection: String, updateState: (List<DocumentSnapshot>) -> Unit) {
        viewModelScope.launch {
            val documents = repository.getPendingRequests(collection)
            updateState(documents)
        }
    }

    fun approveRequest(collection: String, documentId: String) {
        viewModelScope.launch {
            repository.approveRequest(collection, documentId)
            fetchRequests(collection) { updateState(collection, it) }
        }
    }

    fun rejectRequest(collection: String, documentId: String) {
        viewModelScope.launch {
            repository.rejectRequest(collection, documentId)
            fetchRequests(collection) { updateState(collection, it) }
        }
    }

    private fun updateState(collection: String, documents: List<DocumentSnapshot>) {
        when (collection) {
            "pendingEmployees", "pendingEmployers" -> {
                _employeeRequests.value = documents.mapNotNull { it.toObject(DataClassRegister::class.java) }
            }
            "pendingExpenses" -> {
                _expenseRequests.value = documents.mapNotNull { it.toObject(Expense::class.java) }
            }
            "pendingLeaves" -> {
                _leaveRequests.value = documents.mapNotNull { it.toObject(EmployeeLeaveData::class.java) }
            }
            "pendingAdvance" -> {
                _advanceRequests.value = documents.mapNotNull { it.toObject(AdvanceMoneyData::class.java) }
            }
        }
    }



    private val _pendingEmployees = MutableStateFlow<List<DataClassRegister>>(emptyList())
    val pendingEmployees: StateFlow<List<DataClassRegister>> get() = _pendingEmployees
    private val _isLoading = MutableStateFlow<Boolean>(true)
    val isLoading: StateFlow<Boolean> get() = _isLoading
    // Function to fetch pending employees
    fun fetchPendingEmployees(firmName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val employees = repository.fetchPendingEmployee(firmName)
            _pendingEmployees.value = employees
            _isLoading.value = false
        }
    }
    private var employeeListener: ListenerRegistration? = null

    fun listenForEmployeeUpdates(firmName: String, category: String, onUpdate: (String) -> Unit) {
        employeeListener?.remove() // Remove previous listener if any

        employeeListener = repository.listenForEmployeeUpdates(firmName, category) { message, updatedEmployees ->
            _pendingEmployees.value=(updatedEmployees)
            onUpdate(message)
        }
    }

    // Function to approve a pending employee
    fun approvePendingEmployee(firmName: String, data: AddStaffDataClass) {
        viewModelScope.launch {
            try {
                repository.approvePendingEmployee(firmName, data)
                // You can add additional logic here, such as showing a success message
            } catch (e: Exception) {
                // Handle the error appropriately

            }
        }
    }

    // Function to reject a pending employee
    fun rejectPendingEmployee(firmName: String, data: DataClassRegister) {
        viewModelScope.launch {
            try {
                repository.rejectPendingEmployee(firmName, data)
                // You can add additional logic here, such as showing a success message
            } catch (e: Exception) {
                // Handle the error appropriately
            }
        }
    }

    private val _pendingExpenses = MutableStateFlow<List<Expense>>(emptyList())
    val pendingExpenses: StateFlow<List<Expense>> get() = _pendingExpenses
    // Function to fetch pending employees
    fun fetchPendingExpenses(firmName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val expenses = repository.fetchPendingExpenses(firmName)
            _pendingExpenses.value = expenses
            _isLoading.value = false
        }
    }

    fun listenForExpensesUpdates(firmName: String, category: String, onUpdate: (String) -> Unit) {
        employeeListener?.remove() // Remove previous listener if any

        employeeListener = repository.listenForExpensesUpdates(firmName, category) { message, updatedExpenses ->
            _pendingExpenses.value=(updatedExpenses)
            onUpdate(message)
        }
    }

    // Function to approve a pending employee
    fun approvePendingExpenses(employeeIdentity: AddStaffDataClass, data: Expense, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            try {
                repository.approvePendingExpenses(employeeIdentity, data, onSuccess, onFailure)
                // You can add additional logic here, such as showing a success message
            } catch (e: Exception) {
                // Handle the error appropriately

            }
        }
    }

    // Function to reject a pending employee
    fun rejectPendingExpenses(firmName: String, data: Expense, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            try {
                repository.rejectPendingExpenses(firmName, data, onSuccess, onFailure)

                // You can add additional logic here, such as showing a success message
            } catch (e: Exception) {
                // Handle the error appropriately
            }
        }
    }

    private val _pendingLeaves = MutableStateFlow<List<EmployeeLeaveData>>(emptyList())
    val pendingLeaves: StateFlow<List<EmployeeLeaveData>> get() = _pendingLeaves
    // Function to fetch pending employees
    fun fetchPendingLeaves(firmName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val leaves = repository.fetchPendingLeaves(firmName)
            _pendingLeaves.value = leaves
            _isLoading.value = false
        }
    }

    fun listenForLeavesUpdates(firmName: String, category: String, onUpdate: (String) -> Unit) {
        employeeListener?.remove() // Remove previous listener if any

        employeeListener = repository.listenForLeavesUpdates(firmName, category) { message, updatedLeaves ->
            _pendingLeaves.value=(updatedLeaves)
            onUpdate(message)
        }
    }

    // Function to approve a pending employee
    fun approvePendingLeaves(employeeIdentity: AddStaffDataClass, data: EmployeeLeaveData, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            try {
                repository.approvePendingLeaves(employeeIdentity, data, onSuccess, onFailure)
                // You can add additional logic here, such as showing a success message
            } catch (e: Exception) {
                // Handle the error appropriately

            }
        }
    }

    // Function to reject a pending employee
    fun rejectPendingLeaves(employeeIdentity: AddStaffDataClass, data: EmployeeLeaveData, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            try {
                repository.rejectPendingLeaves(employeeIdentity, data, onSuccess, onFailure)

                // You can add additional logic here, such as showing a success message
            } catch (e: Exception) {
                // Handle the error appropriately
            }
        }
    }


    private val _pendingAdvance = MutableStateFlow<List<AdvanceMoneyData>>(emptyList())
    val pendingAdvance: StateFlow<List<AdvanceMoneyData>> get() = _pendingAdvance
    // Function to fetch pending employees
    fun fetchPendingAdvance(firmName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val adv = repository.fetchPendingAdvance(firmName)
            _pendingAdvance.value = adv
            _isLoading.value = false
        }
    }

    fun listenForAdvanceUpdates(firmName: String, category: String, onUpdate: (String) -> Unit) {
        employeeListener?.remove() // Remove previous listener if any

        employeeListener = repository.listenForAdvanceUpdates(firmName, category) { message, updatedAdvance ->
            _pendingAdvance.value=(updatedAdvance)
            onUpdate(message)
        }
    }

    // Function to approve a pending employee
    fun approvePendingAdvance(employeeIdentity: AddStaffDataClass, data: AdvanceMoneyData, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            try {
                repository.approvePendingAdvance(employeeIdentity, data, onSuccess, onFailure)
                // You can add additional logic here, such as showing a success message
            } catch (e: Exception) {
                // Handle the error appropriately

            }
        }
    }

    // Function to reject a pending employee
    fun rejectPendingAdvance(employeeIdentity: AddStaffDataClass, data: AdvanceMoneyData, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            try {
                repository.rejectPendingAdvance(employeeIdentity, data, onSuccess, onFailure)

                // You can add additional logic here, such as showing a success message
            } catch (e: Exception) {
                // Handle the error appropriately
            }
        }
    }


    private val _pendingAttendance = MutableStateFlow<List<OutForWork>>(emptyList())
    val pendingAttendance: StateFlow<List<OutForWork>> get() = _pendingAttendance
    // Function to fetch pending employees
    fun fetchPendingAttendance(firmName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val adv = repository.fetchPendingAttendance(firmName)
            _pendingAttendance.value = adv
            _isLoading.value = false
        }
    }

    fun listenForAttendanceUpdates(firmName: String, category: String, onUpdate: (String) -> Unit) {
        employeeListener?.remove() // Remove previous listener if any

        employeeListener = repository.listenForAttendanceUpdates(firmName, category) { message, updatedAttendance ->
            _pendingAttendance.value=(updatedAttendance)
            onUpdate(message)
        }
    }

    // Function to approve a pending employee
    fun approvePendingAttendance(employeeIdentity: AddStaffDataClass, data: OutForWork, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            try {
                repository.approvePendingAttendance(employeeIdentity, data, onSuccess, onFailure)
                // You can add additional logic here, such as showing a success message
            } catch (e: Exception) {
                // Handle the error appropriately

            }
        }
    }

    // Function to reject a pending employee
    fun rejectPendingAttendance(employeeIdentity: AddStaffDataClass, data: OutForWork, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            try {
                repository.rejectPendingAttendance(employeeIdentity, data, onSuccess, onFailure)

                // You can add additional logic here, such as showing a success message
            } catch (e: Exception) {
                // Handle the error appropriately
            }
        }
    }

}
