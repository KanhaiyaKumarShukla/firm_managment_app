package com.rach.firmmanagement.employee

import android.app.DatePickerDialog
import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.TextFieldValue
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rach.firmmanagement.R
import com.rach.firmmanagement.dataClassImp.ExpenseItem
import com.rach.firmmanagement.notification.MyNotification
import com.rach.firmmanagement.ui.theme.FirmManagementTheme
import com.rach.firmmanagement.ui.theme.blueAcha
import com.rach.firmmanagement.ui.theme.fontBablooBold
import com.rach.firmmanagement.viewModel.EmployeeViewModel1
import com.rach.firmmanagement.viewModel.LoginViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.Calendar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RaiseExpense(
    employeeViewModel: EmployeeViewModel1 = viewModel(),
    loginViewModel: LoginViewModel
) {

    // State for money raise input
    var moneyRaise by remember { mutableStateOf(employeeViewModel.moneyRaise) }

    // State to track the list of items
    var items by remember { mutableStateOf(employeeViewModel.items) }

    // State for the remaining amount
    val remainingMoney by employeeViewModel.remaining.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    var selectedDate by remember { mutableStateOf(employeeViewModel.selectedDate) }
    val adminPhoneNumber by loginViewModel.firmOwnerNumber.collectAsState()
    val scope = rememberCoroutineScope()

    fun updateMoneyRemaining() {
        val raise = moneyRaise.toDoubleOrNull() ?: 0.0
        val totalItemsValue = items.sumOf { it.value.toDoubleOrNull() ?: 0.0 }
        val remaining = raise - totalItemsValue
        employeeViewModel.onRemainingChange(remaining.toString())
        Log.d("Remaining", "updateMoneyRemaining: $remainingMoney")
    }
    LaunchedEffect(moneyRaise, items) {
        updateMoneyRemaining()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Center,
    ) {
        // Money Raise
        Text(
            text = "Money Raise",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            ),
        )

        OutlinedTextField(
            value = moneyRaise,
            onValueChange = {
                moneyRaise = it
                employeeViewModel.onMoneyRaiseChange(it)
            },
            label = { Text("Enter Money Raise") },
            modifier = Modifier.fillMaxWidth(),

            shape = RoundedCornerShape(8.dp),
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = selectedDate,
            onValueChange = { },
            label = { Text("Date") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(8.dp),
            trailingIcon = {
                IconButton(onClick = {
                    showDatePickerDialog (context){date ->

                        selectedDate = date
                        employeeViewModel.onDateChange(date)
                    }
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.calendar),
                        contentDescription = "Date"
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Items
        Text(
            text = "Items:",
            style = androidx.compose.material3.MaterialTheme.typography.headlineSmall.copy(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Display existing items
        items.forEachIndexed { index, item ->
            Item(
                name = item.name,
                value = item.value,
                onNameChange = { name ->
                    items = items.toMutableList().apply {
                        this[index] = ExpenseItem(name = name, value = items[index].value)
                    }
                    employeeViewModel.onItemsChange(items)
                },
                onValueChange = { value ->
                    items = items.toMutableList().apply {
                        this[index] = ExpenseItem(name = items[index].name, value = value)
                    }
                    employeeViewModel.onItemsChange(items)
                },
                onDeleteItem = { // Add deletion logic here
                    items = items.toMutableList().apply {
                        removeAt(index) // Remove the item at the current index
                    }
                    employeeViewModel.onItemsChange(items)
                }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Remaining amount
        Text(
            text = "Remaining",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = remainingMoney,
            onValueChange = {
            },
            label = { Text("Remaining Amount") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            readOnly = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = {
                // Add a new item
                items = items + ExpenseItem(name = "", value = "")
                employeeViewModel.onItemsChange(items)
                },
                modifier = Modifier.width(120.dp),
                colors = ButtonDefaults.buttonColors(
                    blueAcha
                )
            ) {

                Text(
                    text = "Add Item",
                    color = Color.White,
                    style = fontBablooBold
                )

            }

            Button(onClick = {
                // Handle save logic
                // You can perform actions like saving to database or showing a summary
                scope.launch {
                    employeeViewModel.raiseExpense(
                        adminPhoneNumber = adminPhoneNumber,
                        onSuccess = {
                            val notification = MyNotification(context,
                                title = "Firm Management App",
                                message = "Request Added")

                            notification.fireNotification()
                        },
                        onFailure = {
                            val notification = MyNotification(context,
                                title = "Firm Management App",
                                message = "Request Added Failed")

                            notification.fireNotification()
                        }
                    )
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
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Item(
    name: String,
    value: String,
    onNameChange: (String) -> Unit,
    onValueChange: (String) -> Unit,
    onDeleteItem: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Name") },
            modifier = Modifier.weight(1f),

            shape = RoundedCornerShape(8.dp),
        )

        Spacer(modifier = Modifier.width(8.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text("Value") },
            modifier = Modifier.weight(1f),

            shape = RoundedCornerShape(8.dp),
        )
        IconButton( // Use IconButton for the delete icon
            onClick = onDeleteItem,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close, // Replace with your clear icon resource
                contentDescription = "Delete Item",
            )
        }
    }
}

fun showDatePickerDialog(context:Context, onDateSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()
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
@Preview(showBackground = true)
@Composable
fun ExpensePreview(){
    FirmManagementTheme {
        RaiseExpense(loginViewModel = LoginViewModel())
    }
}