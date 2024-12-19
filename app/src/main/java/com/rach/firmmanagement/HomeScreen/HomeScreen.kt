package com.rach.firmmanagement.HomeScreen

import android.Manifest
import android.app.Activity
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.FloatingActionButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.rach.firmmanagement.R
import com.rach.firmmanagement.firmAdminOwner.ScreenAdmin
import com.rach.firmmanagement.navigation.NavDrawerNavigation
import com.rach.firmmanagement.navigationDrawer.NavViewModel
import com.rach.firmmanagement.navigationDrawer.Screen
import com.rach.firmmanagement.navigationDrawer.screenDrawerItemList
import com.rach.firmmanagement.navigationDrawer.screensInBottom
import com.rach.firmmanagement.necessaryItem.AppBarView
import com.rach.firmmanagement.notification.NotificationUtils
import com.rach.firmmanagement.ui.theme.FirmManagementTheme
import com.rach.firmmanagement.ui.theme.blueAcha

import com.rach.firmmanagement.ui.theme.fontBablooBold
import com.rach.firmmanagement.viewModel.LoginViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    loginViewModel: LoginViewModel,
    navigateToLogin: () -> Unit
) {

    val context = LocalContext.current

    val scaffoldstate = rememberScaffoldState()
    val viewModel: NavViewModel = viewModel()
    val scope: CoroutineScope = rememberCoroutineScope()
    val controller = rememberNavController()
    val navBackStackEntry by controller.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val currentScreen = remember {
        viewModel.currentScreen.value
    }

    val title = remember {
        mutableStateOf(currentScreen.title)
    }

    val currentUserName = FirebaseAuth.getInstance().currentUser?.displayName.toString()


    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->

            if (permissions[Manifest.permission.POST_NOTIFICATIONS] == true &&
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            ) {
                Toast.makeText(context, "Notification Permission Granted", Toast.LENGTH_LONG).show()
            } else {

                val rationRequired = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        context as Activity,
                        Manifest.permission.POST_NOTIFICATIONS
                    )

                    ActivityCompat.shouldShowRequestPermissionRationale(
                        context as Activity,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )

                    ActivityCompat.shouldShowRequestPermissionRationale(
                        context as Activity,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                } else {
                    TODO("VERSION.SDK_INT < TIRAMISU")
                }

                if (rationRequired) {

                    Toast.makeText(
                        context,
                        "Notification Permission is Required",
                        Toast.LENGTH_LONG
                    ).show()

                } else {

                    Toast.makeText(
                        context,
                        "Notification is Required To Work Properly. Please Enable it in Settings.",
                        Toast.LENGTH_LONG
                    ).show()
                }

            }

        })
    LaunchedEffect(Unit) {

        val notificationUtils = NotificationUtils(context,scope)

        if (notificationUtils.hasPermissionNotification(context)) {
            // Already Permission
        } else {

            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )

            )

        }
    }

    val bottomBar: @Composable () -> Unit = {
        if (currentScreen is Screen.DrawerScreen || currentScreen == Screen.BottomScreen.Home) {
            BottomNavigation(
                modifier = Modifier.wrapContentSize(),
                backgroundColor = blueAcha,
                elevation = 8.dp
            ) {

                screensInBottom.forEach { item ->
                    BottomNavigationItem(selected = currentRoute == item.bRoute,
                        onClick = { controller.navigate(item.bRoute) },
                        icon = {
                            Icon(
                                painter = painterResource(id = item.icon),
                                contentDescription = "Bottom App Bar"
                            )
                        },
                        selectedContentColor = Color.White,
                        unselectedContentColor = Color.Black,
                        label = { Text(text = item.bTitle) }
                    )

                }


            }
        }
    }

    Scaffold(

        scaffoldState = scaffoldstate,
        bottomBar = bottomBar,
        topBar = {

            AppBarView(title = title.value, onNavClick = {

                scope.launch {
                    scaffoldstate.drawerState.open()
                }

            })


        },
        drawerContent = {
            LazyColumn {
                items(screenDrawerItemList) { item ->

                    DrawerItemDesign(selected = currentRoute == item.dRoute, item = item) {

                        scope.launch {
                            scaffoldstate.drawerState.close()
                        }

                        if (item.dRoute == "Share") {

                            // have to implement share functionality

                        } else if (item.dRoute == "LogOut") {

                            Firebase.auth.signOut()
                            navigateToLogin()


                        } else {
                            controller.navigate(item.dRoute)
                            title.value = item.dTitle

                        }

                    }


                }
            }
        }


    ) {
        NavDrawerNavigation(
            navController = controller,
            viewModel = viewModel,
            pd = it,
            loginViewModel = loginViewModel
        )
    }

}
/*
@Composable
fun DrawerItemDesign(
    selected: Boolean,
    item: Screen.DrawerScreen,
    onDrawerPerItemClick: () -> Unit
) {

    val bgColor = if (selected) blueAcha else Color.White

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clickable { onDrawerPerItemClick() }
            .background(bgColor),
        verticalAlignment = Alignment.CenterVertically

    ) {

        Icon(painter = painterResource(id = item.icon), contentDescription = "Home")
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = item.dTitle, style = fontBablooBold)


    }
}
*/

@Composable
fun DrawerItemDesign(
    selected: Boolean,
    item: Screen.DrawerScreen,
    onDrawerPerItemClick: () -> Unit
) {
    val bgColor = if (selected) blueAcha else Color.White
    val contentColor = if (selected) Color.White else Color.Black

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp) // Apply margin
            .clickable { onDrawerPerItemClick() }
            .background(bgColor, shape = RoundedCornerShape(12.dp)) // Rounded corners
            .padding(16.dp), // Inner padding for larger size
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = item.icon),
            contentDescription = item.dTitle,
            tint = contentColor, // Change icon color based on selection
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = item.dTitle,
            style = fontBablooBold.copy(fontSize = 18.sp),
            color = contentColor // Change text color based on selection
        )
    }
}


@Composable
fun AdminPanelScreen(
    navigateToAddStaff: () -> Unit,
    navigateToHoliday: () -> Unit,
    navigateToWorkingHours: () -> Unit,
    navigateToViewEmpl: () -> Unit,
    navigateToViewAllTask: () -> Unit,
    navigateToTask: () -> Unit,
    navigateToEmployeeAttendance: () -> Unit,
    navigateToAllExpense: () -> Unit

) {


    val scrollState = rememberScrollState()
    val scaffoldState = rememberScaffoldState()

    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navigateToAddStaff() },
                backgroundColor = blueAcha,
                elevation = FloatingActionButtonDefaults.elevation(10.dp)
            ) {

                Icon(
                    imageVector = Icons.Default.Add, contentDescription = "Add Employee",
                    tint = Color.White
                )

            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(it)
                .verticalScroll(scrollState)
        ) {

            TopWelcomeSection(adminName = "Admin")

            Spacer(modifier = Modifier.height(16.dp))
            //  Hours Option
            OptionCard(
                title = "Add Hours",
                description = "Add or edit working hours",
                drawableRes = R.drawable.add,  // Using AccessTime icon for hours
                onClick = { navigateToWorkingHours() }
            )

            // Holiday Option
            OptionCard(
                title = "Add Holiday",
                description = "Add or remove holidays",
                drawableRes = R.drawable.holiday,  // Calendar icon
                onClick = { navigateToHoliday() }
            )

            OptionCard(
                title = "Add Task",
                description = "Add Tasks",
                drawableRes = R.drawable.about_us,  // Calendar icon
                onClick = { navigateToTask() }
            )

            // View Employees Option
            OptionCard(
                title = "View Employees",
                description = "Check employee details",
                drawableRes = R.drawable.about,
                onClick = { navigateToViewEmpl() }
            )

            // View All Task
            OptionCard(
                title = "View All Task",
                description = "See monthly Tasks",
                drawableRes = R.drawable.report,  // Reports icon for viewing reports
                onClick = { navigateToViewAllTask() }
            )

            // Employee Attendance
            OptionCard(
                title = "Employee Attendance",
                description = "See Employee Attendance",
                drawableRes = R.drawable.baseline_account_circle_24,  // Settings icon for app settings
                onClick = { navigateToEmployeeAttendance() }
            )

            OptionCard(
                title = "All Expenses",
                description = "See Expenses",
                drawableRes = R.drawable.expense_list_ic,  // Settings icon for app settings
                onClick = { navigateToAllExpense() }
            )
        }

    }


}

@Composable
fun TopWelcomeSection(adminName: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF3F51B5),  // Gradient color start
                        Color(0xFF2196F3)   // Gradient color end
                    )
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Column {
            Text(
                text = "Welcome, $adminName!",
                style = MaterialTheme.typography.h5.copy(color = Color.White),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Today is ${getCurrentDate()}",
                style = MaterialTheme.typography.body2.copy(color = Color.White)
            )
        }
    }
}

// Function to get current date
@Composable
fun getCurrentDate(): String {
    val dateFormat = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
    return dateFormat.format(Date())
}

@Composable
fun OptionCard(
    title: String,
    description: String,
    drawableRes: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        elevation = 8.dp,  // Shadow effect
        shape = RoundedCornerShape(16.dp)  // Rounded corners for modern look
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = drawableRes),
                contentDescription = title,
                modifier = Modifier.size(35.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.h6)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = description, style = MaterialTheme.typography.body2)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NoDij() {
    FirmManagementTheme {
        AdminPanelScreen({}, {}, {}, {}, {}, {}, {}, {})
    }
}

