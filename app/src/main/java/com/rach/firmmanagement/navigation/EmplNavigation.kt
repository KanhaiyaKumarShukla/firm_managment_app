package com.rach.firmmanagement.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rach.firmmanagement.HomeScreen.ScreensManage
import com.rach.firmmanagement.employee.AdvanceHistoryScreen
import com.rach.firmmanagement.employee.AdvanceMoneyRequestScreen
import com.rach.firmmanagement.employee.AllExpense
import com.rach.firmmanagement.employee.ChatScreen
import com.rach.firmmanagement.employee.EmployAttendance
import com.rach.firmmanagement.employee.EmployeeHomeScreen
import com.rach.firmmanagement.employee.LeaveRequestScreen
import com.rach.firmmanagement.employee.PunchInOutApp
import com.rach.firmmanagement.employee.RaiseExpense
import com.rach.firmmanagement.employee.SeeTasks
import com.rach.firmmanagement.employee.ViewLeaveHistory
import com.rach.firmmanagement.viewModel.AllEmployeeViewModel
import com.rach.firmmanagement.viewModel.EmlAllTask
import com.rach.firmmanagement.viewModel.EmployeeViewModel1
import com.rach.firmmanagement.viewModel.LoginViewModel
import com.rach.firmmanagement.viewModel.ProfileViewModel

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun EmplNavigation() {

    val navController = rememberNavController()
    
    val loginViewModel:LoginViewModel = viewModel()
    val emlViewModel:EmlAllTask = viewModel()
    val employeeViewModel : EmployeeViewModel1=viewModel()
    val profileViewModel: ProfileViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = ScreensManage.EmployeeHomeScreen.route
    ) {

        composable(ScreensManage.EmployeeHomeScreen.route) {
            EmployeeHomeScreen(
                viewmodel = emlViewModel,
                profileViewModel = profileViewModel,
                navigateToSeeTask = { navController.navigate(ScreensManage.goToTask.route) },
                navigateToRaiseLeave = { navController.navigate(ScreensManage.RaiseAHoliday.route) },
                navigateToAdvanceMoney = { navController.navigate(ScreensManage.AdvanceMoney.route) },
                navigateToPunchInPunchOut = {navController.navigate(ScreensManage.PunchInOut.route)},
                navigateToEmployeeAttendence = {navController.navigate(ScreensManage.AttendanceSummary.route)},
                navigateToRaiseExpense = { navController.navigate(ScreensManage.RaiseExpense.route) },
                navigateToAllExpense = { navController.navigate(ScreensManage.AllExpense.route) },
                navigateToChatScreen = { navController.navigate(ScreensManage.ChatScreen.route) }
            )
        }
        
        composable(ScreensManage.goToTask.route){
            SeeTasks(viewModel = emlViewModel , profileViewModel = profileViewModel)
        }
        
        composable(ScreensManage.RaiseAHoliday.route){
            
            LeaveRequestScreen(
                loginViewModel = loginViewModel,
                onViewLeaveHistoryClick = { navController.navigate(ScreensManage.ViewLeaveHistory.route) }
            )
            
        }
        composable(ScreensManage.ViewLeaveHistory.route){
            ViewLeaveHistory(loginViewModel = loginViewModel, profileViewModel = profileViewModel)
        }

        composable(ScreensManage.ViewAdvanceHistory.route){
            AdvanceHistoryScreen(employeeViewModel1 =employeeViewModel, loginViewModel = loginViewModel)
        }

        composable(ScreensManage.AdvanceMoney.route){
            AdvanceMoneyRequestScreen(
                loginViewModel = loginViewModel ,
                onViewAdvanceHistoryClick={navController.navigate(ScreensManage.ViewAdvanceHistory.route)}
            )
        }

        composable(ScreensManage.PunchInOut.route){
            PunchInOutApp(
                viewModel = emlViewModel,
                profileViewModel = profileViewModel,
                navigateToEmployeeAttendence = {navController.navigate(ScreensManage.AttendanceSummary.route)}
            )
        }
        
        composable(ScreensManage.AttendanceSummary.route){
            EmployAttendance(loginViewModel = loginViewModel, employeeViewModel = employeeViewModel, profileViewModel = profileViewModel)
        }
        composable(ScreensManage.RaiseExpense.route){
            RaiseExpense(loginViewModel = loginViewModel)
        }
        composable(ScreensManage.AllExpense.route){
            AllExpense(loginViewModel = loginViewModel, profileViewModel = profileViewModel)
        }
        composable(ScreensManage.ChatScreen.route){
            ChatScreen(loginViewModel = loginViewModel, profileViewModel = profileViewModel)
        }
    }

}