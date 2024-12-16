package com.rach.firmmanagement.firmAdminOwner

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.rach.firmmanagement.R
import com.rach.firmmanagement.notification.MyNotification
import com.rach.firmmanagement.ui.theme.FirmManagementTheme
import com.rach.firmmanagement.ui.theme.fontBablooBold
import com.rach.firmmanagement.ui.theme.progressBarBgColor
import com.rach.firmmanagement.viewModel.AdminViewModel
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HolidayAddScreen(viewModel: AdminViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {

    val holidayName by viewModel.holidayName.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()

    val progressBarState by viewModel.progressBarState2.collectAsState()

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context, { _,
                   selectedYear,
                   selectedMonth,
                   selectedDay ->
            viewModel.onChangeDate("$selectedDay/${selectedMonth + 1}/$selectedYear")
        },
        year, month, day
    )

    // Use a Box to overlay elements
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Main content (holiday form)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(26.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Add New Holiday",
                style = fontBablooBold,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Holiday Name Field
            OutlinedTextField(
                value = holidayName,
                onValueChange = { viewModel.onChangeHolidayName(it) },
                label = { Text("Holiday Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Date Picker Field
            OutlinedTextField(
                value = selectedDate,
                onValueChange = { viewModel.onChangeDate(it) },
                label = { Text("Holiday Date") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                trailingIcon = {
                    IconButton(onClick = { datePickerDialog.show() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.calendar),
                            contentDescription = "Pick Date"
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Submit Button
            Button(
                onClick = {
                    viewModel.onChangeProgressState2(true)
                    scope.launch {
                        if (holidayName.isEmpty() && selectedDate.isEmpty()) {
                            Toast.makeText(context, "Please Fill All the Fields", Toast.LENGTH_LONG).show()
                            viewModel.onChangeProgressState(false)
                        } else {
                            viewModel.addHoliday(
                                onSuccess = {
                                    Toast.makeText(context, "Holiday Added", Toast.LENGTH_LONG).show()
                                    val notification = MyNotification(
                                        context = context,
                                        title = "Firm Management App",
                                        message = "$holidayName Holiday Added"
                                    )
                                    notification.fireNotification()
                                    viewModel.onChangeProgressState(false)
                                },
                                onFailure = {
                                    viewModel.onChangeProgressState(false)
                                    val notification = MyNotification(
                                        context = context,
                                        title = "Firm Management App",
                                        message = "Failed To Add Holiday"
                                    )
                                    notification.fireNotification()
                                },
                                date = selectedDate
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
            ) {
                Text(text = "Add Holiday", fontSize = 18.sp, color = Color.White)
            }
        }

        // Progress bar overlay, displayed when `progressBarState` is true
        if (progressBarState) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(progressBarBgColor.copy(alpha = 0.5f)), // Semi-transparent background
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HolidaysScreenJetPackCompose() {
    FirmManagementTheme {
        HolidayAddScreen()
    }
}