package com.rach.firmmanagement.firmAdminOwner

import android.annotation.SuppressLint
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.FloatingActionButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rach.firmmanagement.R
import com.rach.firmmanagement.ui.theme.FirmManagementTheme
import com.rach.firmmanagement.ui.theme.blueAcha
import com.rach.firmmanagement.ui.theme.fontBablooBold
import com.rach.firmmanagement.viewModel.AllEmployeeViewModel
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.gson.Gson
import com.rach.firmmanagement.dataClassImp.AddStaffDataClass
import com.rach.firmmanagement.dataClassImp.ViewAllEmployeeDataClass
import com.rach.firmmanagement.viewModel.AdminViewModel
import com.rach.firmmanagement.viewModel.ProfileViewModel
import java.net.URLEncoder

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ViewAllEmployee(
    viewModel: AllEmployeeViewModel,
    navController: NavController,
    adminViewModel: AdminViewModel = viewModel(),
    profileViewModel: ProfileViewModel
) {
    val employees = viewModel.employeeList.value
    val isEmployeeLoading = viewModel.isEmployeeLoading.value

    val selectedPermissions by adminViewModel.selectedPermissions.collectAsState()

    val admins=viewModel.adminList.value
    val isAdminLoading = viewModel.isAdminLoading.value

    val employeeIdentity by profileViewModel.employeeIdentity.collectAsState()
    val identityLoading by profileViewModel.loading

    //val isAssigningTask = viewModel.isAssigningTask.value

    val selectedEmployees = remember { mutableStateOf(setOf<AddStaffDataClass>()) }
    val isSelectionMode = remember { mutableStateOf(false) }
    val showDialog = remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val scaffoldState = rememberScaffoldState()
    val context= LocalContext.current
    val permissionsList = listOf("Attendance", "Working Hours", "Holiday", "Task", "Expenses", "Geofence")

    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
            if(isSelectionMode.value==true) {
                FloatingActionButton(
                    onClick = {
                        showDialog.value=true
                    },
                    backgroundColor = blueAcha,
                    elevation = FloatingActionButtonDefaults.elevation(10.dp)
                ) {

                    Icon(
                        imageVector = Icons.Default.Add, contentDescription = "Make Admin",
                        tint = Color.White
                    )

                }
            }
        }
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
                .padding(16.dp)
                .padding(it)
        ) {
            if (isEmployeeLoading || isAdminLoading || identityLoading) {
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

                LazyColumn {
                    items(employees) { employee ->
                        val isSelected = selectedEmployees.value.contains(employee)
                        val selectedEmployeeJson = Uri.encode(Gson().toJson(employee))
                        EmployeeCard(
                            employee = employee,
                            isSelected = isSelected,
                            isSelectionMode = isSelectionMode.value,
                            onCheckedChange = { isChecked ->
                                selectedEmployees.value = if (isChecked) {
                                    selectedEmployees.value + employee
                                } else {
                                    selectedEmployees.value - employee
                                }
                            },
                            onLongPress = {
                                isSelectionMode.value = true
                            },
                            navController = navController,
                            onClickDelete = {
                                viewModel.deleteEmployee(
                                    employee.phoneNumber.toString(),
                                    firmName = employeeIdentity.firmName.toString(),
                                    onSuccess = {
                                        Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
                                    },
                                    onFailure = {
                                        Toast.makeText(context, "Failure", Toast.LENGTH_SHORT).show()

                                    }
                                )
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
                                //val selectedEmployeeJson = Gson().toJson(listOf(employee))
                                //navController.navigate("${ScreenAdmin.HolidayTabMenu.route}/$selectedEmployeeJson")
                                val selectedEmployeeJson = URLEncoder.encode(Gson().toJson(listOf(employee)), "UTF-8")
                                navController.navigate("${ScreenAdmin.HolidayTabMenu.route}/$selectedEmployeeJson")

                            },
                            onExpenseClick = {
                                //val selectedEmployeeJson = Gson().toJson(listOf(employee))
                                navController.navigate("${ScreenAdmin.EmployeeExpense.route}/$selectedEmployeeJson")
                            },
                            onChatClick = {
                                val selectedEmployeeJson = URLEncoder.encode(Gson().toJson(listOf(employee)), "UTF-8")
                                navController.navigate("${ScreenAdmin.AdminMessage.route}/$selectedEmployeeJson")
                            },
                            onSalaryClick = {
                                navController.navigate("${ScreenAdmin.EmployeeSalary.route}/$selectedEmployeeJson")
                            }
                        )
                    }
                }



                /*
                if (isAssigningTask) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                 */
            }
            //AssignTaskToEmployees(viewModel)
        }
    }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { isSelectionMode.value = false },
            title = { Text("Confirm Admin Role") },
            text = {
                Column {
                    Text("Are you sure you want to make the following employees Admin?")
                    selectedEmployees.value.forEach { employee ->
                        Text(text = employee.name.toString())
                    }
                    Text("Select Permissions")

                    Column {
                        permissionsList.forEach { permission ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { adminViewModel.togglePermission(permission) }
                                    .padding(8.dp)
                            ) {
                                androidx.compose.material3.Checkbox(
                                    checked = selectedPermissions.contains(permission),
                                    onCheckedChange = { adminViewModel.togglePermission(permission) }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                androidx.compose.material3.Text(text = permission)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                CustomButton(
                    onClick = {

                        selectedEmployees.value.forEach { employee->
                            adminViewModel.savePermissions(
                                firmName = employee.firmName.toString(),  // Replace with actual firm nam
                                phoneNumber = employee.phoneNumber.toString(),
                                onSuccess = {
                                    Toast.makeText(context, "Permissions Saved", Toast.LENGTH_SHORT)
                                        .show()
                                },
                                onFailure = {
                                    Toast.makeText(context, "Failed to Save", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            )
                        }

                        viewModel.updateEmployeesToAdmin(
                            selectedEmployees.value.toList(),
                            onSuccess={
                                Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
                            },
                            onFailure={
                                Toast.makeText(context, "Failure", Toast.LENGTH_SHORT).show()
                            },
                            firmName = employeeIdentity.firmName.toString()
                        )
                        isSelectionMode.value = false
                        showDialog.value=false
                        selectedEmployees.value = emptySet()
                    },
                    text="Confirm"
                )
            },
            dismissButton = {
                CustomButton(
                    onClick = {
                        isSelectionMode.value = false
                        showDialog.value=false
                        selectedEmployees.value = emptySet()
                    },
                    text="Cancel"
                )

            }
        )
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EmployeeCard(
    employee: AddStaffDataClass,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onLongPress: () -> Unit,
    navController: NavController,
    onClickDelete: () -> Unit,
    onAttendanceClick: () -> Unit,
    onTaskAssignedClick: () -> Unit,
    onProfileClick: () -> Unit,
    onHolidayAssignedClick: () -> Unit,
    onExpenseClick: () -> Unit,
    onChatClick: () -> Unit,
    onSalaryClick: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .combinedClickable(
                onClick = { showDialog = true },
                onLongClick = onLongPress
            ),
        elevation = 4.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {

            if (isSelectionMode) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = onCheckedChange,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
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
                    Spacer(modifier = Modifier.height(8.dp))
                    CustomButton(
                        onClick = {
                            showDialog = false
                            onChatClick()
                        },
                        text="Chatting",
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CustomButton(
                        onClick = {
                            showDialog = false
                            onSalaryClick()
                        },
                        text="Salary",
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

/*
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
*/


@Preview(showBackground = true)
@Composable
fun ViewAllEmployeesPreview() {
    FirmManagementTheme {
        EmployeeCard(
            employee = AddStaffDataClass(),
            onClickDelete = {},
            onAttendanceClick = {},
            onTaskAssignedClick = {},
            onProfileClick = {},
            onHolidayAssignedClick = {},
            onExpenseClick = {},
            isSelected = false,
            isSelectionMode = false,
            onCheckedChange = {},
            onLongPress = {},
            onChatClick = {},
            navController = NavController(LocalContext.current),
            onSalaryClick = {}
        )
    }
}