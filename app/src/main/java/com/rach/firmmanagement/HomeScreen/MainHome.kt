package com.rach.firmmanagement.HomeScreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rach.firmmanagement.employee.EmployeeHomeScreen
import com.rach.firmmanagement.employee.NoEmployee
import com.rach.firmmanagement.firmAdminOwner.RegPending
import com.rach.firmmanagement.navigation.EmplNavigation
import com.rach.firmmanagement.navigation.Navigation2
import com.rach.firmmanagement.viewModel.LoginViewModel
import com.rach.firmmanagement.viewModel.NoAdminViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreenDataLoad(
    loginViewModel: LoginViewModel, navigateToRaise: () -> Unit,
    noAdminViewModel: NoAdminViewModel = viewModel()

) {

    val isRefresh by noAdminViewModel.isRefresh.collectAsState()

    val pullToRefreshState = rememberPullToRefreshState()
    val context = LocalContext.current
    val adminNumber by loginViewModel.firmOwnerNumber.collectAsState()

    val selectGenderState by loginViewModel.selectGenderState.collectAsState()

    val uiState by noAdminViewModel.uiState.collectAsState()


    val scope = rememberCoroutineScope()
    /*
    PullToRefreshContainer(
        state = pullToRefreshState,
        indicator = {pullToRefreshState ->


            if (pullToRefreshState.isRefreshing){
                Box(modifier = Modifier,
                    contentAlignment = Alignment.Center){
                    CircularProgressIndicator()
                }
            }

        },
        modifier = Modifier.fillMaxSize()
    )

     */

    LaunchedEffect(key1 = selectGenderState, key2 = adminNumber) {

        scope.launch {
            noAdminViewModel.checkUserExist(
                genderState = selectGenderState,
                adminNumber = adminNumber,
                dataFound = {

                },
                noData = {

                },
                pendingData = {

                }
            )
        }

    }

    when (uiState) {
        is NoAdminViewModel.UiState.Loading -> {
            ShimmerEffect()
        }

        is NoAdminViewModel.UiState.AdminPanel -> {
            Navigation2()
        }

        is NoAdminViewModel.UiState.OnPending -> {
            RegPending()
        }

        is NoAdminViewModel.UiState.OnFailure -> {
            NoAdmin { navigateToRaise() }
        }

        is NoAdminViewModel.UiState.EmployeeUi -> {

            EmplNavigation()

        }

        is NoAdminViewModel.UiState.NoEmployee -> {

            NoEmployee()

        }
    }


}