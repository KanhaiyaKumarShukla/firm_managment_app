package com.rach.firmmanagement.necessaryItem

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.TimePicker

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.rach.firmmanagement.ui.theme.FirmManagementTheme
import com.rach.firmmanagement.viewModel.AllEmployeeViewModel
import java.util.Calendar

@SuppressLint("DefaultLocale")
@Composable
fun TimePickerHaiDialog(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    context: Context,
    read:Boolean
) {

    val calendar = Calendar.getInstance()

    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val min = calendar.get(Calendar.MINUTE)

    val timePickerDialog = TimePickerDialog(
        context,
        { _: TimePicker, selectedHor: Int, selectedMin: Int ->

            val amPm = if (selectedHor >= 12) "PM" else "AM"
            val selectedHour = if (selectedHor % 12 == 0) 12 else selectedHor % 12
            val selectedTime = String.format("%02d:%02d %s", selectedHour, selectedMin, amPm)
            onValueChange(selectedTime)

        },
        hour,
        min,
        false


    )


    OutlinedTextField(value = value, onValueChange = {
        onValueChange(it)
    },
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(text = label)
        },
        trailingIcon = {
            IconButton(onClick = { timePickerDialog.show() }) {

                Icon(imageVector = Icons.Default.DateRange, contentDescription = "Time Picker")

            }
        },
        readOnly = read
    )


}

@SuppressLint("DefaultLocale")
@Composable
fun DatePickerHaiDialog(
    label: String,
    value: String,
    context: Context,
    onValueChange: (String) -> Unit,
    read: Boolean
) {
    val calendar = Calendar.getInstance()

    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val datePickerDialog = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = String.format("%02d-%02d-%04d", selectedDay, selectedMonth + 1, selectedYear)
            onValueChange(selectedDate)
        },
        year,
        month,
        day
    )


    OutlinedTextField(
        value = value,
        onValueChange = {
            onValueChange(it)
        },
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(text = label)
        },
        trailingIcon = {
            IconButton(onClick = { datePickerDialog.show() }) {
                Icon(imageVector = Icons.Default.DateRange, contentDescription = "Date Picker")
            }
        },
        readOnly = read
    )
}

