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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import com.rach.firmmanagement.employee.showMonthPickerDialog
import com.rach.firmmanagement.ui.theme.blueAcha
import com.rach.firmmanagement.ui.theme.fontBablooBold
import com.rach.firmmanagement.viewModel.AdminViewModel
import com.rach.firmmanagement.viewModel.AllEmployeeViewModel
import com.rach.firmmanagement.viewModel.LoginViewModel
import java.text.SimpleDateFormat
import java.util.*

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
                colors = TextFieldDefaults.outlinedTextFieldColors(),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )

            // Month Picker Field
            OutlinedTextField(
                value = selectedMonth,
                onValueChange = { },
                label = { Text("Month") },
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.outlinedTextFieldColors(),
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