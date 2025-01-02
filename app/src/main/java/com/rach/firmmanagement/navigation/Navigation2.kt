package com.rach.firmmanagement.navigation

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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.Navigation
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rach.firmmanagement.HomeScreen.AdminPanelScreen
import com.rach.firmmanagement.dataClassImp.ViewAllEmployeeDataClass
import com.rach.firmmanagement.firmAdminOwner.AddGeofence
import com.rach.firmmanagement.firmAdminOwner.AddGeofenceByMap
import com.rach.firmmanagement.firmAdminOwner.AddStaff
import com.rach.firmmanagement.firmAdminOwner.AddTask
import com.rach.firmmanagement.firmAdminOwner.AddWorkHoursScreen
import com.rach.firmmanagement.firmAdminOwner.AllEmployeeAttendance
import com.rach.firmmanagement.firmAdminOwner.Expense
import com.rach.firmmanagement.firmAdminOwner.GetWorkHours
import com.rach.firmmanagement.firmAdminOwner.HolidayAddScreen
import com.rach.firmmanagement.firmAdminOwner.HolidayTabMenu
import com.rach.firmmanagement.firmAdminOwner.ScreenAdmin
import com.rach.firmmanagement.firmAdminOwner.ScreenAdmin.AddGeofenceByMap
import com.rach.firmmanagement.firmAdminOwner.ViewAllEmployee
import com.rach.firmmanagement.firmAdminOwner.ViewAllTask
import com.rach.firmmanagement.notification.MyNotification
import com.rach.firmmanagement.repository.HolidayRepository
import com.rach.firmmanagement.repository.HolidayViewModelFactory
import com.rach.firmmanagement.viewModel.AddWorkHourViewModel
import com.rach.firmmanagement.viewModel.AdminViewModel
import com.rach.firmmanagement.viewModel.AllEmployeeViewModel
import com.rach.firmmanagement.viewModel.EmlAllTask
import com.rach.firmmanagement.viewModel.HolidayViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation2(){

    val navController = rememberNavController()
    val emViewModel : AllEmployeeViewModel = viewModel()
    val adminViewModel:AdminViewModel = viewModel()
    val workHoursViewModel: AddWorkHourViewModel= viewModel()

    NavHost(navController = navController, startDestination = ScreenAdmin.AdminPanel.route) {

        composable(ScreenAdmin.AdminPanel.route){
           AdminPanelScreen(
               navigateToAddStaff = { navController.navigate(ScreenAdmin.AddEmployee.route)},
               navigateToHoliday = { navController.navigate(ScreenAdmin.AddHoliday.route) },
               navigateToWorkingHours = { navController.navigate(ScreenAdmin.AddWorkHours.route) },
               navigateToViewEmpl = { navController.navigate(ScreenAdmin.ViewEmployee.route) },
               navigateToViewAllTask = { navController.navigate(ScreenAdmin.ViewAllTask.route) },
               navigateToTask = {navController.navigate(ScreenAdmin.AddTask.route)},
               navigateToEmployeeAttendance={navController.navigate(ScreenAdmin.EmployeeAttendance.route)},
               navigateToAllExpense={navController.navigate(ScreenAdmin.AllExpenses.route)},
               navigateToAddGeofence={navController.navigate(ScreenAdmin.AddGeofence.route)}
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
            ViewAllEmployee()

        }

        composable(ScreenAdmin.AddTask.route){
            AddTask()
        }

        composable(ScreenAdmin.ViewAllTask.route){
            ViewAllTask()
        }
        composable(ScreenAdmin.EmployeeAttendance.route){
            AllEmployeeAttendance(employeeViewModel=emViewModel)
        }

        composable(ScreenAdmin.AllExpenses.route){
            Expense(viewModel=adminViewModel)
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


    }

}