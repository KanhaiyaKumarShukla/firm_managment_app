package com.rach.firmmanagement.navigation

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rach.firmmanagement.appOwner.AddSuperAdmin
import com.rach.firmmanagement.appOwner.AppOwnerHomeScreen
import com.rach.firmmanagement.appOwner.ScreenAppOwner
import com.rach.firmmanagement.viewModel.AppOwnerViewModel
import com.rach.firmmanagement.viewModel.LoginViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppOwnerNavigation(){
    val navController = rememberNavController()
    val loginViewModel:LoginViewModel = viewModel()
    val appOwnerViewModel: AppOwnerViewModel = viewModel()
    NavHost(navController = navController, startDestination = ScreenAppOwner.AppOwnerPanel.route) {
        composable(ScreenAppOwner.AppOwnerPanel.route) {
            AppOwnerHomeScreen(
                navigateToAddSuperAdmin = { navController.navigate(ScreenAppOwner.AddSuperAdmin.route) },
                viewModel = appOwnerViewModel
            )
        }

        composable(ScreenAppOwner.AddSuperAdmin.route){
            AddSuperAdmin()
        }

    }

}