package com.rach.firmmanagement.firmAdminOwner

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rach.firmmanagement.viewModel.AdminViewModel
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rach.firmmanagement.dataClassImp.AddStaffDataClass
import com.rach.firmmanagement.dataClassImp.AddWorkingHourDataClass
import com.rach.firmmanagement.dataClassImp.EmployeeLeaveData
import com.rach.firmmanagement.dataClassImp.Festival
import com.rach.firmmanagement.dataClassImp.RegularHolidayItems
import com.rach.firmmanagement.dataClassImp.ViewAllEmployeeDataClass
import com.rach.firmmanagement.necessaryItem.CustomOutlinedTextFiled
import com.rach.firmmanagement.ui.theme.blueAcha
import com.rach.firmmanagement.viewModel.AddWorkHourViewModel
import com.rach.firmmanagement.viewModel.HolidayViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.abs

@Composable
fun EmployeeSalaryScreen(
    holidayViewModel: HolidayViewModel,
    employee: AddStaffDataClass,
){
    val showMonthPickerDialog = remember { mutableStateOf(false) }

    val selectedMonth by holidayViewModel.selectedMonth.collectAsState()
    val employeeData by holidayViewModel.employeeData.collectAsState()
    val employeeLoading by holidayViewModel.employeeLoading.collectAsState()
    val ragularHolidays by holidayViewModel.regularHolidays.collectAsState()
    val festival by holidayViewModel.festivalsHolidays.collectAsState()
    val festivalLoading by holidayViewModel.festivalsLoading.collectAsState()
    val regularHolidaysLoading by holidayViewModel.regularHolidaysLoading.collectAsState()
    val employeePhoneNumber = employee.phoneNumber.toString()
    val workingHourData by holidayViewModel.monthlyWorkHours.collectAsState()
    val leaveData by holidayViewModel.leaveData.collectAsState()
    val actualWorkHours by holidayViewModel.workHourData.collectAsState()
    val workHourLoading by holidayViewModel.workHourLoading.collectAsState()
    val leaveLoading by holidayViewModel.leaveLoading.collectAsState()
    val adminPhoneNumber = employee.adminNumber.toString()

    val (month, year) = remember(selectedMonth) {
        val parts = selectedMonth.split(" ")
        if (parts.size >= 2) parts[0] to parts[1] else "January" to "2025"
    }

    LaunchedEffect(Unit) {
        holidayViewModel.fetchEmployeeData(
            employeePhoneNumber = employee.phoneNumber.toString()
        )
        holidayViewModel.getFestivalsForMonthAndYear(
            employeeNumber = employeePhoneNumber,
            year = year,
            month = month,
            adminNumber = adminPhoneNumber
        )
        holidayViewModel.getRegularHolidaysForYear(
            employeeNumber = employeePhoneNumber,
            year = year,
            adminNumber = adminPhoneNumber
        )
        holidayViewModel.fetchMonthlyWorkHours(
            employeePhoneNumber = employeePhoneNumber,
            year = year,
            month = month,
            adminNumber = adminPhoneNumber
        )
        holidayViewModel.fetchLeavesForMonthAndYear(
            employeePhoneNumber = employeePhoneNumber,
            year = year,
            month = month,
            adminNumber = adminPhoneNumber
        )
        holidayViewModel.fetchWorkHourData(
            employeePhoneNumber = employeePhoneNumber,
            month = month,
            year = year,
            adminNumber = adminPhoneNumber
        )
    }

    if (employeeLoading || festivalLoading || regularHolidaysLoading || workHourLoading || leaveLoading) { // Use .value for State objects
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
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            // Date Selection
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
                        onValueChange = {},
                        label = { Text("By Month") },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = { showMonthPickerDialog.value = true }) {
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
                        holidayViewModel.fetchEmployeeData(
                            employeePhoneNumber = employee.phoneNumber.toString()
                        )
                        holidayViewModel.getFestivalsForMonthAndYear(
                            employeeNumber = employeePhoneNumber,
                            year = year,
                            month = month,
                            adminNumber = adminPhoneNumber
                        )
                        holidayViewModel.getRegularHolidaysForYear(
                            employeeNumber = employeePhoneNumber,
                            year = year,
                            adminNumber = adminPhoneNumber
                        )
                        holidayViewModel.fetchMonthlyWorkHours(
                            employeePhoneNumber = employeePhoneNumber,
                            year = year,
                            month = month,
                            adminNumber = adminPhoneNumber
                        )
                        holidayViewModel.fetchLeavesForMonthAndYear(
                            employeePhoneNumber = employeePhoneNumber,
                            year = year,
                            month = month,
                            adminNumber = adminPhoneNumber
                        )
                        holidayViewModel.fetchWorkHourData(
                            employeePhoneNumber = employeePhoneNumber,
                            month = month,
                            year = year,
                            adminNumber = adminPhoneNumber
                        )
                    },
                    text = "Search"
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Log.d("employeeSalary", "data: $employeeData, regularHolidays: $ragularHolidays, festival: $festival, leaveData: $leaveData, workHours: $workingHourData, actualWorkHour: $actualWorkHours")



            DetailedSalaryScreen(
                employeeData=employeeData,
                regularHolidays=ragularHolidays,
                festival=festival,
                leaveData= leaveData,
                month=month,
                year= year,
                workHours = workingHourData,
                actualWorkHour=actualWorkHours
            )


        }
        if (showMonthPickerDialog.value) {
            showMonthPicker(
                onMonthSelected = { selected ->
                    holidayViewModel.setSelectedMonth(selected)
                    showMonthPickerDialog.value = false
                },
                onDismissRequest = { showMonthPickerDialog.value = false }
            )
        }
    }

}

@Composable
fun DetailedSalaryScreen(
    employeeData: AddStaffDataClass?,
    regularHolidays: RegularHolidayItems?,
    festival:List<Festival>,
    leaveData: List<EmployeeLeaveData>,
    workHours: Map<String, Long>,
    actualWorkHour: AddWorkingHourDataClass?,
    month:String,
    year: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val salary =employeeData?.salary?:0
    val actualWorkHours=calculateHours(
        actualWorkHour?.startingTime.orEmpty(),
        actualWorkHour?.endTime.orEmpty()
    )

    val salaryCalculationResult = employeeData?.let {

        CalculateSalary(
            employeeData = it,
            regularHolidays = regularHolidays,
            festival = festival,
            leaves = leaveData,
            workHours = workHours,
            actualWorkHours = actualWorkHours,
            month = month,
            year = year
        )


            //SalaryCalculationResult()
    } ?: run {
        // Provide a default result or throw an error
        SalaryCalculationResult()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Month Selector: $month $year")

        CustomOutlinedTextFiled(
            value = "$salary ${employeeData?.salaryUnit}",
            onValueChange = {},
            label = "Salary",
            singleLine = true,
            isError = false,
            readOnly = true
        )

        CustomOutlinedTextFiled(
            value = salaryCalculationResult.totalDays.toString(),
            onValueChange = {},
            label = "Total Working Days",
            singleLine = true,
            isError = false,
            readOnly = true
        )

        CustomOutlinedTextFiled(
            value = salaryCalculationResult.holidays.toString(),
            onValueChange = {},
            label = "Total Holidays",
            singleLine = true,
            isError = false,
            readOnly = true
        )

        CustomOutlinedTextFiled(
            value = salaryCalculationResult.leaveDays.toString(),
            onValueChange = {},
            label = "Approved Leaves",
            singleLine = true,
            isError = false,
            readOnly = true
        )

        CustomOutlinedTextFiled(
            value = salaryCalculationResult.workingDays.toString(),
            onValueChange = {},
            label = "Full Attendance",
            singleLine = true,
            isError = false,
            readOnly = true
        )

        CustomOutlinedTextFiled(
            value = salaryCalculationResult.calculatedSalary.toString(),
            onValueChange = {},
            label = "Calculated Salary",
            singleLine = true,
            isError = false,
            readOnly = true
        )
    }
}

fun calculateHours(startingTime: String, endTime: String): Float {
    return try {
        // Define the time format
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH)

        // Parse the starting and ending times
        val startDate = timeFormat.parse(startingTime)
        val endDate = timeFormat.parse(endTime)

        // Calculate the difference in milliseconds
        var durationMillis = endDate.time - startDate.time

        // Handle case where endTime is on the next day
        if (durationMillis < 0) {
            durationMillis += 24 * 60 * 60 * 1000 // Add 24 hours in milliseconds
        }

        // Convert milliseconds to hours as a float
        durationMillis / (1000 * 60 * 60).toFloat()
    } catch (e: Exception) {
        0f // Return 0 in case of an error
    }
}



fun CalculateSalary(
    employeeData: AddStaffDataClass?,
    regularHolidays: RegularHolidayItems?,
    festival: List<Festival>,
    leaves: List<EmployeeLeaveData>,
    workHours: Map<String, Long>,
    actualWorkHours: Float,
    month: String,
    year: String
): SalaryCalculationResult {
    val inputDateFormat = SimpleDateFormat("d-M-yyyy", Locale.ENGLISH)
    val calendar = Calendar.getInstance()
    val currentDate = Calendar.getInstance()

    // Set calendar to the provided month and year
    calendar.time = SimpleDateFormat("MMM yyyy", Locale.ENGLISH).parse("$month $year") ?: return SalaryCalculationResult(0, 0, 0, 0f, 0)

    // Determine how many days to traverse
    val totalDays = if (
        calendar.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR) &&
        calendar.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH)
    ) {
        currentDate.get(Calendar.DAY_OF_MONTH) // Traverse up to today
    } else {
        calendar.getActualMaximum(Calendar.DAY_OF_MONTH) // Traverse the full month
    }

    var holidays = 0
    var leaveDays = 0
    var workingDays =0
    val salaryPerDay = if (employeeData?.salaryUnit == "Per Month") {
        employeeData.salary!!.toDouble() / calendar.getActualMaximum(Calendar.DAY_OF_MONTH) // Assuming 30 days in a month
    } else {
        employeeData?.salary!!.toDouble() // If not "Per Month", handle appropriately
    }


    // Traverse each day of the month
    for (day in 1..totalDays) {

        calendar.set(Calendar.DAY_OF_MONTH, day)
        val formattedDate = inputDateFormat.format(calendar.time)
        val currentDayOfWeek = SimpleDateFormat("EEE", Locale.ENGLISH).format(calendar.time)

        // Check for regular holidays
        if (regularHolidays?.weekOff?.contains(currentDayOfWeek) == true ||
            regularHolidays?.monthOff?.contains(day) == true ||
            regularHolidays?.additionalOff?.contains(formattedDate) == true
        ) {
            Log.d("employeeSalary", "holiday: $formattedDate")
            holidays++
            continue
        }

        // Check for festivals
        if (festival.any { it.date == formattedDate && it.selected }) {
            holidays++
            continue
        }

        // Check for leaves
        var isOnLeave = false
        for (leave in leaves) {
            val startDate = leave.startingDate?.let { inputDateFormat.parse(it) }
            val endDate = leave.endDate?.let { inputDateFormat.parse(it) }
            val status = leave.status
            if (startDate != null && endDate != null && status!=null && status == 1 && calendar.time in startDate..endDate) {
                leaveDays++
                isOnLeave = true
                break
            }
        }
        if (isOnLeave) continue

        // Check working hours
        val expectedWorkHours = workHours[day.toString()] ?: continue // Skip if no work hours defined

        val timeVariation = employeeData.timeVariation?.toFloat() ?: 0f
        Log.d("employeeSalary", "working: $expectedWorkHours, $actualWorkHours, $timeVariation, $day")
        if (abs(expectedWorkHours - actualWorkHours) <= timeVariation) {

            workingDays++
        }
    }

    Log.d("employeeSalary", "holidays: $holidays, leaveDays: $leaveDays, workingDays: $workingDays, salaryPerDay: $salaryPerDay")
    val salary= (workingDays + holidays + leaveDays) * salaryPerDay.toFloat()
    return SalaryCalculationResult(holidays, leaveDays, workingDays, salary, totalDays = totalDays)
}

data class SalaryCalculationResult(
    val holidays: Int=0,
    val leaveDays: Int=0,
    val workingDays: Int=0,
    val calculatedSalary: Float=0f,
    val totalDays:Int=0
)

