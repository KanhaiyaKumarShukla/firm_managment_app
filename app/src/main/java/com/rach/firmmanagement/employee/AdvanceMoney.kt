package com.rach.firmmanagement.employee

import android.widget.Toast
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rach.firmmanagement.notification.MyNotification
import com.rach.firmmanagement.ui.theme.FirmManagementTheme
import com.rach.firmmanagement.ui.theme.blueAcha
import com.rach.firmmanagement.ui.theme.fontBablooBold
import com.rach.firmmanagement.ui.theme.progressBarBgColor
import com.rach.firmmanagement.viewModel.EmployeeViewModel1
import com.rach.firmmanagement.viewModel.LoginViewModel
import kotlinx.coroutines.launch
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.text.style.TextAlign
import com.rach.firmmanagement.dataClassImp.AdvanceMoneyData
import com.rach.firmmanagement.firmAdminOwner.CustomButton
import com.rach.firmmanagement.firmAdminOwner.showDateRangePicker
import com.rach.firmmanagement.firmAdminOwner.showMonthPicker
import com.rach.firmmanagement.viewModel.ProfileViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvanceMoneyRequestScreen(
    viewModel1: EmployeeViewModel1 = viewModel(),
    loginViewModel: LoginViewModel,
    onViewAdvanceHistoryClick:() -> Unit,
    profileViewModel: ProfileViewModel=viewModel()
) {
    val context = LocalContext.current
    val amount by viewModel1.amount.collectAsState()
    val reason by viewModel1.reasonAdvance.collectAsState()
    val state by viewModel1.circularBarState.collectAsState()
    val adminPhoneNumber by loginViewModel.firmOwnerNumber.collectAsState()
    val scope = rememberCoroutineScope()
    val employeeIdentity by profileViewModel.employeeIdentity.collectAsState()
    val loading by profileViewModel.loading

    LaunchedEffect(Unit) {
        profileViewModel.getEmployeeIdentity()
    }

    if(loading || employeeIdentity.firmName==""){

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
        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Advance Money Request",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Amount Field
                OutlinedTextField(
                    value = amount,
                    onValueChange = { viewModel1.onChangeAmount(it) },
                    label = { Text("Requested Amount") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),

                    shape = RoundedCornerShape(8.dp),
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Reason Field
                OutlinedTextField(
                    value = reason,
                    onValueChange = { viewModel1.onChangeResAdvance(it) },
                    label = { Text("Reason") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    shape = RoundedCornerShape(8.dp),
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Submit Button
                Button(
                    onClick = {
                        scope.launch {
                            if (amount.isEmpty() && reason.isEmpty()) {
                                Toast.makeText(
                                    context,
                                    "Please Fill all the fields",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {

                                viewModel1.advanceMoney(
                                    adminPhoneNumber = adminPhoneNumber,
                                    firmName = employeeIdentity.firmName.toString(),
                                    onSuccess = {
                                        val notification = MyNotification(
                                            context,
                                            title = "Firm Management App",
                                            message = "Request Added"
                                        )

                                        notification.fireNotification()
                                    },
                                    onFailure = {
                                        val notification = MyNotification(
                                            context,
                                            title = "Firm Management App",
                                            message = "Request Added Failed"
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
                        text = "Submit",
                        color = Color.White,
                        style = fontBablooBold
                    )

                }
            }
            CustomButton(
                text = "View Advance History",
                onClick = onViewAdvanceHistoryClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)

            )

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
}

@Composable
fun NoDataFound(message: String = "No Data Found") {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}


@Composable
fun DatePicker(
    selectedMonth: String,
    fromDate: String,
    toDate: String,
    onSearchByMonth: (String) -> Unit, // Callback for searching by month
    onSearchByDateRange: (String, String) -> Unit, // Callback for searching by date range
    showMonthPickerDialog: MutableState<Boolean>, // Control state for month picker dialog
    showDateRangePickerDialog: MutableState<Boolean> // Control state for date range picker dialog
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Row for month selection
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Select Month
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clickable { showMonthPickerDialog.value = true }
            ) {
                OutlinedTextField(
                    value = selectedMonth,
                    onValueChange = {}, // Handle month changes
                    label = { Text("By Month") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { showMonthPickerDialog.value = true }) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Month Picker"
                            )
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.width(5.dp))

            CustomButton(
                onClick = {
                    if (selectedMonth.isNotEmpty()) {
                        onSearchByMonth(selectedMonth)
                    }
                },
                text = "Search"
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Row for date range selection
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Select Date Range
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clickable { showDateRangePickerDialog.value = true }
            ) {
                OutlinedTextField(
                    value = "$fromDate to $toDate",
                    onValueChange = { /* This field is read-only, handled separately */ },
                    label = { Text("By Date Range") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { showDateRangePickerDialog.value = true }) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Date Range Picker"
                            )
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.width(5.dp))

            CustomButton(
                onClick = {
                    if (fromDate.isNotEmpty() && toDate.isNotEmpty()) {
                        onSearchByDateRange(fromDate, toDate)
                    }
                },
                text = "Search"
            )
        }
    }
}



@Composable
fun AdvanceHistoryScreen(
    employeeViewModel1: EmployeeViewModel1 = viewModel(),
    loginViewModel: LoginViewModel,
) {
    val advances by employeeViewModel1.advanceMoney.collectAsState()
    val showMonthPickerDialog = remember { mutableStateOf(false) }
    val showDateRangePickerDialog = remember { mutableStateOf(false) }
    val selectedMonth by employeeViewModel1.selectedAdvanceMonth.collectAsState()
    val fromDate by employeeViewModel1.selectedFromDateAdvance.collectAsState()
    val toDate by employeeViewModel1.selectedToDateAdvance.collectAsState()
    val adminPhoneNumber by loginViewModel.firmOwnerNumber.collectAsState()
    val loader by employeeViewModel1.circularBarState.collectAsState()

    LaunchedEffect(Unit) {
        employeeViewModel1.getAdvance(
            adminPhoneNumber = adminPhoneNumber, // Replace with actual admin phone number
            onSuccess = { /* Handle success */ },
            onFailure = { /* Handle failure */ },
            selectedMonth = selectedMonth,
            from = "",
            to = ""
        )
    }
    if(loader){

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

    }else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column {
                // DatePicker
                DatePicker(
                    selectedMonth = selectedMonth,
                    fromDate = fromDate,
                    toDate = toDate,
                    onSearchByMonth = { month ->
                        employeeViewModel1.getAdvance(
                            adminPhoneNumber = adminPhoneNumber, // Replace with actual admin phone number
                            onSuccess = { /* Handle success */ },
                            onFailure = { /* Handle failure */ },
                            selectedMonth = month,
                            from = "",
                            to = ""
                        )
                    },
                    onSearchByDateRange = { from, to ->
                        employeeViewModel1.getAdvance(
                            adminPhoneNumber = adminPhoneNumber, // Replace with actual admin phone number
                            onSuccess = { /* Handle success */ },
                            onFailure = { /* Handle failure */ },
                            selectedMonth = "",
                            from = from,
                            to = to
                        )
                    },
                    showMonthPickerDialog = showMonthPickerDialog,
                    showDateRangePickerDialog = showDateRangePickerDialog
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (advances.isEmpty()) {
                    NoDataFound(message = "No Data Found")
                } else{
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(advances) { advance ->
                            AdvanceCard(advance)
                        }
                    }
                }
            }
            if (showMonthPickerDialog.value) {
                showMonthPicker(
                    onMonthSelected = { selected ->
                        employeeViewModel1.onSelectedAdvanceMonthChange(selected)
                        showMonthPickerDialog.value = false
                    },
                    onDismissRequest = { showMonthPickerDialog.value = false }
                )
            }

            // Date Range Picker Dialog
            if (showDateRangePickerDialog.value) {
                showDateRangePicker(
                    onRangeSelected = { from, to ->
                        employeeViewModel1.onSelectedFromDateAdvanceChange(to)
                        employeeViewModel1.onSelectedToDateAdvanceChange(from)
                        showDateRangePickerDialog.value = false
                    },
                    onDismissRequest = { showDateRangePickerDialog.value = false }
                )
            }
        }
    }
}

@Composable
fun AdvanceCard(
    advance: AdvanceMoneyData
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Row for Date and Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${advance.date}",
                    style = MaterialTheme.typography.bodySmall
                )
                val statusText = when (advance.status) {
                    0 -> "Not Approved Yet"
                    1 -> "Approved"
                    else -> "Rejected"
                }
                val statusColor = when (advance.status) {
                    0 -> Color.Gray // Not Approved Yet
                    1 -> Color.Green // Approved
                    else -> Color.Red // Rejected

                }
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    color = statusColor
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Amount displayed prominently
            Text(
                text = "Amount: ₹${advance.amount}",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Reason
            Text(
                text = "Reason:",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            )
            Text(
                text = advance.reason.toString(),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp
                ),
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
fun Previeww19191() {
    FirmManagementTheme {
        AdvanceMoneyRequestScreen(loginViewModel = LoginViewModel(), onViewAdvanceHistoryClick = {})
    }
}