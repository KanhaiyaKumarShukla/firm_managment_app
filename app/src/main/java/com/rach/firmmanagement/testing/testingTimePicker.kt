package com.rach.firmmanagement.testing

import android.app.TimePickerDialog
import android.widget.TimePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.rach.firmmanagement.ui.theme.FirmManagementTheme
import java.util.*

@Composable
fun TimePickerOutlinedTextField() {
    var selectedTime by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Current hour and minute from the Calendar instance
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    // TimePickerDialog with 12-hour format (AM/PM)
    val timePickerDialog = TimePickerDialog(
        context,
        { _: TimePicker, selectedHour: Int, selectedMinute: Int ->
            // Formatting to 12-hour AM/PM format
            val amPm = if (selectedHour >= 12) "PM" else "AM"
            val hourFormatted = if (selectedHour % 12 == 0) 12 else selectedHour % 12
            selectedTime = String.format("%02d:%02d %s", hourFormatted, selectedMinute, amPm)
        }, hour, minute, false // false for 12-hour format
    )

    // OutlinedTextField
    OutlinedTextField(
        value = selectedTime,
        onValueChange = { selectedTime = it }, // Allow manual typing
        label = { Text("Select Time") },
        trailingIcon = {
            Icon(
                imageVector = Icons.Filled.DateRange,
                contentDescription = "Time Picker Icon",
                modifier = Modifier.clickable {
                    timePickerDialog.show() // Show TimePickerDialog when icon is clicked
                }
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { /* Allow manual time typing when field is clicked */ }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewTimePickerOutlinedTextField() {
   FirmManagementTheme {
       TimePickerOutlinedTextField()
   }
}
