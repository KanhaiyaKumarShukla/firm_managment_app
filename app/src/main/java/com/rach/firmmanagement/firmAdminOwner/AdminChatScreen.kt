package com.rach.firmmanagement.firmAdminOwner

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rach.firmmanagement.dataClassImp.ViewAllEmployeeDataClass
import com.rach.firmmanagement.viewModel.AdminViewModel
import com.rach.firmmanagement.viewModel.AllEmployeeViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rach.firmmanagement.dataClassImp.AddStaffDataClass
import com.rach.firmmanagement.dataClassImp.MessageDataClass
import com.rach.firmmanagement.employee.InputMessageBar
import com.rach.firmmanagement.employee.MessageList
import com.rach.firmmanagement.ui.theme.FirmManagementTheme
import com.rach.firmmanagement.ui.theme.blueAcha
import com.rach.firmmanagement.viewModel.ProfileViewModel
import kotlinx.coroutines.launch

@Composable
fun AdminChatScreen(
    adminViewModel: AdminViewModel = viewModel(),
    allEmployeeViewModel: AllEmployeeViewModel,
    profileViewModel: ProfileViewModel
) {
    val messages by adminViewModel.messages.collectAsState()
    val messageLoading by adminViewModel.messageLoading.collectAsState()
    val inputMessage by adminViewModel.inputMessage.collectAsState()
    val selectedEmployees = remember { mutableStateOf(setOf<AddStaffDataClass>()) }

    val employees = allEmployeeViewModel.employeeList.value
    val employeeLoading by allEmployeeViewModel.isEmployeeLoading

    val admin = allEmployeeViewModel.adminList.value
    val isAdminLoading = allEmployeeViewModel.isAdminLoading.value

    val employeeNumber = adminViewModel.adminPhoneNumber // Assuming this comes from the ViewModel
    val employeeIdentity by profileViewModel.employeeIdentity.collectAsState()
    val identity by profileViewModel.loading

    if (employeeLoading || identity || messageLoading || isAdminLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = blueAcha,
                strokeWidth = 4.dp
            )
        }
    }else {
        AdminMessageScreen(
            messages = messages,
            employees = employees + if(employeeIdentity.role=="Super Admin")admin else emptyList(),
            selectedEmployees = selectedEmployees,
            inputMessage = inputMessage,
            employeeNumber = employeeNumber,
            onMessageChange = adminViewModel::onChangeMessage,
            onSendMessage = {
                if (inputMessage.isNotEmpty() && selectedEmployees.value.isNotEmpty()) {
                    adminViewModel.sendMessage(
                        selectedEmployees = selectedEmployees.value,
                        message = MessageDataClass(
                            senderName = employeeNumber,
                            receiverName = selectedEmployees.value.joinToString { it.phoneNumber.toString() },
                            message = inputMessage,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                    adminViewModel.onChangeMessage("")
                }
            }
        )
    }
}

@Composable
fun AdminMessageScreen(
    messages: List<MessageDataClass>,
    employees: List<AddStaffDataClass>,
    selectedEmployees: MutableState<Set<AddStaffDataClass>>,
    inputMessage: String,
    employeeNumber: String,
    onMessageChange: (String) -> Unit,
    onSendMessage: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // "To:" Row with EmployeeSelection
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "To:",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(end = 8.dp)
            )
            EmployeeSelection(
                employees = employees,
                selectedEmployees = selectedEmployees
            )
        }

        // MessageList
        Box(
            modifier = Modifier
                .weight(1f) // Makes this part compress when the keyboard is active
                .fillMaxWidth()
        ) {
            MessageList(
                messages = messages,
                employeeNumber = employeeNumber,
                modifier = Modifier.fillMaxSize()
            )
        }

        // InputMessageBar
        InputMessageBar(
            inputMessage = inputMessage,
            onMessageChange = onMessageChange,
            onSendMessage = onSendMessage,
        )
    }
}


