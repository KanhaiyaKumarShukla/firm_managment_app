package com.rach.firmmanagement.navigation

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rach.firmmanagement.HomeScreen.AdminPanelScreen
import com.rach.firmmanagement.HomeScreen.EmployeeProfileEditableScreen
import com.rach.firmmanagement.HomeScreen.ScreensManage
import com.rach.firmmanagement.dataClassImp.MessageDataClass
import com.rach.firmmanagement.dataClassImp.ViewAllEmployeeDataClass
import com.rach.firmmanagement.employee.ChatScreen
import com.rach.firmmanagement.firmAdminOwner.AddGeofence
import com.rach.firmmanagement.firmAdminOwner.AddGeofenceByMap
import com.rach.firmmanagement.firmAdminOwner.AddStaff
import com.rach.firmmanagement.firmAdminOwner.AddTask
import com.rach.firmmanagement.firmAdminOwner.AddWorkHoursScreen
import com.rach.firmmanagement.firmAdminOwner.AdminChatScreen
import com.rach.firmmanagement.firmAdminOwner.AdminMessageScreen
import com.rach.firmmanagement.firmAdminOwner.AllEmployeeAttendance
import com.rach.firmmanagement.firmAdminOwner.EmployeeAttendance
import com.rach.firmmanagement.firmAdminOwner.EmployeeSalaryScreen
import com.rach.firmmanagement.firmAdminOwner.Expense
import com.rach.firmmanagement.firmAdminOwner.GetWorkHours
import com.rach.firmmanagement.firmAdminOwner.HolidayAddScreen
import com.rach.firmmanagement.firmAdminOwner.HolidayTabMenu
import com.rach.firmmanagement.firmAdminOwner.ScreenAdmin
import com.rach.firmmanagement.firmAdminOwner.ViewAllEmployee
import com.rach.firmmanagement.firmAdminOwner.ViewAllEmployeeExpense
import com.rach.firmmanagement.firmAdminOwner.ViewAllTask
import com.rach.firmmanagement.firmAdminOwner.ViewEmployeeExpense
import com.rach.firmmanagement.firmAdminOwner.ViewOneEmployeeTask
import com.rach.firmmanagement.notification.MyNotification
import com.rach.firmmanagement.repository.HolidayRepository
import com.rach.firmmanagement.repository.HolidayViewModelFactory
import com.rach.firmmanagement.viewModel.AddWorkHourViewModel
import com.rach.firmmanagement.viewModel.AdminViewModel
import com.rach.firmmanagement.viewModel.AllEmployeeViewModel
import com.rach.firmmanagement.viewModel.HolidayViewModel
import com.rach.firmmanagement.viewModel.LoginViewModel
import kotlinx.coroutines.launch

@SuppressLint("StateFlowValueCalledInComposition")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation2(){

    val navController = rememberNavController()
    val emViewModel : AllEmployeeViewModel = viewModel()
    val adminViewModel:AdminViewModel = viewModel()
    val workHoursViewModel: AddWorkHourViewModel= viewModel()
    val loginViewModel:LoginViewModel = viewModel()

    NavHost(navController = navController, startDestination = ScreenAdmin.AdminPanel.route) {

        composable(ScreenAdmin.AdminPanel.route){
           AdminPanelScreen(
               navigateToAddStaff = { navController.navigate(ScreenAdmin.AddEmployee.route)},
               navigateToHoliday = { navController.navigate(ScreenAdmin.AddHoliday.route) },
               navigateToWorkingHours = { navController.navigate(ScreenAdmin.AddWorkHours.route) },
               navigateToViewEmpl = { navController.navigate(ScreenAdmin.ViewEmployee.route) },
               navigateToViewAllTask = { navController.navigate(ScreenAdmin.ViewAllTask.route) },
               navigateToTask = {navController.navigate(ScreenAdmin.AddTask.route)},
               navigateToEmployeeAttendance={navController.navigate(ScreenAdmin.AllEmployeeAttendance.route)},
               navigateToAllExpense={navController.navigate(ScreenAdmin.AllExpenses.route)},
               navigateToAddGeofence={navController.navigate(ScreenAdmin.AddGeofence.route)},
               navigateToChatScreen={navController.navigate(ScreenAdmin.AdminChatScreen.route)}
           )
        }

        composable(ScreenAdmin.AddEmployee.route){
            AddStaff(navController=navController)
        }

        composable(ScreenAdmin.AddHoliday.route) {
            val repository = HolidayRepository()
            val viewModel: HolidayViewModel = viewModel(
                factory = HolidayViewModelFactory(repository)
            )
            HolidayAddScreen(holidayViewModel = viewModel)
        }

        composable(ScreenAdmin.AddWorkHours.route) {
            AddWorkHoursScreen()
        }

        composable(ScreenAdmin.ViewEmployee.route){
            ViewAllEmployee(navController = navController)

        }

        composable(ScreenAdmin.AddTask.route){
            AddTask()
        }

        composable(ScreenAdmin.ViewAllTask.route){
            ViewAllTask()
        }
        composable(ScreenAdmin.AllEmployeeAttendance.route){
            AllEmployeeAttendance()
        }

        composable(ScreenAdmin.AllExpenses.route){
            ViewAllEmployeeExpense(adminViewModel=adminViewModel)
        }

        composable(ScreenAdmin.AddGeofence.route){
            AddGeofence(navigateToAddGeofenceByMap = {navController.navigate(ScreenAdmin.AddGeofenceByMap.route)})
        }

        composable(ScreenAdmin.AddGeofenceByMap.route){
            val activity = LocalContext.current as? ComponentActivity
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (activity != null) {
                    AddGeofenceByMap(activity = activity)
                } else {
                    Text("Error: Unable to retrieve activity context.")
                }
            } else {
                Text("This feature is not supported on your device.")
            }
        }

        composable(
            ScreenAdmin.HolidayTabMenu.route + "/{selectedEmployee}",
            arguments = listOf(navArgument("selectedEmployee") { type = NavType.StringType })
        ) { backStackEntry ->
            val selectedEmployeeJson = backStackEntry.arguments?.getString("selectedEmployee")
            Log.d("JSON_DEBUG", selectedEmployeeJson ?: "Null JSON")

            val selectedEmployee: List<ViewAllEmployeeDataClass> = Gson().fromJson(
                selectedEmployeeJson,
                object : TypeToken<List<ViewAllEmployeeDataClass>>() {}.type
            )
            Log.d("JSON_DEBUG", selectedEmployee.toString())
            val repository = HolidayRepository()
            val viewModel: HolidayViewModel = viewModel(
                factory = HolidayViewModelFactory(repository)
            )
            var selectedTab by remember { mutableStateOf("Regular") }
            HolidayTabMenu(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab=it },
                selectedEmployees = selectedEmployee.toSet(),
                holidayViewModel = viewModel
            )
        }

        composable(
            ScreenAdmin.WorkHours.route + "/{selectedEmployee}",
            arguments = listOf(navArgument("selectedEmployee") { type = NavType.StringType })
        ) { backStackEntry ->
            val selectedEmployeeJson = backStackEntry.arguments?.getString("selectedEmployee")
            Log.d("JSON_DEBUG", selectedEmployeeJson ?: "Null JSON")

            val selectedEmployee: List<ViewAllEmployeeDataClass> = Gson().fromJson(
                selectedEmployeeJson,
                object : TypeToken<List<ViewAllEmployeeDataClass>>() {}.type
            )
            Log.d("JSON_DEBUG", selectedEmployee.toString())

            // Handle date, start time, and end time
            val date = remember { mutableStateOf("") }
            val startTime = remember { mutableStateOf("") }
            val endTime = remember { mutableStateOf("") }
            val context = LocalContext.current
            val scope = rememberCoroutineScope()
            GetWorkHours(
                date = date.value,
                onDateChange = { date.value = it },
                startTime = startTime.value,
                onStartTimeChange = { startTime.value = it },
                endTime = endTime.value,
                onEndTimeChange = { endTime.value = it },
                onSaveClick = {
                    // Logic to handle save
                    Log.d("Work Hours", "Saving work hours for: $selectedEmployee")
                    scope.launch {
                        workHoursViewModel.onChangeIsLoading(true)
                        workHoursViewModel.addWorkHoursForEmployees(
                            selectedEmployee.toSet(),
                            onSuccess = {
                                Toast.makeText(
                                    context,
                                    "Working Hour Added",
                                    Toast.LENGTH_LONG
                                ).show()
                                workHoursViewModel.onChangeIsLoading(false)
                                val notification = MyNotification(
                                    context = context,
                                    title = "Firm Management App",
                                    message = "Working Hour Added"
                                )
                                notification.fireNotification()
                            },
                            onFailure = {
                                Toast.makeText(
                                    context,
                                    "Something Went Wrong",
                                    Toast.LENGTH_LONG
                                ).show()
                                workHoursViewModel.onChangeIsLoading(false)
                                val notification = MyNotification(
                                    context = context,
                                    title = "Firm Management App",
                                    message = "Working Hour Addition Failed. Please Try Again."
                                )
                                notification.fireNotification()
                            }
                        )
                    }

                }
            )

        }
        composable(
            "${ScreenAdmin.EmployeeAttendance.route}/{selectedEmployee}",
            arguments = listOf(navArgument("selectedEmployee") { type = NavType.StringType })
        ) { backStackEntry ->
            val selectedEmployeeJson = backStackEntry.arguments?.getString("selectedEmployee")
            val selectedEmployee = Gson().fromJson<ViewAllEmployeeDataClass>(
                selectedEmployeeJson,
                ViewAllEmployeeDataClass::class.java
            )
            EmployeeAttendance(
                selectedEmployees = setOf(selectedEmployee),
                attendanceData = adminViewModel.attendance.collectAsState().value,
                fromDate = adminViewModel.fromDate.collectAsState().value,
                toDate = adminViewModel.toDate.collectAsState().value,
                selectedMonth = adminViewModel.selectedMonth.collectAsState().value,
                onFetchAttendance = { selectedEmployees, month, from, to ->
                    adminViewModel.fetchAttendance(selectedEmployees = selectedEmployees.toList(), selectedMonth = month, from = from, to = to)
                },
                onMonthChange = { adminViewModel.onChangeSelectedMonth(it) },
                attendanceLoading = adminViewModel.loading.collectAsState().value,
                onDateRangeChange = { from, to ->
                    adminViewModel.onChangeAttendanceFromDate(from)
                    adminViewModel.onChangeAttendanceToDate(to)
                },
                toShowOneEmployee = true
            )
        }
        composable(
            "${ScreenAdmin.ViewOneEmployeeTask.route}/{selectedEmployee}",
            arguments = listOf(navArgument("selectedEmployee") { type = NavType.StringType })
        ){
            backStackEntry ->
            val selectedEmployeeJson = backStackEntry.arguments?.getString("selectedEmployee")
            val selectedEmployee = Gson().fromJson<ViewAllEmployeeDataClass>(
                selectedEmployeeJson,
                ViewAllEmployeeDataClass::class.java
            )
            ViewOneEmployeeTask(
                employee = selectedEmployee
            )
        }

        composable(
            "${ScreenAdmin.EmployeeProfile.route}/{selectedEmployee}",
            arguments = listOf(navArgument("selectedEmployee") { type = NavType.StringType })
        ) { backStackEntry ->
            val selectedEmployeeJson = backStackEntry.arguments?.getString("selectedEmployee")
            val selectedEmployee = Gson().fromJson(
                selectedEmployeeJson,
                ViewAllEmployeeDataClass::class.java
            )
            EmployeeProfileEditableScreen(
                employee = selectedEmployee
            )
        }

        composable(
            "${ScreenAdmin.EmployeeExpense.route}/{selectedEmployee}",
            arguments = listOf(navArgument("selectedEmployee") { type = NavType.StringType })
        ){backStackEntry ->
            val selectedEmployeeJson = backStackEntry.arguments?.getString("selectedEmployee")
            val selectedEmployee = Gson().fromJson<ViewAllEmployeeDataClass>(
                selectedEmployeeJson,
                ViewAllEmployeeDataClass::class.java
            )
            ViewEmployeeExpense(
                adminViewModel = adminViewModel,
                selectedEmployee = setOf(selectedEmployee),
                fromDate = adminViewModel.fromDate.collectAsState().value,
                toDate = adminViewModel.toDate.collectAsState().value,
                selectedMonth = adminViewModel.selectedMonth.collectAsState().value,
                expenseLoading = adminViewModel.loading.collectAsState().value,
                onMonthChange = { adminViewModel.onChangeSelectedMonth(it) },
                onDateRangeChange = { from, to ->
                    adminViewModel.onChangeAttendanceFromDate(from)
                    adminViewModel.onChangeAttendanceToDate(to)
                },
                onFetchExpense = { selectedEmployees, month, from, to ->
                    adminViewModel.getEmployeeExpense(employee = listOf(selectedEmployee), selectedMonth = month, from = from, to = to)
                }

            )

        }

        composable(ScreenAdmin.AdminChatScreen.route){
            AdminChatScreen()
        }
        composable(
            "${ScreenAdmin.AdminMessage.route}/{selectedEmployees}",
            arguments = listOf(navArgument("selectedEmployees") { type = NavType.StringType })
        ) { backStackEntry ->
            val selectedEmployeesJson = backStackEntry.arguments?.getString("selectedEmployees")
            val selectedEmployees = Gson().fromJson(selectedEmployeesJson, Array<ViewAllEmployeeDataClass>::class.java).toSet()
            Log.d("Chat", selectedEmployees.toString())
            val inputMessage by adminViewModel.inputMessage.collectAsState()
            val messages by adminViewModel.messages.collectAsState()
            // Fetch messages when the screen loads
            val lifecycleOwner = LocalLifecycleOwner.current
            DisposableEffect(Unit) {
                adminViewModel.fetchMessages(employeeNumber = selectedEmployees.first().phoneNumber.toString()) // Use actual admin phone number

                onDispose {
                    adminViewModel.stopListeningForMessages()
                }
            }
            AdminMessageScreen(
                messages = messages, // Replace with actual messages
                employees = selectedEmployees.toList(),
                selectedEmployees = remember { mutableStateOf(selectedEmployees) },
                inputMessage = inputMessage,
                employeeNumber = adminViewModel.adminPhoneNumber, // Replace with actual admin phone number
                onMessageChange = {
                    adminViewModel.onChangeMessage(it)
                    Log.d("Chat", "change: $it")
                },
                onSendMessage = {
                    if (inputMessage.isNotEmpty() && selectedEmployees.isNotEmpty()) {
                        adminViewModel.sendMessage(
                            selectedEmployees = selectedEmployees,
                            message = MessageDataClass(
                                senderName = adminViewModel.adminPhoneNumber,
                                receiverName = selectedEmployees.first().phoneNumber.toString(),
                                message = inputMessage,
                                timestamp = System.currentTimeMillis()
                            )
                        )
                        adminViewModel.onChangeMessage("")
                    }
                }
            )
        }
        composable(ScreenAdmin.EmployeeChat.route){
            ChatScreen(loginViewModel = loginViewModel)
        }
        composable(
            "${ScreenAdmin.EmployeeSalary.route}/{selectedEmployeeJson}",
            arguments = listOf(navArgument("selectedEmployeeJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val selectedEmployeeJson = backStackEntry.arguments?.getString("selectedEmployeeJson")
            val selectedEmployee = Gson().fromJson(selectedEmployeeJson, ViewAllEmployeeDataClass::class.java)

            val repository = HolidayRepository()
            val viewModel: HolidayViewModel = viewModel(
                factory = HolidayViewModelFactory(repository)
            )
            if (selectedEmployee != null) {
                EmployeeSalaryScreen(
                    employee = selectedEmployee,
                    holidayViewModel = viewModel
                )
            } else {
                // Handle error if employee data is null
                Text("Error: Employee data not found")
            }
        }


    }

}