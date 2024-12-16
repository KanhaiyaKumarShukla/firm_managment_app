package com.rach.firmmanagement.employee

import android.annotation.SuppressLint
import android.text.TextPaint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rach.firmmanagement.ui.theme.FirmManagementTheme
import com.rach.firmmanagement.viewModel.EmlAllTask
import com.rach.firmmanagement.viewModel.LoginViewModel
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import com.rach.firmmanagement.viewModel.AllEmployeeViewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/*
@Composable
fun EmployAttendance(
    emlAllTask: EmlAllTask = viewModel(),
    loginViewModel: LoginViewModel
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val adminPhoneNumber by loginViewModel.firmOwnerNumber.collectAsState()
    val golHai by emlAllTask.gola.collectAsState()

    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = adminPhoneNumber) {
        scope.launch {
            emlAllTask.loadDayWiseByAttendance(
                adminPhoneNumber = adminPhoneNumber,
                onSuccess = {
                    Toast.makeText(context, "Data Loading successful", Toast.LENGTH_LONG).show()
                },
                onFailure = {
                    Toast.makeText(context, "Failed to Load Try Again", Toast.LENGTH_LONG).show()
                }
            )
        }
    }

    val attendenceData by emlAllTask.attendanceDetails.collectAsState()
    val details = attendenceData ?: emptyList()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .horizontalScroll(scrollState)
            .verticalScroll(scrollState)
    ) {
        Canvas(modifier = Modifier.width(1000.dp)) {
            val cellHeight = 100f
            var startY = 0f

            // Calculate column widths with a minimum width fallback
            val nameWidth = details.maxOfOrNull { calculateTextWidth(it.name ?:"") }?.plus(50f) ?: 150f
            val dateWidth = details.maxOfOrNull { calculateTextWidth(it.date ?:"") }?.plus(50f) ?: 150f
            val punchTimeWidth = details.maxOfOrNull { calculateTextWidth(it.punchTime ?: "") }?.plus(50f) ?: 150f
            val punchOutWidth = details.maxOfOrNull { calculateTextWidth(it.punchOutTime ?: "") }?.plus(50f) ?: 150f
            val locationWidth = details.maxOfOrNull { calculateTextWidth(it.locationPunchTime ?: "") }?.plus(50f) ?: 150f

            // Draw headers
            drawCell(0f, startY, nameWidth, cellHeight, "Name")
            drawCell(nameWidth, startY, dateWidth, cellHeight, "Date")
            drawCell(nameWidth + dateWidth, startY, punchTimeWidth, cellHeight, "PunchIn ")
            drawCell(nameWidth + dateWidth + punchTimeWidth, startY, punchOutWidth, cellHeight, "PunchOut")
            drawCell(nameWidth + dateWidth + punchTimeWidth + punchOutWidth, startY, locationWidth, cellHeight, "Location")
            startY += cellHeight

            // Draw details
            if (details.isNotEmpty()) {
                details.forEach {
                    drawCell(0f, startY, nameWidth, cellHeight, it.name ?:"")
                    drawCell(nameWidth, startY, dateWidth, cellHeight, it.date ?:"")
                    drawCell(nameWidth + dateWidth, startY, punchTimeWidth, cellHeight, it.punchTime ?: "")
                    if (it.punchOutTime != null) {
                        drawCell(
                            nameWidth + dateWidth + punchTimeWidth,
                            startY,
                            punchOutWidth,
                            cellHeight,
                            it.punchOutTime
                        )
                    } else {
                        // Draw an empty cell for Punch Out
                        drawCell(
                            nameWidth + dateWidth + punchTimeWidth,
                            startY,
                            punchOutWidth,
                            cellHeight,
                            "" // Draw an empty cell
                        )
                    }
                    drawCell(nameWidth + dateWidth + punchTimeWidth + punchOutWidth, startY, locationWidth, cellHeight, it.locationPunchTime ?: "")
                    startY += cellHeight
                }
            }
        }
    }
}

*/
fun DrawScope.drawCell(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    text: String
) {
    // Draw the cell border
    drawRect(
        color = Color.Black,
        topLeft = Offset(x = x, y = y),
        size = Size(width = width, height = height),
        style = Stroke(
            width = 8f,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )

    // Draw the text inside the cell, vertically centered
    drawContext.canvas.nativeCanvas.drawText(
        text,
        x + 20f,
        y + (height / 2) + 15f, // Adjust for text baseline
        TextPaint().apply {
            isAntiAlias = true
            textSize = 40f
            color = android.graphics.Color.BLACK
        }
    )
}

// Helper function to calculate the width of the text
fun calculateTextWidth(text: String): Float {
    val paint = TextPaint().apply {
        textSize = 40f
        isAntiAlias = true
    }
    return paint.measureText(text)
}

data class AttendanceDetails(
    val name: String,
    val date: String,
    val punchTime: String,
    val punchOutTime: String="-",
    val location: String
)

@Composable
fun PunchInOutAttendance(
    emlAllTask: EmlAllTask = viewModel(),
    loginViewModel: LoginViewModel,
    allEmployeeViewModel: AllEmployeeViewModel
) {

    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val selectedDate by allEmployeeViewModel.selectedDate.collectAsState()
    val (year, month) = extractYearAndMonth(selectedDate)
    val adminPhoneNumber by loginViewModel.firmOwnerNumber.collectAsState()
    val golHai by emlAllTask.gola.collectAsState()
    val selectGenderState by loginViewModel.selectGenderState.collectAsState()

    val scope = rememberCoroutineScope()
    Log.d("TAG", "PunchInOutAttendance: $selectGenderState, $selectedDate, $year, $month")


    LaunchedEffect(key1 = adminPhoneNumber) {
        scope.launch {
            Log.d("TAG", selectGenderState)
            if(selectGenderState=="Employee") {
                emlAllTask.loadDayWiseByAttendance(
                    adminPhoneNumber = adminPhoneNumber,
                    onSuccess = {
                        Toast.makeText(context, "Data Loading successful", Toast.LENGTH_LONG).show()
                    },
                    onFailure = {
                        Toast.makeText(context, "Failed to Load Try Again", Toast.LENGTH_LONG)
                            .show()
                    }
                )
            }else {
                Log.d("TAG", "DATE: $year $month")

                allEmployeeViewModel.getOutForWorkAttendanceForAllEmployees(
                    targetYear = year,
                    targetMonth = month,
                    targetDate = selectedDate,
                    onSuccess = {
                        Toast.makeText(context, "Data Loading successful", Toast.LENGTH_LONG).show()
                    },
                    onFailure = {
                        Toast.makeText(context, "Failed to Load Try Again", Toast.LENGTH_LONG)
                            .show()
                    }
                )
                allEmployeeViewModel.getPunchInOutAttendanceForAllEmployees(
                    targetYear = year,
                    targetMonth = month,
                    targetDate = selectedDate,
                    onSuccess = {
                        Toast.makeText(context, "Data Loading successful", Toast.LENGTH_LONG).show()
                    },
                    onFailure = {
                        Toast.makeText(context, "Failed to Load Try Again", Toast.LENGTH_LONG)
                            .show()
                    }
                )

            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            val headers = if (selectGenderState == "Employee") {
                listOf("Date", "Punch-In", "Punch-Out", "Location")
            } else {
                listOf("Name", "Punch-In", "Punch-Out", "Location")
            }

            headers.forEachIndexed { index, title ->
                Text(
                    text = title,
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        val attendanceData = if (selectGenderState == "Employee") {
            emlAllTask.attendanceDetails.collectAsState()
        } else {
            allEmployeeViewModel.punchInOutAttendanceDetails.collectAsState()
        }

        val details = attendanceData.value ?: emptyList()

        // Content Rows
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(details) { detail ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    if (selectGenderState == "Employee") {
                        Text(
                            text = detail.date ?: "N/A",
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        Text(
                            text = detail.name ?: "N/A",
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Text(
                        text = detail.punchTime?: "N/A",
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = detail.punchOutTime?: "N/A",
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = detail.locationPunchTime?: "N/A",
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }


    }

}

@SuppressLint("DefaultLocale")
@Composable
fun EmployAttendance(
    emlAllTask: EmlAllTask = viewModel(),
    loginViewModel: LoginViewModel,
    employeeViewModel: AllEmployeeViewModel
) {
    /*
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val adminPhoneNumber by loginViewModel.firmOwnerNumber.collectAsState()
    val golHai by emlAllTask.gola.collectAsState()

    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = adminPhoneNumber) {
        scope.launch {
            emlAllTask.loadDayWiseByAttendance(
                adminPhoneNumber = adminPhoneNumber,
                onSuccess = {
                    Toast.makeText(context, "Data Loading successful", Toast.LENGTH_LONG).show()
                },
                onFailure = {
                    Toast.makeText(context, "Failed to Load Try Again", Toast.LENGTH_LONG).show()
                }
            )
        }
    }

     */

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Punch In-Out Attendance", "Out For Work")

    val calendar = Calendar.getInstance()
    val selectedDay = calendar.get(Calendar.DAY_OF_MONTH)
    val selectedMonth = calendar.get(Calendar.MONTH)
    val selectedYear = calendar.get(Calendar.YEAR)
    val today = String.format("%02d-%02d-%04d", selectedDay, selectedMonth + 1, selectedYear)


    Column(modifier = Modifier.fillMaxSize()) {
        // Top Navigation Menu
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(text = title) }
                )
            }
        }
        // Content based on selected tab
        when (selectedTabIndex) {
            0 -> PunchInOutAttendance(emlAllTask, loginViewModel, employeeViewModel)
            1 -> OutForWorkTable(emlAllTask, loginViewModel, employeeViewModel)
        }


    }

}

@Composable
fun OutForWorkTable(
    emlAllTask: EmlAllTask = viewModel(),
    loginViewModel: LoginViewModel,
    allEmployeeViewModel: AllEmployeeViewModel

) {

    val context = LocalContext.current

    val adminPhoneNumber by loginViewModel.firmOwnerNumber.collectAsState()
    val golHai by emlAllTask.gola.collectAsState()
    val selectGenderState by loginViewModel.selectGenderState.collectAsState()
    val scope = rememberCoroutineScope()
    val selectedDate by allEmployeeViewModel.selectedDate.collectAsState()
    Log.d("TAG", "OutForWorkTable: $selectGenderState, $selectedDate")

    LaunchedEffect(key1 = adminPhoneNumber) {
        scope.launch {
            if(selectGenderState=="Employee") {
                emlAllTask.loadOutOfWorkData(
                    adminPhoneNumber = adminPhoneNumber,
                    onSuccess = {
                        Toast.makeText(context, "Data Loading successful", Toast.LENGTH_LONG).show()
                    },
                    onFailure = {
                        Toast.makeText(context, "Failed to Load Try Again", Toast.LENGTH_LONG)
                            .show()
                    }
                )
            }else {
                val (year, month) = extractYearAndMonth(selectedDate)
                allEmployeeViewModel.getOutForWorkAttendanceForAllEmployees(
                    targetYear = year,
                    targetMonth = month,
                    targetDate = selectedDate,
                    onSuccess = {
                        Toast.makeText(context, "Data Loading successful", Toast.LENGTH_LONG).show()
                    },
                    onFailure = {
                        Toast.makeText(context, "Failed to Load Try Again", Toast.LENGTH_LONG)
                            .show()
                    }
                )
                allEmployeeViewModel.getPunchInOutAttendanceForAllEmployees(
                    targetYear = year,
                    targetMonth = month,
                    targetDate = selectedDate,
                    onSuccess = {
                        Toast.makeText(context, "Data Loading successful", Toast.LENGTH_LONG).show()
                    },
                    onFailure = {
                        Toast.makeText(context, "Failed to Load Try Again", Toast.LENGTH_LONG)
                            .show()
                    }
                )
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            val headers = if (selectGenderState == "Employee") {
                listOf("Date", "Duration (in Hours)")
            } else {
                listOf("Name", "Duration (in Hours)")
            }

            headers.forEach { title ->
                Text(
                    text = title,
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }


        val outForWorkData = if (selectGenderState == "Employee") {
            emlAllTask.outOfWorkData.collectAsState()
        } else {
            allEmployeeViewModel.outForWorkAttendanceDetails.collectAsState()
        }

        val details = outForWorkData.value ?: emptyList()

        // Content Rows
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(details) { item ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    if (selectGenderState == "Employee") {
                        Text(
                            text = item.date?: "N/A",
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        Text(
                            text = item.name ?: "N/A",
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Text(
                        text = item.duration.toString(),
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

private fun extractYearAndMonth(date: String): Pair<String, String> {
    return try {
        // Define the input format of the date string
        val inputDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        // Parse the date string into a Date object
        val parsedDate = inputDateFormat.parse(date) ?: throw IllegalArgumentException("Invalid date format")

        // Extract the year and month using Calendar
        val calendar = Calendar.getInstance().apply { time = parsedDate }
        val year = calendar.get(Calendar.YEAR).toString()
        val month = SimpleDateFormat("MMM", Locale.getDefault()).format(calendar.time) // Abbreviated month name

        Pair(year, month)
    } catch (e: ParseException) {
        // Log the error or handle it appropriately
        Log.e("extractYearAndMonth", "Date parsing failed: ${e.message}")
        Pair("Unknown", "Unknown")
    } catch (e: IllegalArgumentException) {
        // Handle invalid input format
        Log.e("extractYearAndMonth", "Invalid date format: ${e.message}")
        Pair("Unknown", "Unknown")
    }
}



@Preview(showBackground = true)
@Composable
fun ImplyAttendPreview() {
    FirmManagementTheme {
        //EmployAttendance(loginViewModel = LoginViewModel())
        EmployAttendance(
            loginViewModel = LoginViewModel(),
            employeeViewModel = AllEmployeeViewModel()
        )
    }
}
