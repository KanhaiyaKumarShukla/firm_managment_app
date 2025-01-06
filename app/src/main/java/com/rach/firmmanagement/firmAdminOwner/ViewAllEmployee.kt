package com.rach.firmmanagement.firmAdminOwner

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rach.firmmanagement.R
import com.rach.firmmanagement.ui.theme.FirmManagementTheme
import com.rach.firmmanagement.ui.theme.blueAcha
import com.rach.firmmanagement.ui.theme.fontBablooBold
import com.rach.firmmanagement.ui.theme.red
import com.rach.firmmanagement.viewModel.AllEmployeeViewModel
import androidx.compose.material3.ButtonDefaults
import androidx.navigation.NavController
import com.google.gson.Gson
import com.rach.firmmanagement.dataClassImp.ViewAllEmployeeDataClass

@Composable
fun ViewAllEmployee(
    viewModel: AllEmployeeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    navController: NavController
) {
    val employees = viewModel.employeeList.value
    val isLoading = viewModel.isLoading.value
    val isAssigningTask = viewModel.isAssigningTask.value

    Column(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn {
                items(employees) { employee ->
                    val selectedEmployeeJson = Gson().toJson(employee)
                    EmployeeCard(
                        employee = employee,
                        onClickDelete = {
                            viewModel.deleteEmployee(employee.phoneNumber ?: "")
                        },
                        onAttendanceClick = {
                            navController.navigate("${ScreenAdmin.EmployeeAttendance.route}/$selectedEmployeeJson")
                        },
                        onTaskAssignedClick = {
                            // Handle Task Assigned action
                            navController.navigate("${ScreenAdmin.ViewOneEmployeeTask.route}/$selectedEmployeeJson")
                        },
                        onProfileClick = {
                            // Handle Profile action
                            navController.navigate("${ScreenAdmin.EmployeeProfile.route}/$selectedEmployeeJson")
                        },
                        onHolidayAssignedClick = {
                            val selectedEmployeeJson = Gson().toJson(listOf(employee))
                            navController.navigate("${ScreenAdmin.HolidayTabMenu.route}/$selectedEmployeeJson")
                        },
                        onExpenseClick={
                            //val selectedEmployeeJson = Gson().toJson(listOf(employee))
                            navController.navigate("${ScreenAdmin.EmployeeExpense.route}/$selectedEmployeeJson")
                        }
                    )
                }
            }

            if (isAssigningTask) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
        //AssignTaskToEmployees(viewModel)
    }
}



@Composable
fun EmployeeCard(
    employee: ViewAllEmployeeDataClass,
    onClickDelete: () -> Unit,
    onAttendanceClick: () -> Unit,
    onTaskAssignedClick: () -> Unit,
    onProfileClick: () -> Unit,
    onHolidayAssignedClick: () -> Unit,
    onExpenseClick: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { showDialog = true },
        elevation = 4.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = employee.name.toString(), style = fontBablooBold, fontSize = 18.sp)
                Text(text = employee.role.toString(), fontSize = 16.sp)
                Text(text = employee.phoneNumber.toString(), fontSize = 14.sp)
            }

            Button(
                onClick = onClickDelete,
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD74A49)
                )
            ) {
                Text(text = "Delete", color = Color.White)
            }
        }
    }
    // Dialog box
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Actions for ${employee.name}", style = fontBablooBold, fontSize = 22.sp) },
            text = {
                Column {
                    CustomButton(
                        onClick = {
                            showDialog = false
                            onAttendanceClick()
                        },
                        text="Attendance",
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CustomButton(
                        onClick = {
                            showDialog = false
                            onTaskAssignedClick()
                        },
                        text="Task Assigned",
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    CustomButton(
                        onClick = {
                            showDialog = false
                            onProfileClick()
                        },
                        text="Profile",
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CustomButton(
                        onClick = {
                            showDialog = false
                            onHolidayAssignedClick()
                        },
                        text="Holiday Assigned",
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CustomButton(
                        onClick = {
                            showDialog = false
                            onExpenseClick()
                        },
                        text="Expense",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                CustomButton(
                    onClick = { showDialog = false },
                    text="Close"
                )
            }
        )
    }
}

@Composable
fun AssignTaskToEmployees(viewModel: AllEmployeeViewModel) {
    val context = LocalContext.current
    val taskText = remember { mutableStateOf(TextFieldValue("")) }
    var isAssigningTask = viewModel.isAssigningTask.value

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = taskText.value,
            onValueChange = { taskText.value = it },
            label = { Text("Task Details") },
            modifier = Modifier
                .fillMaxWidth()
        )

        Button(
            onClick = {
                viewModel.assignTaskToSelectedEmployees(taskText.value.text, context)
                isAssigningTask = true
            },
            modifier = Modifier
                .padding(top = 16.dp)
                .width(120.dp), // Set button width
            colors = ButtonDefaults.buttonColors(
                containerColor = blueAcha // Set button background color
            ),
            shape = RoundedCornerShape(8.dp) // Optional: Add rounded corners for better aesthetics
        ) {
            Text(
                text = "Assign Task",
                color = Color.White,
                style = fontBablooBold
            )
        }

    }
}



@Preview(showBackground = true)
@Composable
fun ViewAllEmployeesPreview() {
    FirmManagementTheme {
        EmployeeCard(
            employee= ViewAllEmployeeDataClass(),
            onClickDelete = {},
            onAttendanceClick = {},
            onTaskAssignedClick = {},
            onProfileClick = {},
            onHolidayAssignedClick = {},
            onExpenseClick = {}
        )
    }
}