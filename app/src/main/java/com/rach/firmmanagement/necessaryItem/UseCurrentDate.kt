package com.rach.firmmanagement.necessaryItem

import androidx.compose.runtime.Composable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun useCurrentDate(): String {
    val dateFormat = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
    return dateFormat.format(Date())
}