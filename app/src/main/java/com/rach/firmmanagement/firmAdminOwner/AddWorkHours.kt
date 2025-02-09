package com.rach.firmmanagement.firmAdminOwner

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rach.firmmanagement.necessaryItem.DatePickerHaiDialog
import com.rach.firmmanagement.necessaryItem.TimePickerHaiDialog
import com.rach.firmmanagement.ui.theme.FirmManagementTheme
import com.rach.firmmanagement.viewModel.AddWorkHourViewModel
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import com.rach.firmmanagement.dataClassImp.AddStaffDataClass
import com.rach.firmmanagement.notification.MyNotification
import com.rach.firmmanagement.ui.theme.blueAcha
import com.rach.firmmanagement.viewModel.AllEmployeeViewModel
import com.rach.firmmanagement.viewModel.ProfileViewModel

/*
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddWorkHoursScreen(viewModel: AddWorkHourViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {

    val selectStartTime by viewModel.startTime.collectAsState()
    val selectEndTime by viewModel.endTime.collectAsState()
    val date by viewModel.date.collectAsState()
    val scope = rememberCoroutineScope()

    val context = LocalContext.current

    val isLoading by viewModel.isLoading.collectAsState()





    Box(modifier = Modifier.fillMaxSize()) {

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {

            Scaffold(
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            scope.launch {
                                viewModel.onChangeIsLoading(true)
                                viewModel.addWorkHours(
                                    onSuccess = {
                                        Toast.makeText(
                                            context,
                                            "Working Hour Added",
                                            Toast.LENGTH_LONG
                                        )
                                            .show()
                                        viewModel.onChangeIsLoading(false)
                                        val notification = MyNotification(
                                            context = context,
                                            title = "Firm Management App",
                                            message = "Working Hour Added"
                                        )

                                        notification.fireNotification()

                                    },
                                    onFailure = {
                                        viewModel.onChangeIsLoading(false)
                                        Toast.makeText(
                                            context,
                                            "Something Went Wrong",
                                            Toast.LENGTH_LONG
                                        )
                                            .show()

                                        val notification = MyNotification(
                                            context = context,
                                            title = "Firm Management App",
                                            message = "Working Hour Added Failed Please Try Again"
                                        )
                                        notification.fireNotification()


                                    }
                                )
                            }
                        },
                        backgroundColor = Color(0xFF3F51B5)  // Custom color for Save button
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Save")
                    }
                }
            ) { it ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                        .padding(16.dp)
                ) {
                    // Date Picker Field
                    /*
                    change

                    OutlinedTextField(
                        value = date,
                        onValueChange = { viewModel.onChangeDate(it) },
                        label = { Text(text = "Date") },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { /* Date picker logic */ }) {
                                Icon(Icons.Default.DateRange, contentDescription = "Date Picker")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { /* Date picker logic */ }
                    )

                     */
                    DatePickerHaiDialog(
                        label = "Date",
                        value = date,
                        onValueChange = {
                            viewModel.onChangeDate(it)
                        },
                        context = context,
                        read = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    TimePickerHaiDialog(
                        label = "Start Time",
                        value = selectStartTime,
                        onValueChange = {
                            viewModel.onChangeStartTime(it)
                        },
                        context = context,
                        read = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    TimePickerHaiDialog(label = "End Time", value = selectEndTime, onValueChange = {
                        viewModel.onChangeEndTime(it)
                    }, context = context, read = true)



                    Spacer(modifier = Modifier.height(24.dp))

                    // Total Hours Display
                    TotalHoursDisplay(startTime = selectStartTime, endTime = selectEndTime)
                }
            }

        }


    }

}
*/

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GetWorkHours(
    date: String,
    onDateChange: (String) -> Unit,
    startTime: String,
    onStartTimeChange: (String) -> Unit,
    endTime: String,
    onEndTimeChange: (String) -> Unit,
    onSaveClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            DatePickerHaiDialog(
                label = "Date",
                value = date,
                onValueChange = onDateChange,
                context = LocalContext.current,
                read = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            TimePickerHaiDialog(
                label = "Start Time",
                value = startTime,
                onValueChange = onStartTimeChange,
                context = LocalContext.current,
                read = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            TimePickerHaiDialog(
                label = "End Time",
                value = endTime,
                onValueChange = onEndTimeChange,
                context = LocalContext.current,
                read = true
            )
            Spacer(modifier = Modifier.height(24.dp))

            val (validatedHours, errorMessage) = calculateTotalHours(startTime, endTime)

            TotalHoursDisplay(validatedHours = validatedHours, errorMessage = errorMessage)
        }

        CustomButton(
            onClick = onSaveClick,
            text = "Save",
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable

fun AddWorkHoursScreen(
    workViewModel: AddWorkHourViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    allEmployeeViewModel: AllEmployeeViewModel,
    profileViewModel: ProfileViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val date by workViewModel.date.collectAsState()
    val startTime by workViewModel.startTime.collectAsState()
    val endTime by workViewModel.endTime.collectAsState()
    val selectedEmployees = remember { mutableStateOf(setOf<AddStaffDataClass>()) }
    val employees = allEmployeeViewModel.employeeList.value
    val isLoading = allEmployeeViewModel.isEmployeeLoading.value

    val employeeIdentity by profileViewModel.employeeIdentity.collectAsState()
    val identityLoading by profileViewModel.loading


    Column(modifier = Modifier.fillMaxSize()) {
        if (isLoading || identityLoading) {
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
            EmployeeSelection(
                employees = employees,
                selectedEmployees = selectedEmployees
            )

            GetWorkHours(
                date = date,
                onDateChange = { workViewModel.onChangeDate(it) },
                startTime = startTime,
                onStartTimeChange = { workViewModel.onChangeStartTime(it) },
                endTime = endTime,
                onEndTimeChange = { workViewModel.onChangeEndTime(it) },
                onSaveClick = {
                    if (selectedEmployees.value.isEmpty()) {
                        Toast.makeText(
                            context,
                            "Please select at least one employee.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        scope.launch {
                            workViewModel.onChangeIsLoading(true)
                            workViewModel.addWorkHoursForEmployees(
                                selectedEmployees = selectedEmployees.value,
                                onSuccess = {
                                    Toast.makeText(
                                        context,
                                        "Working Hour Added",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    workViewModel.onChangeIsLoading(false)
                                    val notification = MyNotification(
                                        context = context,
                                        title = "Firm Management App",
                                        message = "Working Hour Added"
                                    )

                                    notification.fireNotification()
                                },
                                onFailure = {
                                    Toast.makeText(
                                        context,
                                        "Something Went Wrong",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    workViewModel.onChangeIsLoading(false)
                                    val notification = MyNotification(
                                        context = context,
                                        title = "Firm Management App",
                                        message = "Working Hour Added Failed Please Try Again"
                                    )
                                    notification.fireNotification()
                                },
                                adminPhoneNumber = employeeIdentity.adminNumber.toString()
                            )
                        }
                    }
                }
            )
        }
    }
}



/*
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TotalHoursDisplay(startTime: String, endTime: String) {
    // Helper function to convert time to total minutes since midnight
    fun timeToMinutes(time: String): Int {
        val parts = time.split(" ")  // Split into time and AM/PM
        val timeParts = parts[0].split(":")  // Split hours and minutes
        val isPM = parts[1] == "PM"  // Check if it's PM
        var hours = timeParts[0].toInt()
        val minutes = timeParts[1].toInt()

        // Convert to 24-hour format
        if (isPM && hours != 12) {
            hours += 12
        } else if (!isPM && hours == 12) {
            hours = 0  // Midnight case
        }

        return hours * 60 + minutes
    }

    // Convert both start and end times to minutes
    val startMinutes = timeToMinutes(startTime)
    val endMinutes = timeToMinutes(endTime)

    // Calculate the difference in minutes
    val totalMinutes = if (endMinutes < startMinutes) {
        // End time is on the next day
        (24 * 60 - startMinutes) + endMinutes
    } else {
        endMinutes - startMinutes
    }

    // Convert the total minutes to hours and minutes
    val totalHours = totalMinutes / 60
    val remainingMinutes = totalMinutes % 60

    // Display the result
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "Total Hours:", style = MaterialTheme.typography.subtitle1)
        Text(text = "$totalHours hours $remainingMinutes minutes", style = MaterialTheme.typography.subtitle1)
    }
}

 */

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TotalHoursDisplay(validatedHours: String?, errorMessage: String?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (errorMessage == null) {
            Text(text = "Total Hours:", style = MaterialTheme.typography.subtitle1)
            Text(text = validatedHours ?: "0 hours 0 minutes", style = MaterialTheme.typography.subtitle1)
        } else {
            Text(text = "Error:", style = MaterialTheme.typography.subtitle1)
            Text(text = errorMessage, style = MaterialTheme.typography.subtitle1)
        }
    }
}

fun calculateTotalHours(startTime: String, endTime: String): Pair<String?, String?> {
    return try {
        fun timeToMinutes(time: String): Int {
            val parts = time.split(" ")
            if (parts.size != 2) throw IllegalArgumentException("Invalid time format: $time")

            val timeParts = parts[0].split(":")
            if (timeParts.size != 2) throw IllegalArgumentException("Invalid hour and minute format: ${parts[0]}")

            val isPM = parts[1] == "PM"
            var hours = timeParts[0].toIntOrNull() ?: throw IllegalArgumentException("Invalid hour: ${timeParts[0]}")
            val minutes = timeParts[1].toIntOrNull() ?: throw IllegalArgumentException("Invalid minutes: ${timeParts[1]}")

            if (isPM && hours != 12) hours += 12 else if (!isPM && hours == 12) hours = 0

            return hours * 60 + minutes
        }

        val startMinutes = timeToMinutes(startTime)
        val endMinutes = timeToMinutes(endTime)

        val totalMinutes = if (endMinutes < startMinutes) {
            (24 * 60 - startMinutes) + endMinutes
        } else {
            endMinutes - startMinutes
        }

        val totalHours = totalMinutes / 60
        val remainingMinutes = totalMinutes % 60

        "$totalHours hours $remainingMinutes minutes" to null
    } catch (e: Exception) {
        null to e.message
    }
}






@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun Guus() {
    FirmManagementTheme {
        // AddWorkHoursScreen()
    }
}