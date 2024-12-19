package com.rach.firmmanagement.employee

import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.*
import androidx.compose.foundation.verticalScroll
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
import com.rach.firmmanagement.R
import com.rach.firmmanagement.ui.theme.FirmManagementTheme
import com.rach.firmmanagement.ui.theme.blueAcha
import com.rach.firmmanagement.ui.theme.fontBablooBold
import java.util.Calendar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RaiseExpense() {
    // State for money raise input
    var moneyRaise by remember { mutableStateOf("") }

    // State to track the list of items
    var items by remember { mutableStateOf(listOf<Pair<String, String>>()) }

    // State for the remaining amount
    var remaining by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val expanseDate=""

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
            style = androidx.compose.material3.MaterialTheme.typography.headlineSmall.copy(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            ),
        )

        OutlinedTextField(
            value = moneyRaise,
            onValueChange = { moneyRaise = it },
            label = { Text("Enter Money Raise") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(),
            shape = RoundedCornerShape(8.dp),
        )

        OutlinedTextField(
            value = expanseDate,
            onValueChange = { },
            label = { Text("Start Date") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            colors = TextFieldDefaults.outlinedTextFieldColors(),
            shape = RoundedCornerShape(8.dp),
            trailingIcon = {
                IconButton(onClick = {
                    showDatePickerDialog (context){
                        /*
                        employeeViewModel1.onChangeStartingDate(
                            it
                        )

                         */
                    }
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.calendar),
                        contentDescription = "start Date"
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
                name = item.first,
                value = item.second,
                onNameChange = { name ->
                    items = items.toMutableList().apply {
                        this[index] = name to items[index].second
                    }
                },
                onValueChange = { value ->
                    items = items.toMutableList().apply {
                        this[index] = items[index].first to value
                    }
                }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Remaining amount
        Text(
            text = "Remain",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = remaining,
            onValueChange = { remaining = it },
            label = { Text("Enter Remaining Amount") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(),
            shape = RoundedCornerShape(8.dp),
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = {
                // Add a new item
                items = items + ("" to "")
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
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Name") },
            modifier = Modifier.weight(1f),
            colors = TextFieldDefaults.outlinedTextFieldColors(),
            shape = RoundedCornerShape(8.dp),
        )

        Spacer(modifier = Modifier.width(8.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text("Value") },
            modifier = Modifier.weight(1f),
            colors = TextFieldDefaults.outlinedTextFieldColors(),
            shape = RoundedCornerShape(8.dp),
        )
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
        RaiseExpense()
    }
}