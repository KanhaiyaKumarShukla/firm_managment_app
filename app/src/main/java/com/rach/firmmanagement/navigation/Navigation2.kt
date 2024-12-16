package com.rach.firmmanagement.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.Navigation
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rach.firmmanagement.HomeScreen.AdminPanelScreen
import com.rach.firmmanagement.firmAdminOwner.AddStaff
import com.rach.firmmanagement.firmAdminOwner.AddTask
import com.rach.firmmanagement.firmAdminOwner.AddWorkHoursScreen
import com.rach.firmmanagement.firmAdminOwner.AllEmployeeAttendance
import com.rach.firmmanagement.firmAdminOwner.HolidayAddScreen
import com.rach.firmmanagement.firmAdminOwner.ScreenAdmin
import com.rach.firmmanagement.firmAdminOwner.ViewAllEmployee
import com.rach.firmmanagement.firmAdminOwner.ViewAllTask
import com.rach.firmmanagement.viewModel.AllEmployeeViewModel
import com.rach.firmmanagement.viewModel.EmlAllTask

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation2(){

    val navController = rememberNavController()
    val emViewModel : AllEmployeeViewModel = viewModel()

    NavHost(navController = navController, startDestination = ScreenAdmin.AdminPanel.route) {

        composable(ScreenAdmin.AdminPanel.route){
           AdminPanelScreen(
               navigateToAddStaff = { navController.navigate(ScreenAdmin.AddEmployee.route)},
               navigateToHoliday = { navController.navigate(ScreenAdmin.AddHoliday.route) },
               navigateToWorkingHours = { navController.navigate(ScreenAdmin.AddWorkHours.route) },
               navigateToViewEmpl = { navController.navigate(ScreenAdmin.ViewEmployee.route) },
               navigateToViewAllTask = { navController.navigate(ScreenAdmin.ViewAllTask.route) },
               navigateToTask = {navController.navigate(ScreenAdmin.AddTask.route)},
               navigateToEmployeeAttendance={navController.navigate(ScreenAdmin.EmployeeAttendance.route)}
           )
        }

        composable(ScreenAdmin.AddEmployee.route){
            AddStaff()
        }

        composable(ScreenAdmin.AddHoliday.route) {
            HolidayAddScreen()
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

    }

}