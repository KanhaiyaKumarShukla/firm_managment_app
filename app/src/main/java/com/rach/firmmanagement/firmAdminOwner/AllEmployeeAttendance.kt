package com.rach.firmmanagement.firmAdminOwner

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rach.firmmanagement.employee.EmployAttendance
import com.rach.firmmanagement.firmAdminOwner.ScreenAdmin.EmployeeAttendance
import com.rach.firmmanagement.firmAdminOwner.ui.theme.FirmManagementTheme
import com.rach.firmmanagement.necessaryItem.DatePickerHaiDialog
import com.rach.firmmanagement.viewModel.AllEmployeeViewModel
import com.rach.firmmanagement.viewModel.LoginViewModel

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

@Preview(showBackground = true)
@Composable
fun Previeww() {
    FirmManagementTheme {
        AllEmployeeAttendance(loginViewModel= LoginViewModel(), employeeViewModel= AllEmployeeViewModel())
    }
}