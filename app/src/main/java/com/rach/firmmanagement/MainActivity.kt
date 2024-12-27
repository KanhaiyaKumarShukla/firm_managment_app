package com.rach.firmmanagement

import android.app.Activity
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rach.firmmanagement.HomeScreen.HomeScreen
import com.rach.firmmanagement.HomeScreen.HomeScreenDataLoad
import com.rach.firmmanagement.HomeScreen.NoAdmin
import com.rach.firmmanagement.employee.EmployAttendance
import com.rach.firmmanagement.employee.PunchInOutApp
import com.rach.firmmanagement.firmAdminOwner.AddWorkHoursScreen
import com.rach.firmmanagement.firmAdminOwner.EmployeeDetailsProfile
import com.rach.firmmanagement.firmAdminOwner.GoogleMapScreen
import com.rach.firmmanagement.login.OtpScreen
import com.rach.firmmanagement.login.PhoneNumberLogin
import com.rach.firmmanagement.login.RegisterScreen
import com.rach.firmmanagement.navigation.NavigationFirst
import com.rach.firmmanagement.necessaryItem.TimePickerHaiDialog
import com.rach.firmmanagement.testing.NotiUi
import com.rach.firmmanagement.testing.TimePickerOutlinedTextField

import com.rach.firmmanagement.ui.theme.FirmManagementTheme
import com.rach.firmmanagement.ui.theme.blueAcha
import com.rach.firmmanagement.ui.theme.yellow
import com.rach.firmmanagement.viewModel.AllEmployeeViewModel
import com.rach.firmmanagement.viewModel.LoginViewModel

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FirmManagementTheme {

                SetStatusBarColor(color = blueAcha)
                val viewModel: LoginViewModel = viewModel()
                val viewModel2: AllEmployeeViewModel = viewModel()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {

                    NavigationFirst()

                    //GoogleMapScreen(activity = this)
                    //EmployeeDetailsProfile(viewModel = viewModel2)
                    //HomeScreen(loginViewModel = viewModel, navigateToLogin = {})
                }
            }
        }
    }
}

@Composable
fun SetStatusBarColor(color: Color) {
    val view = LocalView.current

    if (!view.isInEditMode) {
        LaunchedEffect(key1 = true) {

            val window = (view.context as Activity).window
            window.statusBarColor = color.toArgb()

        }
    }
}

