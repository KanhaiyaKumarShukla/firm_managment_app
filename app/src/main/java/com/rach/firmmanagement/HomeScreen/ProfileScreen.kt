package com.rach.firmmanagement.HomeScreen

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import java.util.Calendar
import android.util.Log
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material3.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rach.firmmanagement.R
import com.rach.firmmanagement.ui.theme.Black
import com.rach.firmmanagement.ui.theme.FirmManagementTheme
import com.rach.firmmanagement.ui.theme.fontBablooBold
import com.rach.firmmanagement.viewModel.LoginViewModel
import com.rach.firmmanagement.viewModel.ProfileViewModel
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.DpOffset
import com.rach.firmmanagement.HomeScreen.ProfileOutlinedTextField
import com.rach.firmmanagement.dataClassImp.AddStaffDataClass
import com.rach.firmmanagement.dataClassImp.GeofenceItems
import com.rach.firmmanagement.dataClassImp.ViewAllEmployeeDataClass
import com.rach.firmmanagement.ui.theme.blueAcha
import com.rach.firmmanagement.viewModel.AdminViewModel
import com.rach.firmmanagement.viewModel.AllEmployeeViewModel
import com.rach.firmmanagement.viewModel.GeofenceViewModel


@Composable
fun ProfileScreen(
    loginViewModel: LoginViewModel,
    profileViewModel: ProfileViewModel = viewModel()
) {
    val role by loginViewModel.selectGenderState.collectAsState()

    val isLoading by profileViewModel.isLoading

    val name by profileViewModel.name.collectAsState()
    val phoneNumber by profileViewModel.phoneNumber.collectAsState()
    val firmName by profileViewModel.firmName.collectAsState()
    val address by profileViewModel.address.collectAsState()
    val email by profileViewModel.email.collectAsState()

    //change
    var isDialogOpen by remember { mutableStateOf(false) }
    var fieldToEdit by remember { mutableStateOf("") }
    var updatedValue by remember { mutableStateOf("") }
    //change end

    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = role) {
        scope.launch {
            profileViewModel.loadProfile(role)
        }
    }

    //change
    if (isDialogOpen) {
        EditDialog(
            fieldName = fieldToEdit,
            initialValue = updatedValue,
            onConfirm = {
                when (fieldToEdit) {
                    "Name" -> profileViewModel.onChangeName(it)
                    "PhoneNumber" -> profileViewModel.onChangePhoneNumber(it)
                    "Firm Name" -> profileViewModel.onChangeFirmName(it)
                    "Address" -> profileViewModel.onChangeAddress(it)
                    "Email" -> profileViewModel.onChangeEmail(it)
                }
                profileViewModel.updateProfile(role)
                isDialogOpen = false
            },
            onDismiss = {
                isDialogOpen = false
            }
        )
    }
    // change end

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material.CircularProgressIndicator(
                    color = blueAcha,
                    strokeWidth = 4.dp
                )
            }

        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = 20.dp, end = 20.dp,
                        top = 20.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "logo",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Text(text = name, fontSize = 20.sp, style = fontBablooBold)

                Spacer(modifier = Modifier.height(15.dp))

                // Name Field
                ProfileOutlinedTextField(value = name,
                    label = "Name",
                    //change
                    // onValueChange = { profileViewModel.onChangeName(it) },
                    onValueChange = {updatedValue = it},
                    isEditing = false,
                    onEditClick = {
                        fieldToEdit = "Name"
                        updatedValue = name
                        isDialogOpen = true
                    })

                Spacer(modifier = Modifier.height(8.dp))

                // PhoneNumber Field
                ProfileOutlinedTextField(value = phoneNumber,
                    label = "PhoneNumber",
                    // change
                    // onValueChange = { profileViewModel.onChangePhoneNumber(it) },
                    onValueChange = { updatedValue = it },
                    isEditing = false,
                    onEditClick = {
                        fieldToEdit = "PhoneNumber"
                        updatedValue = phoneNumber
                        isDialogOpen = true
                    })

                Spacer(modifier = Modifier.height(8.dp))

                // Firm Name Field
                ProfileOutlinedTextField(value = firmName,
                    label = "Firm Name",
                    //onValueChange = { profileViewModel.onChangeFirmName(it) },
                    onValueChange = { updatedValue = it },
                    isEditing = false,
                    onEditClick = {
                        fieldToEdit = "Firm Name"
                        updatedValue = firmName
                        isDialogOpen = true
                    })

                Spacer(modifier = Modifier.height(8.dp))

                // Address Field
                ProfileOutlinedTextField(value = address, label = "Address",
                    //onValueChange = { profileViewModel.onChangeAddress(it) },
                    onValueChange = { updatedValue = it },
                    isEditing = false,
                    onEditClick = {
                        fieldToEdit = "Address"
                        updatedValue = address
                        isDialogOpen = true
                    })

                Spacer(modifier = Modifier.height(8.dp))

                // Date of Birth Field
                ProfileOutlinedTextField(value = email, label = "Email",
                    // onValueChange = { profileViewModel.onChangeEmail(it) },
                    onValueChange = { updatedValue = it },
                    isEditing = false,
                    onEditClick = {
                        fieldToEdit = "Email"
                        updatedValue = email
                        isDialogOpen = true
                    }
                )
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun EmployeeProfileEditableScreen(
    adminViewModel: AdminViewModel=viewModel(),
    profileViewModel: ProfileViewModel,
    employee: AddStaffDataClass,
    geofenceViewModel: GeofenceViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

){

    val isProfileLoading by adminViewModel.isProfileLoading
    val staffName by adminViewModel.staffName.collectAsState()
    val staffPhoneNumber by adminViewModel.staffNewPhoneNumber.collectAsState()
    val staffRole by adminViewModel.staffRole.collectAsState()
    val staffSalary by adminViewModel.staffSalary.collectAsState()
    val staffRegistrationDate by adminViewModel.staffRegistrationDate.collectAsState()
    val staffTimeVariation by adminViewModel.staffTimeVariation.collectAsState()
    val staffLeaveDays by adminViewModel.staffLeaveDays.collectAsState()
    val staffWorkPlace by adminViewModel.staffWorkPlace.collectAsState()

    var isDialogOpen by remember { mutableStateOf(false) }
    var isDialogForWorkPlaceOpen by remember { mutableStateOf(false) }
    var fieldToEdit by remember { mutableStateOf("") }
    var updatedValue by remember { mutableStateOf("") }
    var isDatePickerOpen by remember { mutableStateOf(false) } // State for date picker

    val geofences by geofenceViewModel.geofences.collectAsState()

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            adminViewModel.loadStaff(employee.phoneNumber.toString())
            geofenceViewModel.fetchAllGeofences(employee.firmName.toString())
        }
    }

    if (isDatePickerOpen) {
        val calendar = Calendar.getInstance()

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val context = LocalContext.current

        DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDay ->
                // Format the selected date
                val selectedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
                adminViewModel.onChangeStaffRegistrationDate(selectedDate)
                adminViewModel.updateStaff(employee.phoneNumber.toString())
                isDatePickerOpen = false
            },
            year,
            month,
            day
        ).show()
    }


    //change
    if (isDialogOpen) {
        EditDialog(
            fieldName = fieldToEdit,
            initialValue = updatedValue,
            onConfirm = {
                when (fieldToEdit) {
                    "Name" -> adminViewModel.onChangeStaffName(it)
                    "PhoneNumber" -> adminViewModel.onChangeStaffPhoneNumber(it)
                    "Role" -> adminViewModel.onChangeStaffRole(it)
                    "Salary" -> adminViewModel.onChangeStaffSalary(it)
                    "RegistrationDate" -> adminViewModel.onChangeStaffRegistrationDate(it)
                    "TimeVariation" -> adminViewModel.onChangeStaffTimeVariation(it)
                    "LeaveDays" -> adminViewModel.onChangeStaffLeaveDays(it)
                }
                adminViewModel.updateStaff(employee.phoneNumber.toString())
                isDialogOpen = false
            },
            onDismiss = {
                isDialogOpen = false
            }
        )
    }
    if(isDialogForWorkPlaceOpen){
        EditDialogForGeoFence(
            fieldName="WorkPlace",
            initialValue = staffWorkPlace,
            geofences = geofences,
            onConfirm = {
                adminViewModel.onChangeStaffWorkPlace(it)
                adminViewModel.updateStaff(employee.phoneNumber.toString())
                isDialogForWorkPlaceOpen = false
            },
            onDismiss = {
                isDialogForWorkPlaceOpen = false
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        if (isProfileLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = 20.dp, end = 20.dp,
                        top = 20.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.about),
                    contentDescription = "logo",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Text(text = staffName, fontSize = 20.sp, style = fontBablooBold)

                Spacer(modifier = Modifier.height(15.dp))

                // Name Field
                ProfileOutlinedTextField(value = staffName,
                    label = "Name",
                    //change
                    // onValueChange = { profileViewModel.onChangeName(it) },
                    onValueChange = { updatedValue = it },
                    isEditing = false,
                    onEditClick = {
                        fieldToEdit = "Name"
                        updatedValue = staffName
                        isDialogOpen = true
                    })

                Spacer(modifier = Modifier.height(8.dp))

                // PhoneNumber Field
                ProfileOutlinedTextField(value = staffPhoneNumber,
                    label = "PhoneNumber",
                    // change
                    // onValueChange = { profileViewModel.onChangePhoneNumber(it) },
                    onValueChange = { updatedValue = it },
                    isEditing = false,
                    onEditClick = {
                        fieldToEdit = "PhoneNumber"
                        updatedValue = staffPhoneNumber
                        isDialogOpen = true
                    })

                Spacer(modifier = Modifier.height(8.dp))

                // Firm Name Field
                ProfileOutlinedTextField(value = staffRole,
                    label = "Role",
                    //onValueChange = { profileViewModel.onChangeFirmName(it) },
                    onValueChange = { updatedValue = it },
                    isEditing = false,
                    onEditClick = {
                        fieldToEdit = "Role"
                        updatedValue = staffRole
                        isDialogOpen = true
                    })

                Spacer(modifier = Modifier.height(8.dp))

                // Address Field
                ProfileOutlinedTextField(value = staffSalary, label = "Salary",
                    //onValueChange = { profileViewModel.onChangeAddress(it) },
                    onValueChange = { updatedValue = it },
                    isEditing = false,
                    onEditClick = {
                        fieldToEdit = "Salary"
                        updatedValue = staffSalary
                        isDialogOpen = true
                    })

                Spacer(modifier = Modifier.height(8.dp))

                // Date of Birth Field
                ProfileOutlinedTextField(value = staffRegistrationDate, label = "RegistrationDate",
                    // onValueChange = { profileViewModel.onChangeEmail(it) },
                    onValueChange = { updatedValue = it },
                    isEditing = false,
                    onEditClick = {
                        fieldToEdit = "RegistrationDate"
                        updatedValue = staffRegistrationDate
                        isDatePickerOpen = true
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Date of Birth Field
                ProfileOutlinedTextField(value = staffTimeVariation, label = "TimeVariation",
                    // onValueChange = { profileViewModel.onChangeEmail(it) },
                    onValueChange = { updatedValue = it },
                    isEditing = false,
                    onEditClick = {
                        fieldToEdit = "TimeVariation"
                        updatedValue = staffTimeVariation
                        isDialogOpen = true
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Date of Birth Field
                ProfileOutlinedTextField(value = staffLeaveDays, label = "LeaveDays",
                    // onValueChange = { profileViewModel.onChangeEmail(it) },
                    onValueChange = { updatedValue = it },
                    isEditing = false,
                    onEditClick = {
                        fieldToEdit = "LeaveDays"
                        updatedValue = staffLeaveDays
                        isDialogOpen = true
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                ProfileOutlinedTextField(value = staffWorkPlace.title.toString(), label = "WorkPlace",
                    onValueChange = { },
                    isEditing = false,
                    onEditClick = {
                        fieldToEdit = "WorkPlace"
                        //updatedValue = staffWorkPlace
                        isDialogForWorkPlaceOpen = true
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))

            }
        }
    }


}

// change
@Composable
fun EditDialog(
    fieldName: String,
    initialValue: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var textFieldValue by remember { mutableStateOf(initialValue) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Add $fieldName") },
        text = {
            Column {
                OutlinedTextField(
                    value = textFieldValue,
                    onValueChange = { textFieldValue = it },
                    label = { Text(text = fieldName) }
                )
            }
        },
        buttons = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp), // Add margin around buttons
                horizontalArrangement = Arrangement.End // Align buttons to the end
            ) {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .height(40.dp)
                        .padding(end = 8.dp), // Add spacing between buttons
                    shape = RoundedCornerShape(7.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                ) {
                    Text("Cancel", fontSize = 12.sp, color = Color.White)
                }
                Button(
                    onClick = { onConfirm(textFieldValue) },
                    modifier = Modifier.height(40.dp),
                    shape = RoundedCornerShape(7.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                ) {
                    Text("Save", fontSize = 12.sp, color = Color.White)
                }
            }
        }
    )

}
// change end
@Composable
fun EditDialogForGeoFence(
    fieldName: String,
    initialValue: GeofenceItems,
    geofences: List<GeofenceItems>, // Replace `GeofenceItem` with your actual model class
    onConfirm: (GeofenceItems) -> Unit,
    onDismiss: () -> Unit
) {
    var textFieldValue by remember { mutableStateOf(initialValue) }
    var expanded by remember { mutableStateOf(false) }
    var selectedGeofence by remember { mutableStateOf(GeofenceItems()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Add $fieldName") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedGeofence.title.toString(),
                        onValueChange = { },
                        label = { Text("Select Geofence") },
                        readOnly = true,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                expanded = true
                                Log.d("Geofence", "Geofence dropdown clicked: $geofences")
                            }
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
                                Log.d("Geofence", "Geofence item: $geofenceItem")
                                DropdownMenuItem(
                                    onClick = {
                                        selectedGeofence = geofenceItem ?: GeofenceItems()
                                        expanded = false
                                    }
                                ) {
                                    Text(text = geofenceItem.title ?: "Unknown")
                                }
                            }
                        }
                    }
                }
            }
        },
        buttons = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .height(40.dp)
                        .padding(end = 8.dp),
                    shape = RoundedCornerShape(7.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                ) {
                    Text("Cancel", fontSize = 12.sp, color = Color.White)
                }
                Button(
                    onClick = { onConfirm(selectedGeofence) },
                    modifier = Modifier.height(40.dp),
                    shape = RoundedCornerShape(7.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                ) {
                    Text("Save", fontSize = 12.sp, color = Color.White)
                }
            }
        }
    )
}



@Composable
fun ProfileOutlinedTextField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    isEditing: Boolean,
    onEditClick: () -> Unit
) {

    OutlinedTextField(
        value = value,
        onValueChange = {

            if (isEditing) {
                onValueChange(it)
            }
        },
        label = { Text(text = label, style = fontBablooBold) },
        textStyle = TextStyle(
            color = Black,
            fontSize = 15.sp,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        shape = RoundedCornerShape(10.dp),
        trailingIcon = {
            IconButton(onClick = { onEditClick() }) {

                Icon(
                    imageVector = if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                    contentDescription = "Save And Edit"
                )

            }
        },
        enabled = isEditing
    )

}

@Composable
fun AnotherRouter(){
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        
        Text(text = "Page is Under Maintenance")

    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScrenPreview() {
    FirmManagementTheme {
        ProfileScreen(loginViewModel = LoginViewModel())
    }
}