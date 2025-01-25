package com.rach.firmmanagement.firmAdminOwner

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.FloatingActionButtonDefaults
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rach.firmmanagement.R
import com.rach.firmmanagement.dataClassImp.Festival
import com.rach.firmmanagement.dataClassImp.RegularHolidayItems
import com.rach.firmmanagement.dataClassImp.ViewAllEmployeeDataClass
import com.rach.firmmanagement.notification.MyNotification
import com.rach.firmmanagement.repository.HolidayRepository
import com.rach.firmmanagement.repository.HolidayViewModelFactory
import com.rach.firmmanagement.ui.theme.FirmManagementTheme
import com.rach.firmmanagement.ui.theme.blueAcha
import com.rach.firmmanagement.ui.theme.fontBablooBold
import com.rach.firmmanagement.ui.theme.progressBarBgColor
import com.rach.firmmanagement.viewModel.AdminViewModel
import com.rach.firmmanagement.viewModel.AllEmployeeViewModel
import com.rach.firmmanagement.viewModel.HolidayViewModel
import kotlinx.coroutines.launch

import java.util.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HolidayAddScreen1(viewModel: AdminViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {

    val holidayName by viewModel.holidayName.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()

    val progressBarState by viewModel.progressBarState2.collectAsState()

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context, { _,
                   selectedYear,
                   selectedMonth,
                   selectedDay ->
            viewModel.onChangeDate("$selectedDay/${selectedMonth + 1}/$selectedYear")
        },
        year, month, day
    )

    // Use a Box to overlay elements
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Main content (holiday form)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(26.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Add New Holiday",
                style = fontBablooBold,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Holiday Name Field
            OutlinedTextField(
                value = holidayName,
                onValueChange = { viewModel.onChangeHolidayName(it) },
                label = { Text("Holiday Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Date Picker Field
            OutlinedTextField(
                value = selectedDate,
                onValueChange = { viewModel.onChangeDate(it) },
                label = { Text("Holiday Date") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                trailingIcon = {
                    IconButton(onClick = { datePickerDialog.show() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.calendar),
                            contentDescription = "Pick Date"
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Submit Button
            Button(
                onClick = {
                    viewModel.onChangeProgressState2(true)
                    scope.launch {
                        if (holidayName.isEmpty() && selectedDate.isEmpty()) {
                            Toast.makeText(context, "Please Fill All the Fields", Toast.LENGTH_LONG).show()
                            viewModel.onChangeProgressState(false)
                        } else {
                            viewModel.addHoliday(
                                onSuccess = {
                                    Toast.makeText(context, "Holiday Added", Toast.LENGTH_LONG).show()
                                    val notification = MyNotification(
                                        context = context,
                                        title = "Firm Management App",
                                        message = "$holidayName Holiday Added"
                                    )
                                    notification.fireNotification()
                                    viewModel.onChangeProgressState(false)
                                },
                                onFailure = {
                                    viewModel.onChangeProgressState(false)
                                    val notification = MyNotification(
                                        context = context,
                                        title = "Firm Management App",
                                        message = "Failed To Add Holiday"
                                    )
                                    notification.fireNotification()
                                },
                                date = selectedDate
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
            ) {
                Text(text = "Add Holiday", fontSize = 18.sp, color = Color.White)
            }
        }

        // Progress bar overlay, displayed when `progressBarState` is true
        if (progressBarState) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(progressBarBgColor.copy(alpha = 0.5f)), // Semi-transparent background
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}


/*
@SuppressLint("MutableCollectionMutableState")
@Composable
fun HolidayAddScreen(holidayViewModel: HolidayViewModel, viewModel: AllEmployeeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    // State for dropdown expanded
    var expanded by remember { mutableStateOf(false) }

    // State to keep track of selected items
    val selectedEmployees = remember { mutableStateOf(setOf<ViewAllEmployeeDataClass>()) }

    // State for navigation
    var selectedTab by remember { mutableStateOf("Regular") }

    // Employees and Loading States
    val employees = viewModel.employeeList.value
    val isLoading = viewModel.isLoading.value
    val isAllSelected = selectedEmployees.value.size == employees.size - 1
    Column(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Selected Employee Display
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                            ) {
                                append("Selected Employee: ")
                            }
                            append("\n${selectedEmployees.value.joinToString(", ") { it.name.toString() }}")
                        },
                        modifier = Modifier.weight(1f)
                    )

                    // Dropdown for Employee Selection
                    Box {
                        IconButton(onClick = { expanded = !expanded }) {
                            Icon(
                                imageVector = if (expanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowLeft,
                                contentDescription = "Expand"
                            )
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            offset = DpOffset(x = 0.dp, y = 0.dp)
                        ) {
                            employees.forEachIndexed { index, item ->
                                DropdownMenuItem(
                                    onClick = {
                                        selectedEmployees.value = when {
                                            index == 0 -> { // Select All
                                                if (isAllSelected) emptySet() else employees.drop(1)
                                                    .toSet()
                                            }

                                            selectedEmployees.value.contains(item) -> selectedEmployees.value - item
                                            else -> selectedEmployees.value + item
                                        }
                                    }
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Checkbox(
                                            checked = if (index == 0) isAllSelected else selectedEmployees.value.contains(
                                                item
                                            ),
                                            onCheckedChange = null
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(item.name.toString())
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Navigation Menu
            TabRow(
                selectedTabIndex = if (selectedTab == "Regular") 0 else 1,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = selectedTab == "Regular",
                    onClick = { selectedTab = "Regular" }
                ) {
                    Text(
                        "Regular",
                        modifier = Modifier.padding(16.dp),
                        style=TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    )
                }
                Tab(
                    selected = selectedTab == "Public Festival",
                    onClick = { selectedTab = "Public Festival" }
                ) {
                    Text(
                        text = "Public Festival",
                        modifier = Modifier.padding(16.dp),
                        style=TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    )
                }
            }

            // Display the appropriate screen
            Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                when (selectedTab) {
                    "Regular" -> RegularHolidayScreen(
                        onSave = { regularHolidays ->
                            holidayViewModel.saveRegularHolidaysForSelectedEmployees(
                                selectedEmployees.value.toList(),
                                regularHolidays
                            )
                        },
                        repository = HolidayRepository()
                    )
                    "Public Festival" -> FestivalHolidayScreen(
                        onSave = { festivalHolidays ->
                            holidayViewModel.saveFestivalHolidaysForSelectedEmployees(
                                selectedEmployees.value.toList(),
                                festivalHolidays
                            )
                        },
                        repository = HolidayRepository()
                    )
                }
            }
        }
    }
}
*/

@SuppressLint("MutableCollectionMutableState")
@Composable
fun HolidayAddScreen(
    holidayViewModel: HolidayViewModel,
    viewModel: AllEmployeeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    // State to keep track of selected employees
    val selectedEmployees = remember { mutableStateOf(setOf<ViewAllEmployeeDataClass>()) }

    // State for tab selection
    var selectedTab by remember { mutableStateOf("Regular") }

    // Employees and Loading States
    val employees = viewModel.employeeList.value
    val isLoading = viewModel.isLoading.value

    Column(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Employee Selection Section
            EmployeeSelection(
                employees = employees,
                selectedEmployees = selectedEmployees
            )

            // Tab Menu Section
            HolidayTabMenu(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                selectedEmployees = selectedEmployees.value,
                holidayViewModel = holidayViewModel
            )
        }
    }
}

@Composable
fun EmployeeSelection(
    employees: List<ViewAllEmployeeDataClass>,
    selectedEmployees: MutableState<Set<ViewAllEmployeeDataClass>>
) {
    var expanded by remember { mutableStateOf(false) }
    val isAllSelected = selectedEmployees.value.size == employees.size
    val allEmployeesOption = ViewAllEmployeeDataClass(name = "All") // Define "All" option
    val updatedEmployees = listOf(allEmployeesOption) + employees

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    ) {
                        append("Selected Employee: \n")
                    }
                    append(
                        if (isAllSelected) "All"
                        else selectedEmployees.value.joinToString(", ") { it.name.toString() }
                    )
                },
                modifier = Modifier.weight(1f)
            )

            // Dropdown for Employee Selection
            Box {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Expand"
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    offset = DpOffset(x = 0.dp, y = 0.dp)
                ) {
                    updatedEmployees.forEachIndexed { index, item ->
                        DropdownMenuItem(
                            onClick = {
                                if (item == allEmployeesOption) {
                                    // Toggle "All" selection
                                    selectedEmployees.value = if (isAllSelected) emptySet() else employees.toSet()
                                } else {
                                    // Toggle individual selection
                                    selectedEmployees.value = if (selectedEmployees.value.contains(item)) {
                                        selectedEmployees.value - item
                                    } else {
                                        selectedEmployees.value + item
                                    }
                                }
                            }
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = if (item == allEmployeesOption) isAllSelected else selectedEmployees.value.contains(item),
                                    onCheckedChange = null
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(item.name.toString())
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HolidayTabMenu(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    selectedEmployees: Set<ViewAllEmployeeDataClass>,
    holidayViewModel: HolidayViewModel
) {

    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = if (selectedTab == "Regular") 0 else 1,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = selectedTab == "Regular",
                onClick = { onTabSelected("Regular") }
            ) {
                Text(
                    "Regular",
                    modifier = Modifier.padding(16.dp),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                )
            }
            Tab(
                selected = selectedTab == "Public Festival",
                onClick = { onTabSelected("Public Festival") }
            ) {
                Text(
                    text = "Public Festival",
                    modifier = Modifier.padding(16.dp),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            when (selectedTab) {
                "Regular" -> RegularHolidayScreen(
                    onSave = { regularHolidays ->
                        if (selectedEmployees.isEmpty()) {
                            Toast.makeText(
                                context,
                                "Please select at least one employee.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            holidayViewModel.saveRegularHolidaysForSelectedEmployees(
                                selectedEmployees.toList(),
                                regularHolidays,
                                onSuccess = {
                                    Log.d(
                                        "Holiday",
                                        "Successfully added to $selectedEmployees, $regularHolidays"
                                    )
                                    val notification = MyNotification(
                                        context = context,
                                        title = "Firm Management App",
                                        message = "Congratulations! Holiday is Successfully added!"
                                    )
                                    notification.fireNotification()
                                }
                            )
                        }
                    },
                    repository = HolidayRepository()
                )

                "Public Festival" -> FestivalHolidayScreen(
                    onSave = { festivalHolidays ->
                        Log.d("Holiday", "button clicked : $festivalHolidays")
                        if (selectedEmployees.isEmpty()) {
                            Toast.makeText(
                                context,
                                "Please select at least one employee.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            holidayViewModel.saveFestivalHolidaysForSelectedEmployees(
                                selectedEmployees.toList(),
                                festivalHolidays,
                                onSuccess = {
                                    Log.d(
                                        "Holiday",
                                        "successfull add to $selectedEmployees, $festivalHolidays"
                                    )
                                    val notification = MyNotification(
                                        context = context,
                                        title = "Firm Management App",
                                        message = "Congratulations Holiday is Successfully added!"
                                    )

                                    notification.fireNotification()
                                }
                            )
                        }
                    },
                    repository = HolidayRepository()
                )
            }
        }
    }
}



@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnusedMaterialScaffoldPaddingParameter")
@Composable
fun FestivalHolidayScreen(repository : HolidayRepository, onSave: (List<Festival>) -> Unit) {

    val viewModel: HolidayViewModel = viewModel(
        factory = HolidayViewModelFactory(repository)
    )

    val festivals by viewModel.festivals.collectAsState()
    val context = LocalContext.current

    var showDialog by remember { mutableStateOf(false) }
    var festivalName by remember { mutableStateOf("") }
    var festivalDate by remember { mutableStateOf("") }
    var festivalMonth by remember { mutableStateOf("") }
    var festivalYear by remember { mutableStateOf("") }

    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    LaunchedEffect(key1 = year) {
        Log.d("Holiday", year.toString())
        viewModel.fetchFestivals(year.toString()) // Fetch festivals for the current year
    }
    val datePickerDialog = DatePickerDialog(
        context, { _,
                   selectedYear,
                   selectedMonth,
                   selectedDay ->
            festivalDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            festivalYear = selectedYear.toString()
            festivalMonth = (selectedMonth + 1).toString()
        },
        year, month, day
    )
    Log.d("Holiday", "screen: $festivals")
    val scaffoldState = rememberScaffoldState()
    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                backgroundColor = blueAcha,
                elevation = FloatingActionButtonDefaults.elevation(10.dp)
            ) {

                Icon(
                    imageVector = Icons.Default.Add, contentDescription = "Add Holiday",
                    tint = Color.White
                )

            }
        }
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            if (festivals.isEmpty()) {
                if (viewModel.loading.collectAsState().value) {
                    CircularProgressIndicator()
                } else {
                    Text(
                        text = viewModel.error.collectAsState().value ?: "No festivals available \nPlease Add Festival(s).",
                        modifier = Modifier
                            .wrapContentSize() // Constrain the size to the text's content
                            .align(Alignment.CenterHorizontally).padding(top = 30.dp), // Center horizontally
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    )
                }
            } else {

                LazyColumn(modifier = Modifier.weight(1f)) {
                    Log.d("Holiday", "lazy: $festivals")
                    items(festivals.size) { index ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .border(BorderStroke(1.dp, Color(0xFF0378D1)), RoundedCornerShape(8.dp))
                                .padding(bottom = 4.dp)
                        ) {
                            Checkbox(
                                checked = festivals[index].selected,
                                onCheckedChange = { isSelected ->
                                    viewModel.updateFestivalSelection(festivals[index], isSelected)
                                },
                                colors = CheckboxDefaults.colors(checkedColor = blueAcha)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = festivals[index].name,
                                style = TextStyle(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(text = festivals[index].date)

                        }
                    }
                }


                CustomButton(
                    onClick = {
                        // Save festivals
                        onSave(festivals.filter { it.selected })
                    },
                    text = "Save",
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Add Festival") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = festivalName,
                            onValueChange = { festivalName = it },
                            label = { Text("Festival Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = festivalDate,
                            onValueChange = { festivalDate=it },
                            label = { Text("Holiday Date") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            trailingIcon = {
                                IconButton(onClick = { datePickerDialog.show() }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.calendar),
                                        contentDescription = "Pick Date"
                                    )
                                }
                            }
                        )
                    }
                },
                confirmButton = {
                    CustomButton(
                        onClick = {
                            viewModel.addFestival(Festival(festivalName, festivalDate, festivalYear, festivalMonth))
                            showDialog = false
                        },
                        text="Save",
                        modifier = Modifier.width(100.dp)
                    )
                },
                dismissButton = {
                    CustomButton(text = "Cancel",onClick = { showDialog = false }, modifier = Modifier.width(100.dp))
                }
            )
        }
    }
}



@Composable
fun SelectableBox(
    text: String,
    isSelected: Boolean,
    onSelectionChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clickable { onSelectionChanged(!isSelected) }
            .padding(1.dp)
            .background(
                color = if (isSelected) Color(0xFF0378D1) else Color.Transparent, // Fill color on selection
                shape = RoundedCornerShape(8.dp)
            )
            .border(BorderStroke(1.dp, Color(0xFF0378D1)), RoundedCornerShape(8.dp))
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White  else Color.Gray
        )
    }
}

@Composable
fun DaySelection(
    selectedDays: List<String>,
    onSelectionChanged: (List<String>) -> Unit
) {
    val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        daysOfWeek.forEach { day ->
            SelectableBox(
                text = day,
                isSelected = selectedDays.contains(day),
                onSelectionChanged = { isSelected ->
                    val updatedDays = if (isSelected) {
                        selectedDays + day
                    } else {
                        selectedDays - day
                    }
                    onSelectionChanged(updatedDays)
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun DateSelection(
    selectedDates: List<Int>,
    onSelectionChanged: (List<Int>) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier.height(180.dp)
    ) {
        items(31) { index ->
            val date = index + 1
            SelectableBox(
                text = date.toString(),
                isSelected = selectedDates.contains(date),
                onSelectionChanged = { isSelected ->
                    val updatedDates = if (isSelected) {
                        selectedDates + date
                    } else {
                        selectedDates - date
                    }
                    onSelectionChanged(updatedDates)
                }
            )
        }
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun RegularHolidayScreen(repository : HolidayRepository, onSave: (RegularHolidayItems) -> Unit) {

    val viewModel: HolidayViewModel = viewModel(
        factory = HolidayViewModelFactory(repository)
    )

    val additionalHolidays by viewModel.additionalHolidays.collectAsState()

    var selectedDays by remember { mutableStateOf(emptyList<String>()) }
    var selectedDates by remember { mutableStateOf(emptyList<Int>()) }
    var additionalHolidaySelectedDays by remember { mutableStateOf(emptyList<String>()) }
    /*
    var additionalHolidays by remember {
        mutableStateOf(listOf(
            "Last Day of Month",
            "First Day of Month",
            "Last Sunday of Month"
        ))
    }*/

    LaunchedEffect(Unit) {
        viewModel.fetchAdditionalHolidays() // Fetch festivals for the current year
    }

    var showAddHolidayDialog by remember { mutableStateOf(false) }
    val scaffoldState = rememberScaffoldState()
    val scrollState=rememberScrollState()
    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddHolidayDialog = true },
                backgroundColor = blueAcha,
                elevation = FloatingActionButtonDefaults.elevation(10.dp)
            ) {

                Icon(
                    imageVector = Icons.Default.Add, contentDescription = "Add Holiday",
                    tint = Color.White
                )

            }
        }
    ) {innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()) // Use single scrolling container
        ) {

            CustomLabeledBox(
                title = "Selected Days:",
                modifier = Modifier.fillMaxWidth()
            )

            DaySelection(
                selectedDays = selectedDays,
                onSelectionChanged = { selectedDays = it }
            )

            Spacer(modifier = Modifier.height(16.dp))
            // Selected Dates
            CustomLabeledBox(
                title = "Selected Dates:",
                modifier = Modifier.fillMaxWidth()
            )

            DateSelection(
                selectedDates = selectedDates,
                onSelectionChanged = { selectedDates = it }
            )

            Text(
                text = "Additional Holidays:",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0378D1)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (holiday in additionalHolidays) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().border(BorderStroke(1.dp, Color(0xFF0378D1)), RoundedCornerShape(8.dp))
                    ) {
                        Checkbox(
                            checked = additionalHolidaySelectedDays.contains(holiday),
                            onCheckedChange = {
                                if (it) {
                                    additionalHolidaySelectedDays =
                                        additionalHolidaySelectedDays + holiday
                                } else {
                                    additionalHolidaySelectedDays =
                                        additionalHolidaySelectedDays - holiday
                                }
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = holiday, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            CustomButton(
                onClick = {
                    onSave(
                        RegularHolidayItems(
                            weekOff = selectedDays,
                            monthOff = selectedDates,
                            additionalOff = additionalHolidaySelectedDays
                        )
                    )
                },
                text = "Save",
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )


        }

    }
    if (showAddHolidayDialog) {
        AddHolidayDialog(
            onDismissRequest = { showAddHolidayDialog = false },
            onConfirm = {
                if (it.isNotEmpty()) {
                    viewModel.addAdditionalHolidays(it)
                }
            }
        )
    }
}

@Composable
fun CustomLabeledBox(
    title: String,
    titleColor: Color = Color(0xFF0378D1),
    titleFontSize: TextUnit = 15.sp,
    titleFontWeight: FontWeight = FontWeight.Bold,
    borderColor: Color = Color(0xFF0378D1),
    padding: Dp = 8.dp,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(vertical = 8.dp)) {
        Text(
            text = title,
            color = titleColor,
            fontSize = titleFontSize,
            fontWeight = titleFontWeight
        )
    }
}

@Composable
fun AddHolidayDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var newHoliday by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .width(300.dp) // Adjust width as needed
            ) {
                Text("Add Holiday")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = newHoliday,
                    onValueChange = { newHoliday = it },
                    label = { Text("Enter Holiday Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CustomButton(
                        onClick = { onDismissRequest() },
                        text = "Cancel"
                    )

                    Spacer(modifier = Modifier.width(8.dp))
                    CustomButton(
                        onClick = {
                            onConfirm(newHoliday)
                            onDismissRequest()
                        },
                        text = "Add"
                    )

                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HolidaysScreenJetPackCompose() {
    FirmManagementTheme {
        //HolidayAddScreen(viewModel = HolidayViewModel())
    }
}