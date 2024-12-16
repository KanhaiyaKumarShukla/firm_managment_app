package com.rach.firmmanagement.navigation

import android.annotation.SuppressLint
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.rach.firmmanagement.HomeScreen.AddRequest
import com.rach.firmmanagement.HomeScreen.AdminPanelScreen
import com.rach.firmmanagement.HomeScreen.HomeScreen
import com.rach.firmmanagement.HomeScreen.HomeScreenDataLoad
import com.rach.firmmanagement.HomeScreen.ProfileScreen
import com.rach.firmmanagement.HomeScreen.ScreensManage
import com.rach.firmmanagement.employee.EmployeeHomeScreen
import com.rach.firmmanagement.employee.SeeTasks
import com.rach.firmmanagement.firmAdminOwner.AddStaff
import com.rach.firmmanagement.firmAdminOwner.AddWorkHoursScreen
import com.rach.firmmanagement.firmAdminOwner.HolidayAddScreen
import com.rach.firmmanagement.firmAdminOwner.ScreenAdmin
import com.rach.firmmanagement.login.OtpScreen
import com.rach.firmmanagement.login.PhoneNumberLogin
import com.rach.firmmanagement.login.RegisterScreen
import com.rach.firmmanagement.navigationDrawer.Screen
import com.rach.firmmanagement.viewModel.EmlAllTask
import com.rach.firmmanagement.viewModel.LoginViewModel

//@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("NewApi")
@Composable
fun NavigationFirst() {
    val navController = rememberNavController()

    val viewModel: LoginViewModel = viewModel()
    val emViewModel :EmlAllTask = viewModel()

    val currentUser = FirebaseAuth.getInstance().currentUser



    NavHost(navController = navController, startDestination = if (currentUser == null ){
        ScreensManage.PhoneLogin.route
    }else{
        ScreensManage.HomeLogi.route
    }) {
        composable(ScreensManage.PhoneLogin.route) {
//            PhoneNumberLogin(
//                navigateToOtp = { navController.navigate("OtpScreen") },
//                navigateToRegister = { navController.navigate("Register") }
//            )

            PhoneNumberLogin(
                navigateToOtp = { navController.navigate(ScreensManage.OtpScreen.route) },
                navigateToRegister = { navController.navigate(ScreensManage.Register.route) },
                viewModel = viewModel
            )

        }
        composable(ScreensManage.Register.route) {
            RegisterScreen {
                navController.navigate(ScreensManage.PhoneLogin.route)
            }

        }
        composable(ScreensManage.OtpScreen.route) {
            OtpScreen(naviagteToHome = {
                navController.navigate(ScreensManage.HomeLogi.route){
                    popUpTo(ScreensManage.PhoneLogin.route){
                        inclusive = true
                    }
                }
            }, loginViewModel = viewModel)

        }

        composable(ScreensManage.HomeLogi.route) {

            HomeScreen(loginViewModel = viewModel) {

                navController.navigate(ScreensManage.PhoneLogin.route){
                    popUpTo(ScreensManage.HomeLogi.route){
                        inclusive = true
                    }
                }

            }
            BackHandler {
                navController.popBackStack()
            }

        }

        composable(ScreensManage.CheckData.route) {

            HomeScreenDataLoad(
                loginViewModel = viewModel,
                navigateToRaise = { navController.navigate(ScreensManage.RaiseScreen.route)}
            )
        }

        composable(ScreensManage.RaiseScreen.route) {
            AddRequest()
        }
        composable(Screen.DrawerScreen.Profile.route){
            ProfileScreen(loginViewModel = viewModel)
        }



        composable("SeeTask"){
            SeeTasks(
                viewModel = emViewModel,
                loginViewModel = viewModel
            )
        }

    }
}