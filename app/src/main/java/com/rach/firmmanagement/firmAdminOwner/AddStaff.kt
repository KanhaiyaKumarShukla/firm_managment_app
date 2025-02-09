package com.rach.firmmanagement.firmAdminOwner

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.gson.Gson
import com.rach.firmmanagement.dataClassImp.ViewAllEmployeeDataClass
import com.rach.firmmanagement.necessaryItem.CustomOutlinedTextFiled
import com.rach.firmmanagement.notification.MyNotification
import com.rach.firmmanagement.ui.theme.FirmManagementTheme
import com.rach.firmmanagement.ui.theme.blueAcha
import com.rach.firmmanagement.ui.theme.fontBablooBold
import com.rach.firmmanagement.ui.theme.fontPoppinsMedium
import com.rach.firmmanagement.ui.theme.progressBarBgColor
import com.rach.firmmanagement.viewModel.AdminViewModel
import com.rach.firmmanagement.viewModel.AllEmployeeViewModel
import com.rach.firmmanagement.viewModel.GeofenceViewModel
import com.rach.firmmanagement.viewModel.ProfileViewModel
import kotlinx.coroutines.launch
import java.net.URLEncoder

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun AddStaff(
    navController: NavController,
    profileViewModel: ProfileViewModel,
    adminViewModel: AdminViewModel = viewModel(),
    allEmployeeViewModel: AllEmployeeViewModel,
    geofenceViewModel: GeofenceViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {

    val context = LocalContext.current

    var showDialog by remember { mutableStateOf(false) }
    val selectedPermissions by adminViewModel.selectedPermissions.collectAsState()

    val permissionsList = listOf("Attendance", "Working Hours", "Holiday", "Task", "Expenses", "Geofence")


    val name by adminViewModel.empName.collectAsState()
    val phoneNumber by adminViewModel.phoneNumber.collectAsState()
    val role by adminViewModel.role.collectAsState()
    val salary by adminViewModel.salary.collectAsState()
    val registrationDate by adminViewModel.registrationDate.collectAsState()
    val timeVariation by adminViewModel.timeVariation.collectAsState()
    val selectedTimeVariationUnit by adminViewModel.timeVariationUnit.collectAsState()

    val selectedAdminNumber by adminViewModel.selectedAdminNumber.collectAsState()
    val selectedFirmName by adminViewModel.selectedFirmName.collectAsState()

    val leaveDays by adminViewModel.leaveDays.collectAsState()

    val buttonState by adminViewModel.onButtonClicked.collectAsState()

    val progressState by adminViewModel.progressBarState.collectAsState()

    val allEmployees by allEmployeeViewModel.employeeList
    val nameError = buttonState && name.isEmpty()
    val phoneNumberError = buttonState && phoneNumber.isEmpty()
    val roleError = buttonState && role.isEmpty()
    val salaryError = buttonState && salary.isEmpty()
    val regDateError = buttonState && registrationDate.isEmpty()
    val timeVariationError = buttonState && timeVariation.isEmpty()
    val leaveDaysError = buttonState && leaveDays.isEmpty()
    val adminNumberError = buttonState && selectedAdminNumber.isEmpty()
    val firmNameError = buttonState && selectedFirmName.isEmpty()
    var selectedRole by remember { mutableStateOf("") }

    val geofences by geofenceViewModel.geofences.collectAsState()
    val scope = rememberCoroutineScope()

    var selectedSalaryUnit = adminViewModel.salaryUnit.collectAsState()

    val employeeIdentity by profileViewModel.employeeIdentity.collectAsState()
    val employeeLoading by profileViewModel.loading

    LaunchedEffect(Unit) {
        geofenceViewModel.fetchAllGeofences(employeeIdentity.firmName.toString())
    }

    if (progressState || employeeLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(progressBarBgColor.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = blueAcha,
                strokeWidth = 4.dp
            )
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

        Column (modifier = Modifier.fillMaxWidth()){
            var expanded by remember { mutableStateOf(false) }
            val roles = listOf("Admin", "Employee")

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = Color.Gray,
                        shape = RoundedCornerShape(6)
                    )
            ) {
                OutlinedTextField(
                    value = selectedRole,
                    onValueChange = { },
                    label = { Text("Select Role") },
                    readOnly = true,
                    modifier = Modifier.weight(1f)
                        .clickable {
                            expanded = true
                            Log.d("role", "123 role clicked: $selectedRole")
                        },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    )
                )
                Box {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = if (expanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowLeft,
                            contentDescription = "Expand"
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        offset = DpOffset(x = 0.dp, y = 0.dp)
                    ) {
                        roles.forEach { role ->
                            Log.d("role", "123 role item: $role")
                            DropdownMenuItem(
                                onClick = {
                                    selectedRole = role
                                    expanded = false
                                    // Notify the adminViewModel about the selected geofence
                                    adminViewModel.onChangeRole(role)
                                }
                            ) {
                                Text(text = role)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        CustomOutlinedTextFiled(
            value = selectedFirmName,
            onValueChange = {
                adminViewModel.onChangeSelectedFirmName(it)
            },
            label = "FirmName",
            singleLine = true,
            isError = firmNameError,
            readOnly = false
        )
        if (selectedRole != "Admin") {
            Spacer(modifier = Modifier.height(10.dp))

            CustomOutlinedTextFiled(
                value = selectedAdminNumber,
                onValueChange = {
                    adminViewModel.onChangeSelectedAdminNumber(it)
                },
                label = "AdminNumber",
                singleLine = true,
                isError = adminNumberError,
                readOnly = false
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        TextFieldWithDropdown(
            value = salary,
            onValueChange = {
                adminViewModel.onChangeSalary(it)
            },
            valueError = salaryError,
            unit = selectedSalaryUnit.value,
            onUnitChange = {
                  adminViewModel.onChangeSalaryUnit(it)
            },
            label = "Salary"
        )

        Spacer(modifier = Modifier.height(10.dp))

        Column (modifier = Modifier.fillMaxWidth()){
            var expanded by remember { mutableStateOf(false) }
            var selectedTitle by remember { mutableStateOf("") }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = Color.Gray,
                        shape = RoundedCornerShape(6)
                    )
            ) {
                OutlinedTextField(
                    value = selectedTitle,
                    onValueChange = { },
                    label = { Text("Select Geofence") },
                    readOnly = true,
                    modifier = Modifier.weight(1f)
                        .clickable {
                            expanded = true
                            Log.d("Geofence", "123 Geofence clicked: $geofences")
                        },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    )
                )
                Box {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = if (expanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowLeft,
                            contentDescription = "Expand"
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        offset = DpOffset(x = 0.dp, y = 0.dp)
                    ) {
                        geofences.forEach { geofenceItem ->
                            Log.d("Geofence", "123 Geofence item: $geofenceItem")
                            DropdownMenuItem(
                                onClick = {
                                    selectedTitle = geofenceItem.title ?: "Unknown"
                                    expanded = false
                                    // Notify the adminViewModel about the selected geofence
                                    adminViewModel.onGeoFenceChanged(geofenceItem)
                                }
                            ) {
                                Text(text = geofenceItem.title ?: "Unknown")
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        TextFieldWithDropdown(
            value = timeVariation,
            onValueChange = {
                adminViewModel.onChangeTimeVariation(it)
            },
            valueError = timeVariationError,
            unit = selectedTimeVariationUnit,
            onUnitChange = {
                adminViewModel.onChangeTimeVariationUnit(it)
            },
            label = "Time Variation"
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
        if(selectedRole=="Admin") {
            CustomOutlinedTextFiled(
                value = selectedPermissions.joinToString(", "),
                onValueChange = {
                },
                label = "Select Permission for Admin",
                singleLine = false,
                isError = selectedPermissions.isEmpty(),
                readOnly = true,
                modifier = Modifier.clickable { showDialog = true } // Open dialog on click
                    .fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.height(40.dp))


        CustomButton(
            onClick = {
                if(!nameError && !phoneNumberError && !roleError && !salaryError && !regDateError && !timeVariationError && !leaveDaysError && (selectedRole=="Employee" || !selectedPermissions.isEmpty())) {

                    adminViewModel.onButtonStateChange(true)
                    scope.launch {
                        adminViewModel.onChangeProgressState(true)
                        adminViewModel.savePermissions(
                            firmName = selectedFirmName,
                            phoneNumber = phoneNumber,
                            onSuccess = {
                                Toast.makeText(context, "Permissions Saved", Toast.LENGTH_SHORT).show()
                            },
                            onFailure = {
                                Toast.makeText(context, "Failed to Save", Toast.LENGTH_SHORT).show()
                            }
                        )
                        adminViewModel.addEmployee(
                            onSuccess = {
                                Toast.makeText(
                                    context,
                                    "Registration SuccessFul",
                                    Toast.LENGTH_SHORT
                                ).show()
                                adminViewModel.onChangeProgressState(false)
                                allEmployeeViewModel.loadAllEmployee(employeeIdentity.firmName.toString())
                                val notification = MyNotification(
                                    context = context,
                                    title = "Firm Management App",
                                    message = "Congratulations $name Register as $selectedRole"
                                )

                                notification.fireNotification()
                            },
                            onFailure = {
                                Toast.makeText(context, "Registration Failed", Toast.LENGTH_LONG)
                                    .show()
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
                }else{
                    Toast.makeText(context, "Please fill in all the required fields.", Toast.LENGTH_SHORT).show()
                }
            },
            text = "Register",
            modifier = Modifier.width(100.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val isRegistered = allEmployees.any { it.phoneNumber == phoneNumber }
            CustomButton(
                onClick = {
                    if(!nameError && !phoneNumberError && !roleError && !salaryError && !regDateError && !timeVariationError && !leaveDaysError){
                        // naviage to add holiday
                        if (isRegistered) {
                            // Navigate to the HolidayTabMenu
                            val selectedEmployee = ViewAllEmployeeDataClass(
                                name = name,
                                phoneNumber = phoneNumber,
                                role = role,
                                isSelected = true
                            )
                            val selectedEmployees = setOf(selectedEmployee)
                            val employeesJson = Gson().toJson(selectedEmployees.toList()) // Convert to JSON for safe navigation
                            navController.navigate(ScreenAdmin.HolidayTabMenu.route + "/$employeesJson")

                        } else {
                            // Show toast if not registered
                            Toast.makeText(context, "Employee not registered. Please register first.", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        // toast
                        Toast.makeText(context, "Please fill in all the required fields.", Toast.LENGTH_SHORT).show()
                    }
                },
                text = "Add Holidays"
            )

            CustomButton(
                onClick = {
                    if(!nameError && !phoneNumberError && !roleError && !salaryError && !regDateError && !timeVariationError && !leaveDaysError ){
                        // naviage to work hours
                        if (isRegistered) {
                            // Navigate to the HolidayTabMenu
                            val selectedEmployee = ViewAllEmployeeDataClass(
                                name = name,
                                phoneNumber = phoneNumber,
                                role = role,
                                isSelected = true
                            )
                            val selectedEmployees = setOf(selectedEmployee)
                            val employeesJson = Gson().toJson(selectedEmployees.toList()) // Convert to JSON
                            navController.navigate(ScreenAdmin.WorkHours.route + "/$employeesJson")
                        } else {
                            // Show toast if not registered
                            Toast.makeText(context, "Employee not registered. Please register first.", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        // toast
                        Toast.makeText(context, "Please fill in all the required fields.", Toast.LENGTH_SHORT).show()
                    }
                },
                text = "Add Work Time"
            )
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Select Permissions") },
            text = {
                Column {
                    permissionsList.forEach { permission ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { adminViewModel.togglePermission(permission) }
                                .padding(8.dp)
                        ) {
                            Checkbox(
                                checked = selectedPermissions.contains(permission),
                                onCheckedChange = { adminViewModel.togglePermission(permission) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = permission)
                        }
                    }
                }
            },
            confirmButton = {
                CustomButton(
                    onClick = {
                        showDialog = false
                    },
                    text="Save"
                )
            },
            dismissButton = {
                CustomButton(
                    onClick = {
                        showDialog = false

                    },
                    text="Cancel"
                )

            }
        )
    }
}

@Composable
fun TextFieldWithDropdown(
    value: String,
    onValueChange: (String) -> Unit,
    valueError: Boolean,
    unit: String,
    label:String,
    onUnitChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val dropdownOptions = listOf("Per Hour", "Per Day", "Per Month")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Color.Gray,
                shape = RoundedCornerShape(6)
            )
    ) {

        CustomOutlinedTextFiled(
            value = "$value $unit",
            onValueChange = { onValueChange(it) },
            label = label,
            singleLine = true,
            isError = valueError,
            readOnly = false,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            ),
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Box {
            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Expand"
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                offset = DpOffset(x = 0.dp, y = 0.dp)
            ) {
                dropdownOptions.forEach { option ->
                    DropdownMenuItem(
                        onClick = {
                            onUnitChange(option)
                            expanded = false
                        }
                    ) {
                        Text(text = option)
                    }
                }
            }
        }
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
        shape = RoundedCornerShape(12.dp),
        colors= ButtonDefaults.buttonColors(
            blueAcha
        )
    ) {
        Text(
            text = text,
            style = fontBablooBold,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AddStaffPreview() {
    FirmManagementTheme {
        //AddStaff()
    }
}