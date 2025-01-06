package com.rach.firmmanagement.firmAdminOwner

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.rach.firmmanagement.dataClassImp.Expense
import com.rach.firmmanagement.dataClassImp.ExpenseItem
import com.rach.firmmanagement.employee.AllExpenseList
import android.app.DatePickerDialog
import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rach.firmmanagement.R
import com.rach.firmmanagement.dataClassImp.ViewAllEmployeeDataClass
import com.rach.firmmanagement.employee.ExpenseCard
import com.rach.firmmanagement.employee.showMonthPickerDialog
import com.rach.firmmanagement.ui.theme.blueAcha
import com.rach.firmmanagement.ui.theme.fontBablooBold
import com.rach.firmmanagement.viewModel.AdminViewModel
import com.rach.firmmanagement.viewModel.AllEmployeeViewModel
import com.rach.firmmanagement.viewModel.LoginViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Expense(viewModel: AdminViewModel) {
    val context = LocalContext.current
    var selectedIdentity by remember { mutableStateOf("") }
    val monthYearFormat = SimpleDateFormat("MM/yyyy", Locale.getDefault())
    var selectedMonth by remember {
        mutableStateOf(SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Calendar.getInstance().time))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Identity Selection with Autocomplete
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Identity Field
            OutlinedTextField(
                value = selectedIdentity,
                onValueChange = { selectedIdentity = it },
                label = { Text("Employee Ph.") },
                modifier = Modifier.weight(1f),

                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )

            // Month Picker Field
            OutlinedTextField(
                value = selectedMonth,
                onValueChange = { },
                label = { Text("Month") },
                modifier = Modifier.weight(1f),
                // colors = TextFieldDefaults.outlinedTextFieldColors(),
                shape = RoundedCornerShape(8.dp),
                trailingIcon = {
                    IconButton(onClick = {
                        showMonthPickerDialog(context) { year, month ->
                            selectedMonth = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
                                .format(Calendar.getInstance().apply { set(year, month, 1) }.time)
                        }
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.calendar),
                            contentDescription = "Select Month"
                        )
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search Button
        Button(
            onClick = {
                // Perform search
                val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
                val calendar = Calendar.getInstance()
                calendar.time = monthYearFormat.parse(selectedMonth) ?: return@Button

                val month = calendar.get(Calendar.MONTH) // Calendar.MONTH is zero-based
                val year = calendar.get(Calendar.YEAR)

                // Call ViewModel to fetch expenses
                viewModel.getExpensesForMonth(
                    employeeNumber = selectedIdentity,
                    month = onMonthChange(month),
                    year = year.toString()
                )
                Log.d("ExpensesData", "$month, ${onMonthChange(month)}, $year")
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                blueAcha
            )
        ) {
            Text(
                "Search",
                color = Color.White,
                style = fontBablooBold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display sample expenses or other content
        val expenses by viewModel.expenses.collectAsState(emptyList())
        AllExpenseList(expenses = expenses)
    }
}

@Composable
fun ViewEmployeeExpense(
    adminViewModel: AdminViewModel,
    selectedEmployee: Set<ViewAllEmployeeDataClass>,
    modifier: Modifier = Modifier,
    fromDate: String,
    toDate: String,
    selectedMonth: String,
    expenseLoading: Boolean,
    onMonthChange: (String) -> Unit,
    onDateRangeChange: (String, String) -> Unit,
    onFetchExpense: (Set<ViewAllEmployeeDataClass>, String, String, String) -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }

    val employeeExpenses by adminViewModel.employeeExpense.collectAsState(initial = emptyList())
    val showMonthDialog = remember { mutableStateOf(false) }
    val showDateRangePickerDialog = remember { mutableStateOf(false) }
    var selectedExpense by remember { mutableStateOf<Expense?>(null) }

    if (expenseLoading) { // Use .value for State objects
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
        Column(modifier = modifier.padding(16.dp)) {
            // Date Selection
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Select Month
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showMonthDialog.value = true }
                ) {
                    OutlinedTextField(
                        value = selectedMonth,
                        onValueChange = {},
                        label = { Text("By Month") },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = { showMonthDialog.value = true }) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "Date Picker"
                                )
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.width(5.dp))

                CustomButton(
                    onClick = {
                        onFetchExpense(selectedEmployee, selectedMonth, "", "")
                    },
                    text = "Search"
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

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
                        onValueChange = {},
                        label = { Text("By Date Range") },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = { showDateRangePickerDialog.value = true }) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "Date Picker"
                                )
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.width(5.dp))

                CustomButton(
                    onClick = {
                        onFetchExpense(selectedEmployee, "", fromDate, toDate)
                    },
                    text = "Search"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Date",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Text(
                    text = "Status",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(employeeExpenses) { expense ->
                    EmployeeExpense(
                        expense = expense,
                        onClick = {
                            selectedExpense = expense
                            showDialog = true
                        }
                    )
                }
            }


            if (showDialog) {
                selectedExpense?.let { expense ->
                    ExpenseDetailDialog(
                        expense = expense,
                        onDismiss = {
                            showDialog = false
                        }
                    )
                }
            }

            // Month Picker Dialog
            if (showMonthDialog.value) {
                showMonthPicker(
                    onMonthSelected = { selected ->
                        onMonthChange(selected)
                        showMonthDialog.value = false
                    },
                    onDismissRequest = { showMonthDialog.value = false }
                )
            }

            // Date Range Picker Dialog
            if (showDateRangePickerDialog.value) {
                showDateRangePicker(
                    onRangeSelected = { from, to ->
                        onDateRangeChange(from, to)
                        showDateRangePickerDialog.value = false
                    },
                    onDismissRequest = { showDateRangePickerDialog.value = false }
                )
            }
        }
    }
}

@Composable
fun EmployeeExpense(expense: Expense, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = expense.selectedDate, fontSize = 16.sp)
            Text(
                text = if (expense.status) "Yes" else "No",
                color = if (expense.status) Color.Green else Color.Red,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}

@Composable
fun EmployeeExpenseCard(
    expense: Expense,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Row for Date and Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = expense.selectedDate, fontSize = 14.sp)
                Text(
                    text = if (expense.status) "Yes" else "No",
                    color = if (expense.status) Color.Green else Color.Red,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Row for Employee Name and Advance
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = expense.employeeNumber,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = expense.moneyRaise,
                    fontSize = 14.sp,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
    }
}


@Composable
fun ExpenseDetailDialog(expense: Expense, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onDismiss) { Text("Close") }
        },
        text = {
            Column {
                /*
                Text("Expense Details", style = MaterialTheme.typography.h6)

                Spacer(modifier = Modifier.height(8.dp))

                Text("Date: ${expense.selectedDate}")
                Text("Raised Money: ${expense.moneyRaise}")
                Text("Remaining Money: ${expense.remaining}")

                Spacer(modifier = Modifier.height(8.dp))

                Text("Items:")
                expense.items.forEach { item ->
                    Text("- ${item.name}: ${item.value}")
                }

                 */
                ExpenseCard(expense = expense)
            }
        }
    )
}

fun onMonthChange(newMonth: Int):String {
    // Get the month in "MMM" format (0-based month)
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.MONTH, newMonth)  // Set the calendar to the selected month
    return SimpleDateFormat("MMM", Locale.getDefault()).format(calendar.time)  // Format month as "MMM"
}


@Preview(showBackground = true)
@Composable
fun PreviewExpenseList() {
    Expense(viewModel = AdminViewModel())
}