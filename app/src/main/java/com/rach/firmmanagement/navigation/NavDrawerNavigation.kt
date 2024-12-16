package com.rach.firmmanagement.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.rach.firmmanagement.HomeScreen.AddRequest
import com.rach.firmmanagement.HomeScreen.HomeScreenDataLoad
import com.rach.firmmanagement.HomeScreen.ProfileScreen
import com.rach.firmmanagement.HomeScreen.AdminPanelScreen
import com.rach.firmmanagement.HomeScreen.AnotherRouter
import com.rach.firmmanagement.HomeScreen.ScreensManage
import com.rach.firmmanagement.employee.PunchInOutApp
import com.rach.firmmanagement.employee.SeeTasks
import com.rach.firmmanagement.firmAdminOwner.AddStaff
import com.rach.firmmanagement.firmAdminOwner.AddWorkHoursScreen
import com.rach.firmmanagement.firmAdminOwner.HolidayAddScreen
import com.rach.firmmanagement.firmAdminOwner.ScreenAdmin
import com.rach.firmmanagement.navigationDrawer.NavViewModel
import com.rach.firmmanagement.navigationDrawer.Screen
import com.rach.firmmanagement.viewModel.EmlAllTask
import com.rach.firmmanagement.viewModel.LoginViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavDrawerNavigation(
    navController: NavController,
    viewModel: NavViewModel,
    pd: PaddingValues,
    loginViewModel: LoginViewModel
) {


    val viewModel2: LoginViewModel = viewModel()
    val emlViewModel:EmlAllTask = viewModel()


    NavHost(
        navController = navController as NavHostController,
        startDestination = Screen.DrawerScreen.MainHome.route,
        modifier = Modifier.padding(pd)
    ) {

        composable(Screen.DrawerScreen.MainHome.route) {

            HomeScreenDataLoad(
                loginViewModel = viewModel2,
                navigateToRaise = { navController.navigate("RaiseRequest")})

        }

        composable(Screen.BottomScreen.Another.route) {

            AnotherRouter()

        }
        composable(Screen.DrawerScreen.Profile.route) {
            ProfileScreen(loginViewModel = viewModel2)

        }
        composable(Screen.DrawerScreen.About.route) {
            AddWorkHoursScreen()

        }

        composable(Screen.DrawerScreen.ContactUs.route) {
            PunchInOutApp(
                viewModel = emlViewModel,
                loginViewModel = loginViewModel,
                navigateToEmployeeAttendence = {navController.navigate(ScreensManage.AttendanceSummary.route)}
            )

        }

        composable(Screen.DrawerScreen.Settings.route) {

            HolidayAddScreen()

        }

        composable("RaiseRequest") {
            AddRequest()
        }

        // AdminPanel Navigation

        composable("SeeTask") {
            SeeTasks(viewModel = emlViewModel,
                loginViewModel = viewModel2)
        }


    }

}