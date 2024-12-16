package com.rach.firmmanagement.firmAdminOwner

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rach.firmmanagement.necessaryItem.DatePickerHaiDialog
import com.rach.firmmanagement.necessaryItem.TimePickerHaiDialog
import com.rach.firmmanagement.notification.MyNotification
import com.rach.firmmanagement.ui.theme.FirmManagementTheme
import com.rach.firmmanagement.viewModel.AddWorkHourViewModel
import kotlinx.coroutines.launch

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


@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun Guus() {
    FirmManagementTheme {
        AddWorkHoursScreen()
    }
}