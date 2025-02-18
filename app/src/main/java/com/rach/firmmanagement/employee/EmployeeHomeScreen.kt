package com.rach.firmmanagement.employee

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rach.firmmanagement.R
import com.rach.firmmanagement.ui.theme.FirmManagementTheme
import com.rach.firmmanagement.ui.theme.blueAcha
import com.rach.firmmanagement.ui.theme.fontBablooBold
import com.rach.firmmanagement.ui.theme.fontPoppinsMedium
import com.rach.firmmanagement.viewModel.EmlAllTask
import com.rach.firmmanagement.viewModel.LoginViewModel
import com.rach.firmmanagement.viewModel.NoAdminViewModel
import com.rach.firmmanagement.viewModel.ProfileViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeHomeScreen(
    viewmodel: EmlAllTask,
    profileViewModel: ProfileViewModel,
    navigateToSeeTask: () -> Unit,
    navigateToRaiseLeave: () -> Unit,
    navigateToAdvanceMoney: () -> Unit,
    navigateToPunchInPunchOut: () -> Unit,
    navigateToEmployeeAttendence: () -> Unit,
    navigateToRaiseExpense: () -> Unit,
    navigateToAllExpense: () -> Unit,
    navigateToChatScreen: () -> Unit
) {

    val progressState by viewmodel.progressBarState.collectAsState()
    val employeeIdentity by profileViewModel.employeeIdentity.collectAsState()
    val loading by profileViewModel.loading
    val adminPhoneNumber = employeeIdentity.adminNumber.toString()
    val name = employeeIdentity.name.toString()
    val scope = rememberCoroutineScope()


    val pullToRefreshState = rememberPullToRefreshState()


    LaunchedEffect(key1 = adminPhoneNumber) {

        /*
        scope.launch {
            viewmodel.loadEmployeeData(
                adminPhoneNumber = adminPhoneNumber
            )
        }

         */


    }

    Box(modifier = Modifier.fillMaxSize()) {

        if (progressState || loading) {
            Box(modifier = Modifier.fillMaxSize())
            {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {

                // Top Welcome Section
                // change
                Log.d("Hins", employeeIdentity.toString())
                val role=employeeIdentity.role
                val joiningDate=employeeIdentity.registrationDate
                //employeeData.firstOrNull()?.let {
                    WelcomeSection(
                        userName = name,
                        role = role?: "-",
                        joiningDate = joiningDate ?: ""
                    )
                //}
                // change end

                Spacer(modifier = Modifier.height(24.dp))


                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    content = {
                        item {
                            OptionCard("Punch In/Out", iconVector =Icons.Default.Check,
                                onClick = {

                                    navigateToPunchInPunchOut()
                                })
                        }
                        item {
                            OptionCard("Raise Leave", iconVector =Icons.Default.Call,
                                onClick = { navigateToRaiseLeave() })
                        }
                        item {
                            OptionCard("Attendance History", iconVector =Icons.Default.DateRange,
                                onClick = { navigateToEmployeeAttendence() })
                        }
                        item {
                            OptionCard(
                                "Task",
                                Icons.Default.CheckCircle,
                                onClick = { navigateToSeeTask() })
                        }
                        item {
                            OptionCard("Raise Adv Money", iconVector =  Icons.Default.Info,
                                onClick = { navigateToAdvanceMoney() })
                        }
                        item {
                            OptionCard(
                                "Raise Expense",
                                iconDrawable = painterResource(id=R.drawable.expense),
                                onClick = { navigateToRaiseExpense() }
                            )
                        }
                        item {
                            OptionCard(
                                "Expense Hist",
                                iconDrawable = painterResource(id=R.drawable.expense_list_ic),
                                onClick = { navigateToAllExpense() }
                            )
                        }
                        item {
                            OptionCard(
                                "Chat",
                                iconVector = Icons.Default.Send,
                                onClick = { navigateToChatScreen() }
                            )
                        }

                    }
                )
            }
        }


    }


}


@Composable
fun WelcomeSection(userName: String, role: String, joiningDate: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile Image
        Image(
            painter = painterResource(R.drawable.about_us), // Replace with actual profile image
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .border(2.dp, blueAcha, CircleShape)
        )
        Spacer(modifier = Modifier.width(16.dp))
        // Welcome Text
        Column {
            Text(
                text = "Welcome,",
                style = fontBablooBold,
                fontSize = 20.sp
            )



            Text(
                text = userName,
                style = fontPoppinsMedium,
                color = blueAcha
            )

            Text(text = "Joining Date: $joiningDate")

        }
    }
}

@Composable
fun OptionCard(
    optionName: String,
    iconVector: ImageVector?=null,
    iconDrawable: Painter? = null,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier.clickable {
            onClick()
        }
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = 8.dp,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .aspectRatio(1f)
                .clickable { onClick() },
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (iconVector != null) {
                    // Display vector icon
                    Icon(
                        imageVector = iconVector,
                        contentDescription = optionName,
                        tint = blueAcha,
                        modifier = Modifier.size(48.dp)
                    )
                } else if (iconDrawable != null) {
                    // Display drawable or PNG icon
                    Image(
                        painter = iconDrawable,
                        contentDescription = optionName,
                        modifier = Modifier.size(48.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = optionName,
                    style = MaterialTheme.typography.h6,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun EmployeeHomeScreenPreview() {
    FirmManagementTheme {
        EmployeeHomeScreen(
            viewmodel = EmlAllTask(),
            profileViewModel = ProfileViewModel(),
            {},
            {},
            {},
            {},
            {},
            {},
            {},
            {}
        )

    }
}