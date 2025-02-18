package com.rach.firmmanagement.firmAdminOwner

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rach.firmmanagement.dataClassImp.AddStaffDataClass
import com.rach.firmmanagement.necessaryItem.CustomOutlinedTextFiled
import com.rach.firmmanagement.necessaryItem.DatePickerHaiDialog
import com.rach.firmmanagement.notification.MyNotification
import com.rach.firmmanagement.ui.theme.FirmManagementTheme
import com.rach.firmmanagement.ui.theme.blueAcha
import com.rach.firmmanagement.viewModel.AdminViewModel
import com.rach.firmmanagement.viewModel.AllEmployeeViewModel
import com.rach.firmmanagement.viewModel.ProfileViewModel
import kotlinx.coroutines.launch
/*
@Composable
fun AddTask(viewModel: AdminViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {

    val context = LocalContext.current
    val task by viewModel.task.collectAsState()
    val formattedDate by viewModel.registrationDate.collectAsState()
    val scope = rememberCoroutineScope()
    val progressbar by viewModel.state.collectAsState()
    var isError by remember {
        mutableStateOf(false)
    }




    Box(modifier = Modifier.fillMaxSize()) {

        if (progressbar) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 22.dp, end = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {


            CustomOutlinedTextFiled(
                value = formattedDate,
                onValueChange = { viewModel.onChangeRegistrationDate(it) },
                label = "Date",
                singleLine = true,
                isError = false,
                readOnly = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            CustomOutlinedTextFiled(
                value = task,
                onValueChange = { viewModel.onChangeTask(it) },
                label = "Enter Task",
                singleLine = false,
                isError = isError,
                readOnly = false
            )

            Spacer(modifier = Modifier.height(15.dp))

            Button(
                onClick = {
                    scope.launch {
                        if (task.isEmpty()){
                            isError = true
                        }else{
                            viewModel.onStateChange(true)
                            viewModel.addTask(
                                onSuccess = {
                                    Toast.makeText(context, "Task Added", Toast.LENGTH_LONG).show()
                                    viewModel.onStateChange(false)
                                    val notification = MyNotification(
                                        context= context,
                                        title = "Firm Management App",
                                        message = "Task Added"
                                    )
                                    notification.fireNotification()

                                },
                                onFailure = {
                                    Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_LONG)
                                        .show()
                                    viewModel.onStateChange(false)
                                    val notification = MyNotification(
                                        context= context,
                                        title = "Firm Management App",
                                        message = "Task Failed"
                                    )
                                    notification.fireNotification()
                                }
                            )
                        }
                    }
                },
                modifier = Modifier.width(120.dp),
                colors = ButtonDefaults.buttonColors(
                    blueAcha
                )
            ) {

                Text(
                    text = "Add Task",
                    color = Color.White,
                    style = fontBablooBold
                )

            }

        }


    }

}

 */

@Composable
fun AddTask(
    allEmployeeViewModel: AllEmployeeViewModel,
    adminViewModel: AdminViewModel = viewModel(),
    profileViewModel: ProfileViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val assignDate by adminViewModel.assignTaskDate.collectAsState()
    val submitDate by adminViewModel.submitionTaskDate.collectAsState()
    val task by adminViewModel.task.collectAsState()
    val progressbar by adminViewModel.state.collectAsState()
    val selectedEmployees = remember { mutableStateOf(setOf<AddStaffDataClass>()) }

    val employees = allEmployeeViewModel.employeeList.value // Assuming employee list comes from AdminViewModel
    val employeeLoading by allEmployeeViewModel.isEmployeeLoading

    val admin = allEmployeeViewModel.adminList.value
    val isAdminLoading = allEmployeeViewModel.isAdminLoading.value

    var isError by remember { mutableStateOf(false) }

    val employeeIdentity by profileViewModel.employeeIdentity.collectAsState()
    val identityLoading by profileViewModel.loading
    val role=employeeIdentity.role

    Box(modifier = Modifier.fillMaxSize()) {
        if (progressbar || employeeLoading || identityLoading || isAdminLoading) {
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
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                EmployeeSelection(
                    employees = employees + if(role=="Super Admin")admin else emptyList(),
                    selectedEmployees = selectedEmployees
                )

                Spacer(modifier = Modifier.height(20.dp))

                DatePickerHaiDialog(
                    label = "Assign Date",
                    value = assignDate,
                    onValueChange = { adminViewModel.onChangeAssignTaskDate(it) },
                    context = LocalContext.current,
                    read = true
                )

                Spacer(modifier = Modifier.height(20.dp))
                DatePickerHaiDialog(
                    label = "Submit Date",
                    value = submitDate,
                    onValueChange = { adminViewModel.onChangeSubmitionTaskDate(it) },
                    context = LocalContext.current,
                    read = true
                )

                Spacer(modifier = Modifier.height(20.dp))

                CustomOutlinedTextFiled(
                    value = task,
                    onValueChange = { adminViewModel.onChangeTask(it) },
                    label = "Enter Task",
                    singleLine = false,
                    isError = isError,
                    readOnly = false
                )

                Spacer(modifier = Modifier.height(20.dp))

                CustomButton(
                    onClick = {
                        if (task.isEmpty()) {
                            isError = true
                        } else {
                            scope.launch {
                                adminViewModel.onStateChange(true)
                                adminViewModel.assignTask(
                                    selectedEmployees = selectedEmployees.value,
                                    adminPhoneNumber=employeeIdentity.adminNumber.toString(),
                                    onSuccess = {
                                        Toast.makeText(context, "Task Assigned", Toast.LENGTH_LONG).show()
                                        adminViewModel.onStateChange(false)
                                        val notification = MyNotification(
                                            context = context,
                                            title = "Firm Management App",
                                            message = "Task Added"
                                        )

                                        notification.fireNotification()
                                    },
                                    onFailure = {
                                        Toast.makeText(context, "Task Assignment Failed", Toast.LENGTH_LONG).show()
                                        adminViewModel.onStateChange(false)
                                    }
                                )
                            }
                        }
                    },
                    text = "Assign Task",
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AddTaskPreview() {
    FirmManagementTheme {
        AddTask(allEmployeeViewModel = AllEmployeeViewModel(), profileViewModel = ProfileViewModel())
    }
}