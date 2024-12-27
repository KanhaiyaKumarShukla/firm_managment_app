package com.rach.firmmanagement.firmAdminOwner

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rach.firmmanagement.necessaryItem.CustomOutlinedTextFiled
import com.rach.firmmanagement.notification.MyNotification
import com.rach.firmmanagement.ui.theme.FirmManagementTheme
import com.rach.firmmanagement.ui.theme.blueAcha
import com.rach.firmmanagement.ui.theme.fontBablooBold
import com.rach.firmmanagement.ui.theme.fontPoppinsMedium
import com.rach.firmmanagement.ui.theme.progressBarBgColor
import com.rach.firmmanagement.viewModel.AdminViewModel
import kotlinx.coroutines.launch

@Composable
fun AddStaff(adminViewModel: AdminViewModel = viewModel()) {

    val context = LocalContext.current

    val name by adminViewModel.empName.collectAsState()
    val phoneNumber by adminViewModel.phoneNumber.collectAsState()
    val role by adminViewModel.role.collectAsState()
    val salary by adminViewModel.salary.collectAsState()
    val registrationDate by adminViewModel.registrationDate.collectAsState()
    val timeVariation by adminViewModel.timeVariation.collectAsState()
    val leaveDays by adminViewModel.leaveDays.collectAsState()

    val buttonState by adminViewModel.onButtonClicked.collectAsState()

    val progressState by adminViewModel.progressBarState.collectAsState()

    val nameError = buttonState && name.isEmpty()
    val phoneNumberError = buttonState && phoneNumber.isEmpty()
    val roleError = buttonState && role.isEmpty()
    val salaryError = buttonState && salary.isEmpty()
    val regDateError = buttonState && registrationDate.isEmpty()
    val timeVariationError = buttonState && timeVariation.isEmpty()
    val leaveDaysError = buttonState && leaveDays.isEmpty()

    val scope = rememberCoroutineScope()

    if (progressState) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(progressBarBgColor.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 22.dp, end = 22.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Add New Employee", fontSize = 20.sp, style = fontPoppinsMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(10.dp)
        )
        Text(
            text = "Please Fill Your Employee Details", fontSize = 18.sp,
            style = fontPoppinsMedium
        )


        Spacer(modifier = Modifier.height(20.dp))

        CustomOutlinedTextFiled(
            value = name,
            onValueChange = {
                adminViewModel.onChangeEmpName(it)
            },
            label = "Enter Employee Name",
            singleLine = true,
            isError = nameError,
            readOnly = false
        )

        Spacer(modifier = Modifier.height(10.dp))

        CustomOutlinedTextFiled(
            value = phoneNumber,
            onValueChange = {
                adminViewModel.onChangePhoneNumber(it)
            },
            label = "Enter Phone Number",
            singleLine = true,
            isError = phoneNumberError,
            readOnly = false
        )

        Spacer(modifier = Modifier.height(10.dp))

        CustomOutlinedTextFiled(
            value = role,
            onValueChange = {
                adminViewModel.onChangeRole(it)
            },
            label = "Enter Employee Role",
            singleLine = true,
            isError = roleError,
            readOnly = false
        )

        Spacer(modifier = Modifier.height(10.dp))

        CustomOutlinedTextFiled(
            value = salary,
            onValueChange = {
                adminViewModel.onChangeSalary(it)
            },
            label = "Salary",
            singleLine = true,
            isError = salaryError,
            readOnly = false
        )
        Spacer(modifier = Modifier.height(10.dp))
        CustomOutlinedTextFiled(
            value = timeVariation,
            onValueChange = {
                adminViewModel.onChangeTimeVariation(it)
            },
            label = "Time Variation",
            singleLine = true,
            isError = timeVariationError,
            readOnly = false
        )
        Spacer(modifier = Modifier.height(10.dp))
        CustomOutlinedTextFiled(
            value = leaveDays,
            onValueChange = {
                adminViewModel.onChangeLeaveDays(it)
            },
            label = "Leave Days",
            singleLine = true,
            isError = leaveDaysError,
            readOnly = false
        )
        Spacer(modifier = Modifier.height(10.dp))
        CustomOutlinedTextFiled(
            value = registrationDate,
            onValueChange = {
                adminViewModel.onChangeRegistrationDate(it)
            },
            label = "Employee Registration Date",
            singleLine = true,
            isError = regDateError,
            readOnly = true
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CustomButton(
                onClick = {
                    if(!nameError && !phoneNumberError && !roleError && !salaryError && !regDateError && !timeVariationError && !leaveDaysError){
                        // naviage to add holiday
                    }else{
                        // toast
                    }
                },
                text = "Add Holidays"
            )

            CustomButton(
                onClick = {
                    if(!nameError && !phoneNumberError && !roleError && !salaryError && !regDateError && !timeVariationError && !leaveDaysError){
                        // naviage to work hours
                    }else{
                        // toast
                    }
                },
                text = "Add Work Time"
            )
        }
        Spacer(modifier = Modifier.height(40.dp))


        CustomButton(
            onClick = {
                adminViewModel.onButtonStateChange(true)
                scope.launch {
                    adminViewModel.onChangeProgressState(true)
                    adminViewModel.addEmployee(
                        onSuccess = {
                            Toast.makeText(context, "Registration SuccessFul", Toast.LENGTH_SHORT)
                                .show()
                            adminViewModel.onChangeProgressState(false)
                            val notification = MyNotification(
                                context = context,
                                title = "Firm Management App",
                                message = "Congratulations $name Register as Employee"
                            )

                            notification.fireNotification()
                        },
                        onFailure = {
                            Toast.makeText(context, "Registration Failed", Toast.LENGTH_LONG).show()
                            adminViewModel.onChangeProgressState(false)
                            val notification = MyNotification(
                                context = context,
                                title = "Firm Management App",
                                message = "Employee Added Failed"
                            )

                            notification.fireNotification()
                        }
                    )
                }
            },
            text = "Register",
            modifier = Modifier.width(100.dp)
        )
    }
}

@Composable
fun CustomButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors= ButtonDefaults.buttonColors(
            blueAcha
        )
    ) {
        Text(
            text = text,
            style = fontBablooBold,
            color = Color.White
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AddStaffPreview() {
    FirmManagementTheme {
        AddStaff()
    }
}