package com.rach.firmmanagement.employee

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.AlertDialog
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rach.firmmanagement.dataClassImp.AddTaskDataClass
import com.rach.firmmanagement.dataClassImp.Remark
import com.rach.firmmanagement.ui.theme.FirmManagementTheme
import com.rach.firmmanagement.ui.theme.blueAcha
import com.rach.firmmanagement.ui.theme.fontBablooBold
import com.rach.firmmanagement.viewModel.EmlAllTask
import com.rach.firmmanagement.viewModel.LoginViewModel
import com.rach.firmmanagement.viewModel.ProfileViewModel
import kotlinx.coroutines.launch


@Composable
fun SeeTasks(
    viewModel: EmlAllTask,
    profileViewModel: ProfileViewModel
) {
    val tasks = viewModel.taskList.value
    val isLoading = viewModel.isLoading.value
    val employeeIdentity by profileViewModel.employeeIdentity.collectAsState()
    val identityLoding by profileViewModel.loading
    val adminPhoneNumber = employeeIdentity.adminNumber.toString()

    LaunchedEffect(key1 = adminPhoneNumber) {
        viewModel.loadAllTask(adminPhoneNumber = adminPhoneNumber)
    }

    if (isLoading || identityLoding) {

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

    }else if(tasks.isEmpty()){

        NoDataFound()

    }else {
        LazyColumn {
            items(tasks) { item ->

                // SeeTasksDesign(date = item.date ?: "N/A", task = item.task ?: "N/A", taskId = item.id ?: "N/A",)

                SeeTasksDesign(
                    item = item,
                    onAddRemark = { remark ->
                        viewModel.addRemark(
                            adminPhoneNumber = adminPhoneNumber,
                            isCommon = item.isCommon,
                            taskId = item.id,
                            newRemark = remark,
                            onSuccess = {
                                Log.d("TAG", "Remark added successfully")
                            },
                            onFailure = {
                                Log.d("TAG", "Failed to add remark")
                            }
                        )
                    },
                    fetchRemarks = {
                        viewModel.fetchRemarks(
                            adminPhoneNumber = adminPhoneNumber,
                            isCommon = item.isCommon,
                            taskId = item.id
                        )
                    },
                    addRealtimeRemarksListener = { onRemarksUpdated ->
                        viewModel.addRealtimeRemarksListener(
                            adminPhoneNumber = adminPhoneNumber,
                            employeePhone = item.employeePhoneNumber,
                            taskId = item.id,
                            onRemarksUpdated = onRemarksUpdated
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun SeeTasksDesign(
    item: AddTaskDataClass,
    onAddRemark: (String) -> Unit,
    fetchRemarks: suspend () -> List<Remark>,
    addRealtimeRemarksListener: (onRemarksUpdated: (List<Remark>) -> Unit) -> Unit
) {
    val scope = rememberCoroutineScope()
    var showAddRemarkDialog by remember { mutableStateOf(false) }
    var showRemarkListDialog by remember { mutableStateOf(false) }
    var remarkCount by remember { mutableIntStateOf(item.remarks.size) }
    var remarks by remember { mutableStateOf<List<Remark>>(item.remarks) }
    var newRemarkText = remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(Unit) {
        // This will run when the composable is first composed or recomposed
        addRealtimeRemarksListener { updatedRemarks ->
            remarks = updatedRemarks
        }
        //remarks = fetchRemarks()
        Log.d("TAG", "RemarkCount: ${remarks.size}")
    }
    /*
    LaunchedEffect(showRemarkListDialog) {
        if (showRemarkListDialog) {
            scope.launch {
                remarks = fetchRemarks()
                Log.d("TAG", "RemarkCount: ${remarks.size}")
            }
        }
    }
    LaunchedEffect(showAddRemarkDialog) {
        if (!showAddRemarkDialog) {
            scope.launch {
                remarks = fetchRemarks()
                Log.d("TAG", "Add remark : RemarkCount: ${remarks.size}")
            }
        }
    }

     */

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFB2EBF2),
                        Color(0xFFE0F7FA)
                    )
                ),
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Task Date
            Text(
                text = item.assignDate,
                fontSize = 16.sp,
                style = fontBablooBold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                textAlign = TextAlign.Center
            )

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
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Add Remark Button
                Button(
                    onClick = { showAddRemarkDialog = true },
                    modifier = Modifier
                        .padding(top = 16.dp),
                         // Set button width
                    colors = ButtonDefaults.buttonColors(
                        containerColor = blueAcha // Set button background color
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text="Add Remark",
                        color = Color.White,
                        style = fontBablooBold
                    )
                }

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

@Composable
fun AddRemarkDialog(
    newRemarkText: MutableState<TextFieldValue>,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Remark", modifier = Modifier.padding(bottom = 8.dp)) },
        text = {
            Column {
                OutlinedTextField(
                    value = newRemarkText.value,
                    onValueChange = { newRemarkText.value = it },
                    label = { Text("Enter remark") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    singleLine = false,
                    trailingIcon = {
                        if (newRemarkText.value.text.isNotEmpty()) {
                            Icon(
                                imageVector = Icons.Default.Close, // Use Material Design Close icon
                                contentDescription = "Clear text",
                                tint = Color.Black, // Set icon color to black
                                modifier = Modifier
                                    .size(20.dp) // Adjust size as needed
                                    .clickable {
                                        newRemarkText.value = TextFieldValue("") // Clear the text
                                    }
                            )
                        }
                    }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onSave,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .width(100.dp), // Set button width
                colors = ButtonDefaults.buttonColors(
                    containerColor = blueAcha // Set button background color
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "Save",
                    color = Color.White,
                    style = fontBablooBold
                )
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .width(100.dp), // Set button width
                colors = ButtonDefaults.buttonColors(
                    containerColor = blueAcha // Set button background color
                ),
                shape = RoundedCornerShape(8.dp)
                ) {
                Text(
                    "Cancel",
                    color = Color.White,
                    style = fontBablooBold
                )
            }
        },
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
fun RemarksListDialog(
    remarks: List<Remark>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Remarks") },
        text = {
            LazyColumn {
                items(remarks) { remark ->
                    RemarkItem(remark = remark)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .width(120.dp), // Set button width
                colors = ButtonDefaults.buttonColors(
                    containerColor = blueAcha // Set button background color
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "Cancel",
                    color = Color.White,
                    style = fontBablooBold
                )
            }
        }
    )
}

@Composable
fun RemarkItem(remark: Remark) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .border(
                width = 2.dp,
                color = blueAcha,
                shape = RoundedCornerShape(16.dp) // Circular boundary
            )
            .padding(12.dp) // Inner padding for content
    ) {
        Column {
            // Row for "By" and "Date"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween // Space between items
            ) {
                Text(
                    text = "By: ${remark.person}",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Text(
                    text = remark.date,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Message Text
            Text(
                text = remark.message,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold, // Bold text for message
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}


/*

@Composable
fun SeeTasksDesign(
    date: String,
    task: String,
) {

    val scope = rememberCoroutineScope()
    var showAddRemarkDialog by remember { mutableStateOf(false) }
    var showRemarkListDialog by remember { mutableStateOf(false) }
    var remarkCount by remember { mutableStateOf(0) }
    var remarks by remember { mutableStateOf<List<Remark>>(emptyList()) }
    var newRemarkText by remember { mutableStateOf(TextFieldValue()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFB2EBF2),
                        Color(0xFFE0F7FA)
                    )
                ),
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = date,
                fontSize = 16.sp,
                style = fontBablooBold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = task,
                fontSize = 20.sp,
                style = fontBablooBold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp, top = 8.dp),
                textAlign = TextAlign.Center,
                color = Color.Black
            )
            // changes
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Add Remark Button
                Button(
                    onClick = { showAddRemarkDialog = true },
                    modifier = Modifier.padding(4.dp)
                ) {
                    Text("Add Remark")
                }

                // Remark Count Clickable Text
                Text(
                    text = "$remarkCount Remarks",
                    fontSize = 14.sp,
                    color = Color.Blue,
                    modifier = Modifier
                        .clickable { showRemarkListDialog = true }
                        .padding(4.dp)
                        .align(Alignment.CenterVertically),
                    textAlign = TextAlign.End
                )
            }

        }

    }


}

 */

@Preview(showBackground = true)
@Composable
fun SeeTaskPreview() {
    FirmManagementTheme {
        /*
        SeeTasksDesign(
            date = "156526",
            task = "ihqwddkd"
        )

         */
    }
}