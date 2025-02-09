package com.rach.firmmanagement.firmAdminOwner

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.OutlinedTextField
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rach.firmmanagement.HomeScreen.OptionCard
import com.rach.firmmanagement.R
import com.rach.firmmanagement.dataClassImp.AddStaffDataClass
import com.rach.firmmanagement.dataClassImp.AdvanceMoneyData
import com.rach.firmmanagement.dataClassImp.EmployeeIdentity
import com.rach.firmmanagement.dataClassImp.EmployeeLeaveData
import com.rach.firmmanagement.dataClassImp.Expense
import com.rach.firmmanagement.dataClassImp.GeofenceItems
import com.rach.firmmanagement.dataClassImp.OutForWork
import com.rach.firmmanagement.employee.ExpenseCard
import com.rach.firmmanagement.employee.LeaveCard
import com.rach.firmmanagement.login.DataClassRegister
import com.rach.firmmanagement.necessaryItem.CustomOutlinedTextFiled
import com.rach.firmmanagement.ui.theme.blueAcha
import com.rach.firmmanagement.viewModel.AdminViewModel
import com.rach.firmmanagement.viewModel.GeofenceViewModel
import com.rach.firmmanagement.viewModel.ProfileViewModel
import com.rach.firmmanagement.viewModel.RegularizationViewModel


@Composable
fun Regularization(
    navigateToEmployeeRequest: () -> Unit,
    navigateToExpensesRequest: () -> Unit,
    navigateToAttendanceRequest: () -> Unit,
    navigateToLeaveRequest: () -> Unit,
    navigateToAdvanceRequest: () -> Unit,
    profileViewModel: ProfileViewModel,
    adminViewModel: AdminViewModel
) {

    val employeeIdentity by profileViewModel.employeeIdentity.collectAsState()
    val loading by profileViewModel.loading
    val firmName = employeeIdentity.firmName
    val adminPhoneNumber = employeeIdentity.adminNumber
    val permissionLoading by adminViewModel.permissionLoading.collectAsState()
    val adminPermissions by adminViewModel.adminPermissions.collectAsState()
    val role = employeeIdentity.role

    LaunchedEffect(Unit) {
        adminViewModel.getAdminPermissions(firmName = firmName.toString(), phoneNumber = adminPhoneNumber.toString())
        //Log.d("permission", "Permission: ${adminPermissions.toString()}")
    }

    val scrollState = rememberScrollState()
    if (loading || permissionLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = blueAcha,
                strokeWidth = 4.dp
            )
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            if(role!="Admin" || adminPermissions.contains("Add Employee")) {
                OptionCard(
                    title = "Employee Request",
                    description = "Add as Employee",
                    drawableRes = R.drawable.add,  // Using AccessTime icon for hours
                    onClick = { navigateToEmployeeRequest() }
                )
            }

            if(role!="Admin" || adminPermissions.contains("Expenses")) {
                OptionCard(
                    title = "Expenses Request",
                    description = "Regularize Expenses",
                    drawableRes = R.drawable.expense,  // Using AccessTime icon for hours
                    onClick = { navigateToExpensesRequest() }
                )
            }
            if(role!="Admin" || adminPermissions.contains("Attendance")) {
                OptionCard(
                    title = "Attendance Request",
                    description = "Regularize Attendance",
                    drawableRes = R.drawable.calendar,  // Using AccessTime icon for hours
                    onClick = { navigateToAttendanceRequest() }
                )
            }
            if(role!="Admin" || adminPermissions.contains("Leave")) {
                OptionCard(
                    title = "Leave Request",
                    description = "Regularize Leave",
                    drawableRes = R.drawable.baseline_accessibility_new_24,  // Using AccessTime icon for hours
                    onClick = { navigateToLeaveRequest() }
                )
            }
            if(role!="Admin" || adminPermissions.contains("Advance")) {
                OptionCard(
                    title = "Advance Request",
                    description = "Regularize Advance",
                    drawableRes = R.drawable.expense_list_ic,  // Using AccessTime icon for hours
                    onClick = { navigateToAdvanceRequest() }
                )
            }
        }
    }
}

@Composable
fun EmployeeRequestItem(data: DataClassRegister, onApprove: () -> Unit, onReject: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = data.name ?: "N/A", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = data.mobileNumber ?: "N/A", style = MaterialTheme.typography.bodyMedium)
                Text(text = data.email ?: "N/A", style = MaterialTheme.typography.bodyMedium)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CustomButton(onClick = onApprove, text="Approve")
                CustomButton(onClick = onReject, text="Reject")
            }
        }
    }
}

@Composable
fun ExpenseRegularizeItem(expense: Expense, onApprove: () -> Unit, onReject: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {


            ExpenseCard(expense = expense)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CustomButton(onClick = onApprove, text="Approve")
                CustomButton(onClick = onReject, text="Reject")
            }
        }
    }
}

@Composable
fun LeaveRegularizeItem(leave: EmployeeLeaveData, onApprove: () -> Unit, onReject: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LeaveCard(leave)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CustomButton(onClick = onApprove, text="Approve")
                CustomButton(onClick = onReject, text="Reject")
            }
        }
    }
}

@Composable
fun AdvanceRegularizeItem(advance: AdvanceMoneyData, onApprove: () -> Unit, onReject: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = advance.emplPhoneNumber ?: "N/A", style = MaterialTheme.typography.titleMedium)
                Text(text = "Amount: ${advance.amount ?: "N/A"}", style = MaterialTheme.typography.bodyMedium)
            }
            Text(text = "${advance.date ?: "N/A"}  ${advance.time ?: "N/A"}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Reason: ${advance.reason ?: "N/A"}", style = MaterialTheme.typography.bodyMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CustomButton(onClick = onApprove, text="Approve")
                CustomButton(onClick = onReject, text="Reject")
            }
        }
    }
}


@Composable
fun AttendanceRegularizeItem(attendance: OutForWork, onApprove: () -> Unit, onReject: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Name: ${attendance.name ?: "N/A"}", style = MaterialTheme.typography.titleMedium)
                Text(text = attendance.date ?: "N/A", style = MaterialTheme.typography.bodyMedium)
            }
            Text(text = "Duration: ${attendance.duration.toString()}", style = MaterialTheme.typography.bodyMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CustomButton(onClick = onApprove, text="Approve")
                CustomButton(onClick = onReject, text="Reject")
            }
        }
    }
}

@Composable
fun EmployeeRequestList(
    employeeIdentity: AddStaffDataClass,
    viewModel: RegularizationViewModel,
    geofenceViewModel: GeofenceViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val pendingEmployees by viewModel.pendingEmployees.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var selectedEmployee by remember { mutableStateOf< DataClassRegister?>(null) }
    val geofences by geofenceViewModel.geofences.collectAsState()
    val firmName = employeeIdentity.firmName.toString()

    LaunchedEffect(Unit) {
        Log.d("EmployeeRequestList", "Fetching pending employees, $firmName : Firm Name")

        viewModel.fetchPendingEmployees(firmName)
        viewModel.listenForEmployeeUpdates(firmName, "pendingEmployees") { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

        geofenceViewModel.fetchAllGeofences(employeeIdentity.firmName.toString())


    }

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
        ) {
            CircularProgressIndicator(
                color = blueAcha,
                strokeWidth = 4.dp
            )
        }
    } else {
        LazyColumn {
            items(pendingEmployees) { employee ->
                EmployeeRequestItem(
                    data = employee,
                    onApprove = {
                        selectedEmployee = employee
                        showDialog = true
                    },
                    onReject = {
                        viewModel.rejectPendingEmployee(firmName, employee)
                    }
                )
            }
        }
    }

    if (showDialog && selectedEmployee != null) {
        ApproveEmployeeDialog(
            geofences=geofences,
            employee = selectedEmployee!!,
            onDismiss = { showDialog = false },
            onConfirm = { updatedEmployee ->
                viewModel.approvePendingEmployee(firmName, updatedEmployee)
                showDialog = false
            }
        )
    }
}

@Composable
fun ApproveEmployeeDialog(
    geofences: List<GeofenceItems>,
    employee: DataClassRegister,
    onDismiss: () -> Unit,
    onConfirm: (AddStaffDataClass) -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val name = remember { mutableStateOf(employee.name ?: "") }
    val phoneNumber = remember { mutableStateOf(employee.mobileNumber ?: "") }
    val role = remember { mutableStateOf("Employee") }
    val salary = remember { mutableStateOf("") }
    val selectedSalaryUnit = remember { mutableStateOf("") }
    val timeVariation = remember { mutableStateOf("") }
    val selectedTimeVariationUnit = remember { mutableStateOf("") }
    val leaveDays = remember { mutableStateOf("") }
    val registrationDate = remember { mutableStateOf("") }
    val selectedGeofence = remember { mutableStateOf(geofences.firstOrNull() ?: GeofenceItems()) }
    val adminNumber = remember { mutableStateOf("") }
    val firmName = remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            CustomButton(
                onClick = {
                    val updatedEmployee = AddStaffDataClass(
                        name = name.value,
                        phoneNumber = phoneNumber.value,
                        role = role.value,
                        salary = salary.value,
                        salaryUnit = selectedSalaryUnit.value,
                        registrationDate = registrationDate.value,
                        timeVariation = timeVariation.value,
                        timeVariationUnit = selectedTimeVariationUnit.value,
                        leaveDays = leaveDays.value,
                        workPlace = selectedGeofence.value,
                        adminNumber = adminNumber.value,
                        firmName = firmName.value
                    )
                    onConfirm(updatedEmployee)
                },
                text = "Register"
            )
        },
        dismissButton = {
            CustomButton(
                onClick = onDismiss,
                text = "Cancel"
            )
        },
        title = { Text("Approve Employee") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                CustomOutlinedTextFiled(
                    value = name.value,
                    onValueChange = { name.value = it },
                    label = "Enter Employee Name",
                    singleLine = true,
                    readOnly = false,
                    isError = false
                )
                CustomOutlinedTextFiled(
                    value = phoneNumber.value,
                    onValueChange = { phoneNumber.value = it },
                    label = "Enter Phone Number",
                    singleLine = true,
                    readOnly = false,
                    isError = false
                )
                CustomOutlinedTextFiled(
                    value = role.value,
                    onValueChange = { role.value = it },
                    label = "Enter Employee Role",
                    singleLine = true,
                    readOnly = false,
                    isError = false
                )
                Spacer(modifier = Modifier.height(5.dp))
                TextFieldWithDropdown(
                    value = salary.value,
                    onValueChange = { salary.value = it },
                    unit = selectedSalaryUnit.value,
                    onUnitChange = { selectedSalaryUnit.value = it },
                    label = "Salary",
                    valueError = false
                )
                Spacer(modifier = Modifier.height(5.dp))
                TextFieldWithDropdown(
                    value = timeVariation.value,
                    onValueChange = { timeVariation.value = it },
                    unit = selectedTimeVariationUnit.value,
                    onUnitChange = { selectedTimeVariationUnit.value = it },
                    label = "Time Variation",
                    valueError = false
                )
                CustomOutlinedTextFiled(
                    value = leaveDays.value,
                    onValueChange = { leaveDays.value = it },
                    label = "Leave Days",
                    singleLine = true,
                    readOnly = false,
                    isError = false
                )
                CustomOutlinedTextFiled(
                    value = firmName.value,
                    onValueChange = { firmName.value = it },
                    label = "Employee FirmName",
                    singleLine = true,
                    readOnly = false,
                    isError = false
                )
                CustomOutlinedTextFiled(
                    value = adminNumber.value,
                    onValueChange = { adminNumber.value = it },
                    label = "Employee Admin Number",
                    singleLine = true,
                    readOnly = false,
                    isError = false
                )
            }
        }
    )
}


@Composable
fun GeofenceDropdown(geofences: List<GeofenceItems>, selectedGeofence: MutableState<GeofenceItems>, onGeofenceSelected: (GeofenceItems) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedTitle by remember { mutableStateOf("Select Geofence") }

    Column {
        OutlinedTextField(
            value = selectedTitle,
            onValueChange = {},
            label = { Text("Select Geofence") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth().clickable { expanded = true }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            geofences.forEach { geofence ->
                DropdownMenuItem(
                    onClick = {
                        selectedTitle = geofence.title ?: "Unknown"
                        selectedGeofence.value = geofence
                        onGeofenceSelected(geofence)
                        expanded = false
                    }
                ) {
                    Text(text = geofence.title ?: "Unknown")
                }
            }
        }
    }
}


@Composable
fun ExpenseRequestList(
    viewModel: RegularizationViewModel,
    employeeIdentity: AddStaffDataClass
) {
    val pendingExpenses by viewModel.pendingExpenses.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        Log.d("ExpensesRequestList", "Fetching pending Expenses, $employeeIdentity : Firm Name")


        viewModel.fetchPendingExpenses(employeeIdentity.firmName.toString())
        viewModel.listenForExpensesUpdates(employeeIdentity.firmName.toString(), "pendingExpenses") { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
        ) {
            CircularProgressIndicator(
                color = blueAcha,
                strokeWidth = 4.dp
            )
        }
    } else {

        LazyColumn {
            items(pendingExpenses) { expenses ->
                ExpenseRegularizeItem(
                    expense = expenses,
                    onApprove = {
                        viewModel.approvePendingExpenses(
                            employeeIdentity,
                            expenses,
                            onSuccess={
                                Toast.makeText(context, "Approved Successfully", Toast.LENGTH_SHORT).show()
                            },
                            onFailure={
                                Toast.makeText(context, "Approval Failed", Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    onReject = {
                        viewModel.rejectPendingExpenses(
                            employeeIdentity.firmName.toString(),
                            expenses,
                            onSuccess={
                                Toast.makeText(context, "Rejected Successfully", Toast.LENGTH_SHORT).show()
                            },
                            onFailure={
                                Toast.makeText(context, "Rejection Failed", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun LeaveRequestList(viewModel: RegularizationViewModel, employeeIdentity: AddStaffDataClass) {
    val requests by viewModel.pendingLeaves.collectAsState()

    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        Log.d("LeaveRequestList", "Fetching pending Leave, $employeeIdentity : Firm Name")


        viewModel.fetchPendingLeaves(employeeIdentity.firmName.toString())
        viewModel.listenForLeavesUpdates(employeeIdentity.firmName.toString(), "pendingLeaves") { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
        ) {
            CircularProgressIndicator(
                color = blueAcha,
                strokeWidth = 4.dp
            )
        }
    } else {

        Log.d("LeaveRequestList", "Requests: $requests")

        LazyColumn {
            items(requests) { request ->
                Log.d("LeaveRequestList", "Request: $request")
                LeaveRegularizeItem(
                    leave = request,
                    onApprove = {
                        viewModel.approvePendingLeaves(
                            employeeIdentity,
                            request,
                            onSuccess={
                                Toast.makeText(context, "Approved Successfully", Toast.LENGTH_SHORT).show()
                            },
                            onFailure={
                                Toast.makeText(context, "Approval Failed", Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    onReject = {
                        viewModel.rejectPendingLeaves(
                            employeeIdentity,
                            request,
                            onSuccess={
                                Toast.makeText(context, "Rejected Successfully", Toast.LENGTH_SHORT).show()
                            },
                            onFailure={
                                Toast.makeText(context, "Rejection Failed", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun AdvanceRequestList(viewModel: RegularizationViewModel, employeeIdentity: AddStaffDataClass) {
    val requests by viewModel.pendingAdvance.collectAsState()

    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        Log.d("AdvanceRequestList", "Fetching pending Leave, $employeeIdentity : Firm Name")


        viewModel.fetchPendingAdvance(employeeIdentity.firmName.toString())
        viewModel.listenForAdvanceUpdates(employeeIdentity.firmName.toString(), "pendingAdvances") { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
        ) {
            CircularProgressIndicator(
                color = blueAcha,
                strokeWidth = 4.dp
            )
        }
    } else {

        Log.d("AdvanceRequestList", "Requests: $requests")

        LazyColumn {
            items(requests) { request ->
                Log.d("AdvanceRequestList", "Request: $request")
                AdvanceRegularizeItem(
                    advance = request,
                    onApprove = {
                        viewModel.approvePendingAdvance(
                            employeeIdentity,
                            request,
                            onSuccess={
                                Toast.makeText(context, "Approved Successfully", Toast.LENGTH_SHORT).show()
                            },
                            onFailure={
                                Toast.makeText(context, "Approval Failed", Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    onReject = {
                        viewModel.rejectPendingAdvance(
                            employeeIdentity,
                            request,
                            onSuccess={
                                Toast.makeText(context, "Rejected Successfully", Toast.LENGTH_SHORT).show()
                            },
                            onFailure={
                                Toast.makeText(context, "Rejection Failed", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun AttendanceRequestList(viewModel: RegularizationViewModel, employeeIdentity: AddStaffDataClass) {
    val requests by viewModel.pendingAttendance.collectAsState()

    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        Log.d("AttendanceRequestList", "Fetching pending Leave, $employeeIdentity : Firm Name")


        viewModel.fetchPendingAttendance(employeeIdentity.firmName.toString())
        viewModel.listenForAttendanceUpdates(employeeIdentity.firmName.toString(), "pendingAttendance") { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
        ) {
            CircularProgressIndicator(
                color = blueAcha,
                strokeWidth = 4.dp
            )
        }
    } else {

        Log.d("AttendanceRequestList", "Requests: $requests")

        LazyColumn {
            items(requests) { request ->
                Log.d("AttendanceRequestList", "Request: $request")
                AttendanceRegularizeItem(
                    attendance = request,
                    onApprove = {
                        viewModel.approvePendingAttendance(
                            employeeIdentity,
                            request,
                            onSuccess={
                                Toast.makeText(context, "Approved Successfully", Toast.LENGTH_SHORT).show()
                            },
                            onFailure={
                                Toast.makeText(context, "Approval Failed", Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    onReject = {
                        viewModel.rejectPendingAttendance(
                            employeeIdentity,
                            request,
                            onSuccess={
                                Toast.makeText(context, "Rejected Successfully", Toast.LENGTH_SHORT).show()
                            },
                            onFailure={
                                Toast.makeText(context, "Rejection Failed", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                )
            }
        }
    }
}


@Composable
fun RegularizationScreen(
    categoryName: String,
    viewModel: RegularizationViewModel,
    profileViewModel: ProfileViewModel,
) {

    val employeeIdentity by profileViewModel.employeeIdentity.collectAsState()
    val loading by profileViewModel.loading

    LaunchedEffect(Unit) {
        profileViewModel.getEmployeeIdentity()
    }

    if(loading || employeeIdentity.firmName==""){
        Box(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
        ) {
            CircularProgressIndicator(
                color = blueAcha,
                strokeWidth = 4.dp
            )
        }
    }else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "$categoryName Regularization", style = MaterialTheme.typography.titleLarge)

            when (categoryName) {
                "Employee" -> EmployeeRequestList(viewModel=viewModel, employeeIdentity = employeeIdentity)
                "Expense" -> ExpenseRequestList(viewModel, employeeIdentity)
                "Leave" -> LeaveRequestList(viewModel, employeeIdentity)
                "Advance" -> AdvanceRequestList(viewModel, employeeIdentity)
                "Attendance" -> AttendanceRequestList(viewModel, employeeIdentity)
            }
        }
    }

}



