package com.rach.firmmanagement.employee

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rach.firmmanagement.dataClassImp.Expense
import com.rach.firmmanagement.dataClassImp.ExpenseItem
import com.rach.firmmanagement.viewModel.EmployeeViewModel1
import com.rach.firmmanagement.viewModel.LoginViewModel
import com.rach.firmmanagement.R
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllExpense(
    employeeViewModel: EmployeeViewModel1 = viewModel(),
    loginViewModel: LoginViewModel
) {
    val calendar = Calendar.getInstance()
    var selectedDate by remember { mutableStateOf(SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendar.time)) }
    val context = LocalContext.current
    val adminPhoneNumber by loginViewModel.firmOwnerNumber.collectAsState()

    LaunchedEffect(Unit) {
        employeeViewModel.onYearChange(calendar.get(Calendar.YEAR).toString())
        employeeViewModel.onMonthChange(calendar.get(Calendar.MONTH)) // 0-based month
        employeeViewModel.getExpensesForMonth(adminPhoneNumber)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Date Picker using OutlinedTextField
        OutlinedTextField(
            value = selectedDate,
            onValueChange = { },
            label = { Text("Date") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(),
            shape = RoundedCornerShape(8.dp),
            trailingIcon = {
                IconButton(onClick = {
                    showMonthPickerDialog(context) { year, month ->
                        val formattedDate = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
                            .format(calendar.apply { set(year, month, 1) }.time)
                        selectedDate = formattedDate
                        employeeViewModel.onYearChange(year.toString())
                        employeeViewModel.onMonthChange(month) // Month is 0-based
                        employeeViewModel.getExpensesForMonth(adminPhoneNumber)
                    }
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.calendar),
                        contentDescription = "Select Date"
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Loading Indicator or Expenses List
        if (employeeViewModel.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            AllExpenseList(expenses = employeeViewModel.expenses)
        }
    }
}


fun showMonthPickerDialog(context: Context, onDateSelected: (Int, Int) -> Unit) {
    val calendar = Calendar.getInstance()
    DatePickerDialog(
        context,
        { _, year, month, _ ->
            onDateSelected(year, month)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
}


@Composable
fun ExpenseCard(expense: Expense) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFB2EBF2),
                        Color(0xFFE0F7FA)
                    )
                ),
                shape = MaterialTheme.shapes.medium
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Employee number and Date (small size in one row)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Employee: ${expense.employeeNumber}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light,
                    color = Color.Gray
                )
                Text(
                    text = expense.selectedDate,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Money Raised (bold and large)
            val raisedValue = expense.moneyRaise.replace(Regex("[^0-9.]"), "")
            Text(
                text = "Money Raised: ₹ $raisedValue",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            // List of Items
            Text(
                text = "Items:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            expense.items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = item.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "₹${item.value}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Remaining Money
            val numericValue = expense.remaining.replace(Regex("[^0-9.]"), "")
            Text(
                text = "Remaining Money: ₹$numericValue",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

@Composable
fun AllExpenseList(expenses: List<Expense>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(expenses) { expense ->
            ExpenseCard(expense = expense)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewExpenseList() {
    val sampleExpenses = listOf(
        Expense(
            employeeNumber = "EMP001",
            selectedDate = "2024-12-19",
            moneyRaise = "$500",
            items = listOf(ExpenseItem("Laptop", "346"), ExpenseItem("Stationery", "9847"), ExpenseItem("Snacks","897")),
            remaining = "$50"
        ),
        Expense(
            employeeNumber = "EMP001",
            selectedDate = "2024-12-19",
            moneyRaise = "$500",
            items = listOf(ExpenseItem("Laptop", "346"), ExpenseItem("Stationery", "9847"), ExpenseItem("Snacks","897")),
            remaining = "$50"
        )
    )
    AllExpenseList(expenses = sampleExpenses)
}

