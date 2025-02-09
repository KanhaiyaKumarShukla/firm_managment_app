package com.rach.firmmanagement.firmAdminOwner

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.AlertDialog
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rach.firmmanagement.dataClassImp.AddStaffDataClass
import com.rach.firmmanagement.dataClassImp.PunchInPunchOut
import com.rach.firmmanagement.dataClassImp.ViewAllEmployeeDataClass
import com.rach.firmmanagement.employee.EmployAttendance
import com.rach.firmmanagement.employee.NoDataFound
import com.rach.firmmanagement.firmAdminOwner.ScreenAdmin.EmployeeAttendance
import com.rach.firmmanagement.firmAdminOwner.ui.theme.FirmManagementTheme
import com.rach.firmmanagement.necessaryItem.DatePickerHaiDialog
import com.rach.firmmanagement.ui.theme.blueAcha
import com.rach.firmmanagement.viewModel.AdminViewModel
import com.rach.firmmanagement.viewModel.AllEmployeeViewModel
import com.rach.firmmanagement.viewModel.LoginViewModel
import com.rach.firmmanagement.viewModel.ProfileViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/*
@Composable
fun AllEmployeeAttendance(loginViewModel: LoginViewModel=LoginViewModel(), employeeViewModel: AllEmployeeViewModel) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val selectedDate by employeeViewModel.selectedDate.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        DatePickerHaiDialog(
            label = "Select Date",
            value = selectedDate,
            onValueChange = { employeeViewModel.onChangeSelectedDate(it) },
            context = context,
            read = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Employ Attendance Section
        Log.d("TAG", "selected date: $selectedDate")
        EmployAttendance(loginViewModel = LoginViewModel(), employeeViewModel=employeeViewModel)

    }
}
*/

/*
@Composable
fun AllEmployeeAttendance(
    allEmployeeViewModel: AllEmployeeViewModel = viewModel(),
    adminViewModel: AdminViewModel = viewModel()
) {
    val employees by allEmployeeViewModel.employeeList
    val employeeLoading by allEmployeeViewModel.isLoading
    val selectedEmployees = remember { mutableStateOf(employees.toSet()) }
    val fromDate by adminViewModel.fromDate.collectAsState()
    val toDate by adminViewModel.toDate.collectAsState()
    val attendanceData by adminViewModel.attendance.collectAsState()
    val selectedMonth by adminViewModel.selectedMonth.collectAsState()
    val attendanceLoading by adminViewModel.loading.collectAsState()

    val showMonthPickerDialog = remember { mutableStateOf(false) }
    val showDateRangePickerDialog = remember { mutableStateOf(false) }

    LaunchedEffect(employees) {
        selectedEmployees.value = employees.toSet()
        adminViewModel.fetchAttendance(
            selectedEmployees = selectedEmployees.value.toList(),
            from = fromDate,
            to = toDate,
            selectedMonth = ""
        )
    }
    if (attendanceLoading) { // Use .value for State objects
        Box(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
        ) {
            CircularProgressIndicator(
                color = blueAcha,
                strokeWidth = 2.dp
            )
        }
    }else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Employee Selection
            EmployeeSelection(
                employees = employees,
                selectedEmployees = selectedEmployees
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Date Selection
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Select Month
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            showMonthPickerDialog.value = true
                            Log.d("Attandence", "month clicked, ${showMonthPickerDialog.value}")
                        }
                ) {
                    OutlinedTextField(
                        value = selectedMonth,
                        onValueChange = {},
                        label = { Text("By Month") },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = { showMonthPickerDialog.value = true }) {
                                Icon(imageVector = Icons.Default.DateRange, contentDescription = "Date Picker")
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.width(5.dp))
                CustomButton(
                    onClick = {
                        adminViewModel.fetchAttendance(
                            selectedEmployees = selectedEmployees.value.toList(),
                            selectedMonth = selectedMonth,
                            from = "",
                            to = ""
                        )
                    },
                    text = "Search"
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Select Date Range
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showDateRangePickerDialog.value = true
                            Log.d("Attandence", "date clicked, ${showDateRangePickerDialog.value}")
                        }
                ) {
                    OutlinedTextField(
                        value = "$fromDate to $toDate",
                        onValueChange = {},
                        label = { Text("By Date Range") },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = { showDateRangePickerDialog.value = true }) {
                                Icon(imageVector = Icons.Default.DateRange, contentDescription = "Date Picker")
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.width(5.dp))

                // Search Button
                CustomButton(
                    onClick = {
                        adminViewModel.fetchAttendance(
                            selectedEmployees = selectedEmployees.value.toList(),
                            from = fromDate,
                            to = toDate,
                            selectedMonth = ""
                        )
                    },
                    text = "Search"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            /*
            // Fetch Attendance Data
            LaunchedEffect(fromDate, toDate, selectedEmployees.value) {
                adminViewModel.fetchAttendance(
                    selectedEmployees = selectedEmployees.value.toList(),
                    from = fromDate,
                    to = toDate,
                    selectedMonth = ""
                )
            }

             */

            Spacer(modifier = Modifier.height(16.dp))

            // Attendance Table
            AttendanceTable(
                attendanceData = attendanceData
            )
        }
    }

    if (showMonthPickerDialog.value) {
        Log.d("Attandence", "month clicked1, ${showMonthPickerDialog.value}")
        showMonthPicker(
            onMonthSelected = { selected ->
                adminViewModel.onChangeSelectedMonth(selected)
                showMonthPickerDialog.value = false
            },
            onDismissRequest = { showMonthPickerDialog.value = false }
        )
    }

    // Date Range Picker Dialog
    if (showDateRangePickerDialog.value) {
        Log.d("Attandence", "date clicked1, ${showDateRangePickerDialog.value}")
        showDateRangePicker(
            onRangeSelected = { from, to ->
                adminViewModel.onChangeAttendanceFromDate(from)
                adminViewModel.onChangeAttendanceToDate(to)
                showDateRangePickerDialog.value = false
            },
            onDismissRequest = { showDateRangePickerDialog.value = false }
        )
    }
}

 */

@Composable
fun EmployeeAttendance(
    selectedEmployees: Set<AddStaffDataClass>,
    attendanceData: List<PunchInPunchOut>,
    fromDate: String,
    toDate: String,
    selectedMonth: String,
    attendanceLoading: Boolean,
    employeeIdentityLoading: Boolean,
    onFetchAttendance: (Set<AddStaffDataClass>, String, String, String) -> Unit,
    onMonthChange: (String) -> Unit,
    onDateRangeChange: (String, String) -> Unit,
    toShowOneEmployee: Boolean=false
) {
    val showMonthPickerDialog = remember { mutableStateOf(false) }
    val showDateRangePickerDialog = remember { mutableStateOf(false) }

    if (attendanceLoading || employeeIdentityLoading) { // Use .value for State objects
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
            // Date Selection
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
                        onFetchAttendance(selectedEmployees, selectedMonth, "", "")
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
                        onFetchAttendance(selectedEmployees, "", fromDate, toDate)
                    },
                    text = "Search"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            if (attendanceData.isEmpty()) {
                NoDataFound(message = "No Data Found")
            } else{
            // Attendance Table
                AttendanceTable(attendanceData, toShowOneEmployee = toShowOneEmployee)
            }

            // Month Picker Dialog
            if (showMonthPickerDialog.value) {
                showMonthPicker(
                    onMonthSelected = { selected ->
                        onMonthChange(selected)
                        showMonthPickerDialog.value = false
                    },
                    onDismissRequest = { showMonthPickerDialog.value = false }
                )
            }

            // Date Range Picker Dialog
            if (showDateRangePickerDialog.value) {
                showDateRangePicker(
                    onRangeSelected = { from, to ->
                        onDateRangeChange(from, to)
                        showDateRangePickerDialog.value = false
                    },
                    onDismissRequest = { showDateRangePickerDialog.value = false }
                )
            }
        }
    }
}

@Composable
fun AllEmployeeAttendance(
    allEmployeeViewModel: AllEmployeeViewModel,
    adminViewModel: AdminViewModel = viewModel(),
    profileViewModel: ProfileViewModel
) {
    val employees by allEmployeeViewModel.employeeList
    val selectedEmployees = remember { mutableStateOf(employees.toSet()) }
    val fromDate by adminViewModel.fromDate.collectAsState()
    val toDate by adminViewModel.toDate.collectAsState()
    val attendanceData by adminViewModel.attendance.collectAsState()
    val selectedMonth by adminViewModel.selectedMonth.collectAsState()
    val attendanceLoading by adminViewModel.loading.collectAsState()

    val employeeIdentity by profileViewModel.employeeIdentity.collectAsState()
    val employeeIdentityLoading by profileViewModel.loading

    LaunchedEffect(employees) {
        selectedEmployees.value = employees.toSet()
        adminViewModel.fetchAttendance(
            selectedEmployees = selectedEmployees.value.toList(),
            adminPhoneNumber = employeeIdentity.adminNumber.toString(),
            from = fromDate,
            to = toDate,
            selectedMonth = ""
        )
    }
    Column(modifier = Modifier.fillMaxSize()) {
        // Employee Selection
        EmployeeSelection(
            employees = employees,
            selectedEmployees = selectedEmployees
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Employee Attendance
        EmployeeAttendance(
            selectedEmployees = selectedEmployees.value,
            attendanceData = attendanceData,
            fromDate = fromDate,
            toDate = toDate,
            selectedMonth = selectedMonth,
            attendanceLoading=attendanceLoading,
            employeeIdentityLoading = employeeIdentityLoading,
            onFetchAttendance = { selectedEmployees, selectedMonth, from, to ->
                Log.d("Attendance", "Fetch: $selectedEmployees, $selectedMonth, $from, $to")
                adminViewModel.fetchAttendance(
                    selectedEmployees = selectedEmployees.toList(),
                    adminPhoneNumber = employeeIdentity.adminNumber.toString(),
                    selectedMonth = selectedMonth,
                    from = from,
                    to = to
                )
            },
            onMonthChange = { selected ->
                adminViewModel.onChangeSelectedMonth(selected)
            },
            onDateRangeChange = { from, to ->
                adminViewModel.onChangeAttendanceFromDate(from)
                adminViewModel.onChangeAttendanceToDate(to)
            }
        )
    }
}


@Composable
fun DateSelection(
    fromDate: String,
    toDate: String,
    adminViewModel: AdminViewModel
) {
    Column {
        DatePickerHaiDialog(
            label = "From Date",
            value = fromDate,
            onValueChange = { adminViewModel.onChangeAttendanceFromDate(it) },
            context = LocalContext.current,
            read = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        DatePickerHaiDialog(
            label = "To Date",
            value = toDate,
            onValueChange = { adminViewModel.onChangeAttendanceToDate(it) },
            context = LocalContext.current,
            read = true
        )
    }
}

/*
@Composable
fun showMonthPicker(
    onMonthSelected: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)

    val datePickerDialog = DatePickerDialog(
        LocalContext.current,
        { _, selectedYear, selectedMonth, _ ->
            val selectedMonthString = SimpleDateFormat(
                "MMM yyyy", Locale.getDefault()
            ).format(Calendar.getInstance().apply {
                set(selectedYear, selectedMonth, 1)
            }.time)
            onMonthSelected(selectedMonthString)
        },
        year,
        month,
        1
    )
    datePickerDialog.setOnDismissListener { onDismissRequest() }
    datePickerDialog.show()
}
*/
@Composable
fun showMonthPicker(
    onMonthSelected: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)

    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        title = { Text("Select Month") },
        text = {
            Column {
                DatePickerDialog(
                    context,
                    { _, selectedYear, selectedMonth, _ ->
                        val selectedMonthString = SimpleDateFormat(
                            "MMM yyyy", Locale.getDefault()
                        ).format(Calendar.getInstance().apply {
                            set(selectedYear, selectedMonth, 1)
                        }.time)
                        onMonthSelected(selectedMonthString)
                        onDismissRequest() // Dismiss dialog after selection
                    },
                    year,
                    month,
                    1
                ).show()
            }
        },
        confirmButton = {},
        dismissButton = {
            CustomButton(onClick = { onDismissRequest() }, text = "Cancel")
        }
    )
}


@Composable
fun showDateRangePicker(
    onRangeSelected: (String, String) -> Unit,
    onDismissRequest: () -> Unit
) {
    val fromDate = remember { mutableStateOf("") }
    val toDate = remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        title = { Text("Select Date Range") },
        text = {
            Column {
                DatePickerHaiDialog(
                    label = "From",
                    value = fromDate.value,
                    onValueChange = { fromDate.value = it },
                    context = LocalContext.current,
                    read = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                DatePickerHaiDialog(
                    label = "To",
                    value = toDate.value,
                    onValueChange = { toDate.value = it },
                    context = LocalContext.current,
                    read = true
                )
            }
        },
        confirmButton = {
            CustomButton(
                onClick = {
                    onRangeSelected(fromDate.value, toDate.value)
                },
                text = "Select"
            )
        },
        dismissButton = {
            CustomButton(
                onClick = { onDismissRequest() },
                text = "Cancel"
            )
        }
    )

}

@Composable
fun AttendanceTable(
    attendanceData: List<PunchInPunchOut>,
    toShowOneEmployee: Boolean=false
) {
    Column {
        // Header Row
        Row(modifier = Modifier.fillMaxWidth()) {
            if(!toShowOneEmployee) {
                Text(
                    text = "Employee Name",
                    modifier = Modifier.weight(1f).align(Alignment.CenterVertically),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center
                )
            }
            Text("Date",
                modifier = Modifier.weight(1f).align(Alignment.CenterVertically),
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )
            Text("Attendance Status", modifier = Modifier.weight(1f).align(Alignment.CenterVertically),fontWeight = FontWeight.Bold, fontSize = 15.sp, textAlign = TextAlign.Center )
        }
        Spacer(modifier = Modifier.height(5.dp))
        Divider(
            color = blueAcha,
            thickness = 2.dp
        )

        Spacer(modifier = Modifier.height(5.dp))

        // Data Rows
        attendanceData.forEach { attendance ->
            Row(modifier = Modifier.fillMaxWidth()) {
                if(!toShowOneEmployee) {
                    Text(
                        attendance.name ?: "",
                        modifier = Modifier.weight(1f).align(Alignment.CenterVertically),
                        textAlign = TextAlign.Center
                    )
                }

                Text(
                    attendance.date ?: "", modifier = Modifier.weight(1f).align(Alignment.CenterVertically),
                    textAlign = TextAlign.Center
                )
                Text(
                    getAttendanceStatus(attendance),
                    modifier = Modifier.weight(1f).align(Alignment.CenterVertically),
                    textAlign = TextAlign.Center,
                    color = Color.Red,
                )
            }
        }
    }
}

fun getAttendanceStatus(attendance: PunchInPunchOut): String {
    return when {
        attendance.punchOutTime.isNullOrEmpty() -> "Punch In"
        attendance.punchOutTime.isNotEmpty() -> "Punch Out"
        else -> "No Punch"
        // else -> "Out for Work"
    }
}


@Preview(showBackground = true)
@Composable
fun Previeww() {
    FirmManagementTheme {
        // AllEmployeeAttendance(loginViewModel= LoginViewModel(), employeeViewModel= AllEmployeeViewModel())
    }
}