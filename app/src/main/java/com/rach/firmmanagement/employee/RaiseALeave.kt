package com.rach.firmmanagement.employee

import androidx.compose.foundation.lazy.items
import android.app.DatePickerDialog
import androidx.compose.ui.unit.dp
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rach.firmmanagement.R
import com.rach.firmmanagement.dataClassImp.EmployeeLeaveData
import com.rach.firmmanagement.firmAdminOwner.CustomButton
import com.rach.firmmanagement.firmAdminOwner.showDateRangePicker
import com.rach.firmmanagement.firmAdminOwner.showMonthPicker
import com.rach.firmmanagement.notification.MyNotification
import com.rach.firmmanagement.ui.theme.FirmManagementTheme
import com.rach.firmmanagement.ui.theme.blueAcha
import com.rach.firmmanagement.ui.theme.fontBablooBold
import com.rach.firmmanagement.ui.theme.progressBarBgColor
import com.rach.firmmanagement.viewModel.EmployeeViewModel1
import com.rach.firmmanagement.viewModel.LoginViewModel
import com.rach.firmmanagement.viewModel.ProfileViewModel
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaveRequestScreen(
    employeeViewModel1: EmployeeViewModel1 = viewModel(),
    loginViewModel: LoginViewModel,
    onViewLeaveHistoryClick: () -> Unit,
    profileViewModel: ProfileViewModel=viewModel()
) {

    val leaveType by employeeViewModel1.leaveType.collectAsState()
    val startDate by employeeViewModel1.startingDate.collectAsState()
    val endDate by employeeViewModel1.endDate.collectAsState()
    val reason by employeeViewModel1.reason.collectAsState()
    val state by employeeViewModel1.circularBarState.collectAsState()
    val adminPhoneNumber by loginViewModel.firmOwnerNumber.collectAsState()

    val employeeIdentity by profileViewModel.employeeIdentity.collectAsState()
    val loading by profileViewModel.loading

    val scrollState = rememberScrollState()

    val scope = rememberCoroutineScope()

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    LaunchedEffect(Unit) {
        profileViewModel.getEmployeeIdentity()
    }

    fun showDatePickerDialog(onDateSelected: (String) -> Unit) {

        DatePickerDialog(
            context,
            { _,
              selectedYear,
              selectedMonth,
              selectedDay ->
                onDateSelected("$selectedDay-${selectedMonth + 1}-$selectedYear")
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)

        ).show()

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
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .background(Color.White)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Leave Request",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Leave Type Dropdown
                OutlinedTextField(
                    value = leaveType,
                    onValueChange = { employeeViewModel1.onChangeLeaveType(it) },
                    label = { Text("Leave Type") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Start Date Field
                OutlinedTextField(
                    value = startDate,
                    onValueChange = { employeeViewModel1.onChangeStartingDate(it) },
                    label = { Text("Start Date") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),

                    shape = RoundedCornerShape(8.dp),
                    trailingIcon = {
                        IconButton(onClick = {
                            showDatePickerDialog {
                                employeeViewModel1.onChangeStartingDate(
                                    it
                                )
                            }
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.calendar),
                                contentDescription = "start Date"
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // End Date Field
                OutlinedTextField(
                    value = endDate,
                    onValueChange = { employeeViewModel1.onChangeEndDate(it) },
                    label = { Text("End Date") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),

                    shape = RoundedCornerShape(8.dp),
                    trailingIcon = {
                        IconButton(onClick = {

                            showDatePickerDialog { employeeViewModel1.onChangeEndDate(it) }
                        }) {

                            Icon(
                                painter = painterResource(id = R.drawable.calendar),
                                contentDescription = "start Date"
                            )

                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Reason Field
                OutlinedTextField(
                    value = reason,
                    onValueChange = { employeeViewModel1.onChangeReason(it) },
                    label = { Text("Reason") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    shape = RoundedCornerShape(8.dp),
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Submit Button
                Button(
                    onClick = {
                        scope.launch {
                            if (leaveType.isNotEmpty() && reason.isNotEmpty() && startDate.isNotEmpty() && endDate.isNotEmpty()) {

                                employeeViewModel1.raiseALeave(
                                    adminPhoneNumber = employeeIdentity.adminNumber.toString(),
                                    firmName = employeeIdentity.firmName.toString(),
                                    onSuccess = {
                                        val notification = MyNotification(
                                            context = context,
                                            title = "Firm Management App",
                                            message = "Request Added"
                                        )
                                        notification.fireNotification()

                                    },
                                    onFailure = {

                                        val notification = MyNotification(
                                            context = context,
                                            title = "Firm Management App",
                                            message = "Request Added Failed"
                                        )
                                        notification.fireNotification()

                                    }
                                )

                            } else {
                                Toast.makeText(
                                    context,
                                    "Please Fill all the fields",
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                            }
                        }
                    },
                    modifier = Modifier.width(120.dp),
                    colors = ButtonDefaults.buttonColors(
                        blueAcha
                    )
                ) {

                    Text(
                        text = "Submit",
                        color = Color.White,
                        style = fontBablooBold
                    )

                }
            }
            CustomButton(
                text = "View Leave History",
                onClick = onViewLeaveHistoryClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )

            if (state) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(progressBarBgColor.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

            }
        }
    }
}

@Composable
fun ViewLeaveHistory(
    employeeViewModel1: EmployeeViewModel1 = viewModel(),
    loginViewModel: LoginViewModel,
    profileViewModel: ProfileViewModel
) {
    val leaveRequests by employeeViewModel1.leaves.collectAsState(initial = emptyList())
    val loading by employeeViewModel1.circularBarState.collectAsState()
    val showMonthPickerDialog = remember { mutableStateOf(false) }
    val showDateRangePickerDialog = remember { mutableStateOf(false) }
    val selectedMonth by employeeViewModel1.selectedLeaveMonth.collectAsState()
    val fromDate by employeeViewModel1.selectedLeaveFromDate.collectAsState()
    val toDate by employeeViewModel1.selectedLeaveToDate.collectAsState()
    val employeeIdentity by profileViewModel.employeeIdentity.collectAsState()
    val identityLoading by profileViewModel.loading

    val adminPhoneNumber = employeeIdentity.adminNumber.toString()

    // Get current year
    val currentYear = remember { java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) }
    val currentMonth = remember { java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) } +1

    LaunchedEffect(key1 = Unit) {
        // Trigger getLeaves for the whole year by default
        employeeViewModel1.getLeaves(
            adminPhoneNumber = adminPhoneNumber,
            selectedMonth = "", // Empty for whole year
            from = "01-$currentMonth-$currentYear",
            to = "31-$currentMonth-$currentYear",
            onSuccess = {},
            onFailure = {}
        )
    }

    if (loading || identityLoading) {
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
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Select Month
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showMonthPickerDialog.value = true }
                ) {
                    OutlinedTextField(
                        value = selectedMonth,
                        onValueChange = {},
                        label = { Text("By Month") },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = { showMonthPickerDialog.value = true }) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "Date Picker"
                                )
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.width(5.dp))

                CustomButton(
                    onClick = {
                        employeeViewModel1.getLeaves(
                            adminPhoneNumber = adminPhoneNumber,
                            selectedMonth = selectedMonth,
                            from = "",
                            to = "",
                            onSuccess = {},
                            onFailure = {}
                        )
                    },
                    text = "Search"
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Select Date Range
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showDateRangePickerDialog.value = true }
                ) {
                    OutlinedTextField(
                        value = "$fromDate to $toDate",
                        onValueChange = {},
                        label = { Text("By Date Range") },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = { showDateRangePickerDialog.value = true }) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "Date Picker"
                                )
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.width(5.dp))

                CustomButton(
                    onClick = {
                        employeeViewModel1.getLeaves(
                            adminPhoneNumber = adminPhoneNumber,
                            selectedMonth = "",
                            from = fromDate,
                            to = toDate,
                            onSuccess = {},
                            onFailure = {}
                        )
                    },
                    text = "Search"
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            if (leaveRequests.isEmpty()) {
                NoDataFound(message = "No Data Found")
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()

                ) {
                    items(leaveRequests) { leave ->
                        LeaveCard(leave)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            // Month Picker Dialog
            if (showMonthPickerDialog.value) {
                showMonthPicker(
                    onMonthSelected = { selected ->
                        employeeViewModel1.onSelectedLeaveMonthChange(selected)
                        showMonthPickerDialog.value = false
                    },
                    onDismissRequest = { showMonthPickerDialog.value = false }
                )
            }

            // Date Range Picker Dialog
            if (showDateRangePickerDialog.value) {
                showDateRangePicker(
                    onRangeSelected = { from, to ->
                        employeeViewModel1.onSelectedFromDateChange(from)
                        employeeViewModel1.onSelectedLeaveToDateChange(to)
                        showDateRangePickerDialog.value = false
                    },
                    onDismissRequest = { showDateRangePickerDialog.value = false }
                )
            }
        }
    }
}

@Composable
fun LeaveCard(leave: EmployeeLeaveData) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .shadow(2.dp,  shape = RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = leave.type.toString(),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.weight(1f)
            )
            val statusText = when (leave.status) {
                1 -> "Approved"
                0 -> "Not Approved yet"
                else -> "Rejected"
            }
            Text(
                text = statusText, // Assuming `status` is a property in `LeaveDataClass`
                style = MaterialTheme.typography.bodySmall,
                color = when (leave.status) {
                    1 -> Color.Green
                    0 -> Color.Gray
                    else -> Color.Red
                }
            )
        }
        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "${leave.startingDate} - ${leave.endDate}",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = leave.reason.toString(),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}



@Preview(showBackground = true)
@Composable
fun Prevbiwew() {
    FirmManagementTheme {
        LeaveRequestScreen(
            loginViewModel = LoginViewModel(),
            onViewLeaveHistoryClick = {},
            employeeViewModel1 = EmployeeViewModel1()
        )
    }
}