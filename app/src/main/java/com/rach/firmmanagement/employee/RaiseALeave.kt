package com.rach.firmmanagement.employee

import android.app.DatePickerDialog
import androidx.compose.ui.unit.dp
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rach.firmmanagement.R
import com.rach.firmmanagement.notification.MyNotification
import com.rach.firmmanagement.ui.theme.FirmManagementTheme
import com.rach.firmmanagement.ui.theme.progressBarBgColor
import com.rach.firmmanagement.viewModel.EmployeeViewModel1
import com.rach.firmmanagement.viewModel.LoginViewModel
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaveRequestScreen(
    employeeViewModel1: EmployeeViewModel1 = viewModel(),
    loginViewModel: LoginViewModel
) {

    val leaveType by employeeViewModel1.leaveType.collectAsState()
    val startDate by employeeViewModel1.startingDate.collectAsState()
    val endDate by employeeViewModel1.endDate.collectAsState()
    val reason by employeeViewModel1.reason.collectAsState()
    val state by employeeViewModel1.circularBarState.collectAsState()
    val adminPhoneNumber by loginViewModel.firmOwnerNumber.collectAsState()


    val scrollState = rememberScrollState()

    val scope = rememberCoroutineScope()

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    fun showDatePickerDialog(onDateSelected: (String) -> Unit) {

        DatePickerDialog(
            context,
            { _,
              selectedYear,
              selectedMonth,
              selectedDay ->
                onDateSelected("$selectedDay/${selectedMonth + 1}/$selectedYear")
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)

        ).show()

    }


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
                colors = TextFieldDefaults.outlinedTextFieldColors(),
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
                colors = TextFieldDefaults.outlinedTextFieldColors(),
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
                colors = TextFieldDefaults.outlinedTextFieldColors(),
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
                colors = TextFieldDefaults.outlinedTextFieldColors(),
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
                                adminPhoneNumber = adminPhoneNumber,
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
                            Toast.makeText(context, "Please Fill all the fields", Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
            ) {
                Text(text = "Submit", fontSize = 18.sp, color = Color.White)
            }
        }

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

@Preview(showBackground = true)
@Composable
fun Prevbiwew() {
    FirmManagementTheme {
        LeaveRequestScreen(
            loginViewModel = LoginViewModel()
        )
    }
}