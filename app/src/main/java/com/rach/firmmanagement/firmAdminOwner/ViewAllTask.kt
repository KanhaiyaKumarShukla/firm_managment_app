package com.rach.firmmanagement.firmAdminOwner

import android.util.Log
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rach.firmmanagement.ui.theme.FirmManagementTheme
import com.rach.firmmanagement.viewModel.AdminViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import com.rach.firmmanagement.dataClassImp.AddStaffDataClass
import com.rach.firmmanagement.dataClassImp.AddTaskDataClass
import com.rach.firmmanagement.dataClassImp.Remark
import com.rach.firmmanagement.dataClassImp.ViewAllEmployeeDataClass
import com.rach.firmmanagement.employee.AddRemarkDialog
import com.rach.firmmanagement.employee.RemarksListDialog
import com.rach.firmmanagement.ui.theme.blueAcha
import com.rach.firmmanagement.ui.theme.fontBablooBold
import com.rach.firmmanagement.viewModel.AllEmployeeViewModel
import com.rach.firmmanagement.viewModel.ProfileViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


@Composable
fun ViewAllTask(
    adminViewModel: AdminViewModel= viewModel(),
    allEmployeeViewModel: AllEmployeeViewModel,
    profileViewModel: ProfileViewModel
) {

    val tasks = adminViewModel.tasks.collectAsState()
    val loading = adminViewModel.loading.collectAsState()
    val employees by allEmployeeViewModel.employeeList
    val employeeLoading by allEmployeeViewModel.isEmployeeLoading
    val employeeIdentity by profileViewModel.employeeIdentity.collectAsState()
    val identityLoading by profileViewModel.loading
    LaunchedEffect(Unit) {
        allEmployeeViewModel.loadAllEmployee(employeeIdentity.firmName.toString())
    }
    LaunchedEffect(employees) {
        if(!employees.isEmpty()){
            adminViewModel.loadTasks(employees)
        }
    }

    if (loading.value || employeeLoading || identityLoading) { // Use .value for State objects
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
        LazyColumn {
            items(tasks.value) { item -> // Access .value for the list
                ViewAllTaskDesign(
                    item = item,
                    onAddRemark = { remark ->
                        adminViewModel.addRemark(
                            isCommon = item.isCommon,
                            taskId = item.id,
                            employeePhone = item.employeePhoneNumber,
                            newRemark = remark,
                            onSuccess = {
                                Log.d("TAG", "Remark added successfully")
                            },
                            onFailure = {
                                Log.d("TAG", "Failed to add remark")
                            },
                            adminPhoneNumber = employeeIdentity.adminNumber.toString()
                        )
                    },
                    fetchRemarks = {
                        adminViewModel.fetchRemarks(
                            isCommon = item.isCommon,
                            taskId = item.id,
                            employeePhone = item.employeePhoneNumber,
                            adminPhoneNumber = employeeIdentity.adminNumber.toString()
                        )
                    },
                    onDeleteClick = {
                        adminViewModel.deleteTask(
                            task= item,
                            adminPhoneNumber = employeeIdentity.adminNumber.toString()
                        )
                    },
                    addRealtimeRemarksListener = { onRemarksUpdated ->
                        adminViewModel.addRealtimeRemarksListener(
                            employeePhone = item.employeePhoneNumber,
                            taskId = item.id,
                            onRemarksUpdated = onRemarksUpdated,
                            adminPhoneNumber = employeeIdentity.adminNumber.toString()
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun ViewOneEmployeeTask(
    adminViewModel: AdminViewModel= viewModel(),
    employee: AddStaffDataClass,
    profileViewModel: ProfileViewModel
){
    val tasks = adminViewModel.oneEmployeeTask.collectAsState()
    val loading = adminViewModel.loading.collectAsState()

    val employeeIdentity by profileViewModel.employeeIdentity.collectAsState()
    val identityLoading by profileViewModel.loading

    LaunchedEffect(Unit) {
        adminViewModel.loadOneEmployeeTask(employee =  employee, adminPhoneNumber = employeeIdentity.adminNumber.toString())
    }
    if (loading.value || identityLoading) { // Use .value for State objects
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
        LazyColumn {
            items(tasks.value) { item -> // Access .value for the list
                ViewAllTaskDesign(
                    item = item,
                    onAddRemark = { remark ->
                        adminViewModel.addRemark(
                            isCommon = item.isCommon,
                            taskId = item.id,
                            employeePhone = item.employeePhoneNumber,
                            newRemark = remark,
                            onSuccess = {
                                Log.d("TAG", "Remark added successfully")
                            },
                            onFailure = {
                                Log.d("TAG", "Failed to add remark")
                            },
                            adminPhoneNumber = employeeIdentity.adminNumber.toString()
                        )
                    },
                    fetchRemarks = {
                        adminViewModel.fetchRemarks(
                            isCommon = item.isCommon,
                            taskId = item.id,
                            employeePhone = item.employeePhoneNumber,
                            adminPhoneNumber = employeeIdentity.adminNumber.toString()
                        )
                    },
                    onDeleteClick = {
                        adminViewModel.deleteTask(
                            task=item,
                            adminPhoneNumber = employeeIdentity.adminNumber.toString()
                        )
                    },
                    addRealtimeRemarksListener = { onRemarksUpdated ->
                        adminViewModel.addRealtimeRemarksListener(
                            employeePhone = item.employeePhoneNumber,
                            taskId = item.id,
                            onRemarksUpdated = onRemarksUpdated,
                            adminPhoneNumber = employeeIdentity.adminNumber.toString()
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun ViewAllTaskDesign(
    item: AddTaskDataClass,
    onAddRemark: (String) -> Unit,
    fetchRemarks: suspend () -> List<Remark>,
    onDeleteClick: () -> Unit,
    addRealtimeRemarksListener: (onRemarksUpdated: (List<Remark>) -> Unit) -> Unit

) {

    val scope = rememberCoroutineScope()
    var showAddRemarkDialog by remember { mutableStateOf(false) }
    var showRemarkListDialog by remember { mutableStateOf(false) }
    var remarkCount by remember { mutableIntStateOf(item.remarks.size) }
    var remarks by remember { mutableStateOf<List<Remark>>(emptyList()) }
    var newRemarkText = remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(Unit) {
        // This will run when the composable is first composed or recomposed
        addRealtimeRemarksListener { updatedRemarks ->
            remarks = updatedRemarks
        }
        //remarks = fetchRemarks()
        Log.d("Task", "RemarkCount: ${remarks.size}")
    }

    LaunchedEffect(showRemarkListDialog) {
        if (showRemarkListDialog) {
            scope.launch {
                remarks = fetchRemarks()
                remarkCount = remarks.size
                Log.d("Task", "RemarkCount: ${remarks.size}")
            }
        }
    }
    LaunchedEffect(showAddRemarkDialog) {
        if (!showAddRemarkDialog) {
            scope.launch {
                remarks = fetchRemarks()
                Log.d("Task", "Add remark : RemarkCount: ${remarks.size}")
            }
        }
    }
    /*
    ElevatedCard(
        modifier = modifier.padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = modifier.weight(1f)
            ) {
                Text(
                    text = date,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .padding(bottom = 4.dp),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = task,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }

            IconButton(
                onClick = {
                    onDeleteClick()
                },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete, contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

        }
    }*/
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp) // Rounded corners for the card
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Task Date
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Assignment Date: ${item.assignDate}",
                        fontSize = 16.sp,
                        style = fontBablooBold,
                        modifier = Modifier.padding(bottom = 4.dp),
                        textAlign = TextAlign.Start
                    )
                    Text(
                        text = "Submission Date: ${item.submitDate}",
                        fontSize = 14.sp,
                        style = fontBablooBold,
                        textAlign = TextAlign.Start
                    )
                }
                Text(
                    text = if (item.status == "Completed") {
                        "Completed"
                    } else {
                        findStatusOfTask(item.submitDate)
                    },
                    fontSize = 18.sp,
                    style = fontBablooBold,
                    textAlign = TextAlign.Center,
                    color=Color.Red
                )

            }

            Spacer(modifier = Modifier.height(10.dp))

            // Task Description
            Text(
                text = item.task,
                fontSize = 20.sp,
                style = fontBablooBold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp, top = 8.dp),
                textAlign = TextAlign.Center,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Add Remark Button
                Button(
                    onClick = { showAddRemarkDialog = true },
                    modifier = Modifier
                        .padding(top = 16.dp),
                        //.width(120.dp), // Set button width
                    colors = ButtonDefaults.buttonColors(
                        containerColor = blueAcha // Set button background color
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Add Remark",
                        color = Color.White,
                        style = fontBablooBold
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Remark Count Clickable Text
                    Text(
                        text = "${remarks.size} Remarks",
                        fontSize = 14.sp,
                        color = blueAcha,
                        modifier = Modifier
                            .clickable { showRemarkListDialog = true }
                            .padding(4.dp)
                            .align(Alignment.CenterVertically),
                        textAlign = TextAlign.End
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    // Delete Button
                    IconButton(
                        onClick = { onDeleteClick() }, // Delete button action
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Add Remark Dialog
            if (showAddRemarkDialog) {
                AddRemarkDialog(
                    newRemarkText = newRemarkText,
                    onDismiss = { showAddRemarkDialog = false },
                    onSave = {
                        val remark = newRemarkText.value.text
                        onAddRemark(remark)
                        newRemarkText.value = TextFieldValue("") // Reset input
                        showAddRemarkDialog = false
                    }
                )
            }

            // Show Remarks List Dialog
            if (showRemarkListDialog) {
                RemarksListDialog(
                    remarks = remarks,
                    onDismiss = { showRemarkListDialog = false }
                )
            }
        }
    }

}
fun findStatusOfTask(date: String): String {
    return try {
        // Define the date format used for the input date
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val taskDate = dateFormat.parse(date) // Parse the input date string
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time // Get today's date with time set to 00:00:00

        if (taskDate.before(today)) {
            "Overdue" // Task date is before today
        } else {
            "Open" // Task date is today or later
        }
    } catch (e: Exception) {
        Log.e("Task", "$date ,${e.message}")
        "Invalid Date" // Handle any parsing errors
    }
}

@Preview
@Composable
private fun Preview() {
    FirmManagementTheme {
        ViewAllTask(profileViewModel = ProfileViewModel(), allEmployeeViewModel = AllEmployeeViewModel())
    }
}