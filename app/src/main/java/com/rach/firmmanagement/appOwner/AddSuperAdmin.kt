package com.rach.firmmanagement.appOwner

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rach.firmmanagement.dataClassImp.NoAdminDataClass
import com.rach.firmmanagement.necessaryItem.CustomOutlinedTextFiled
import com.rach.firmmanagement.ui.theme.blueAcha
import com.rach.firmmanagement.ui.theme.fontBablooBold
import com.rach.firmmanagement.viewModel.AppOwnerViewModel
import kotlinx.coroutines.launch
/*
@Composable
fun AddSuperAdmin(
    viewModel: AppOwnerViewModel = viewModel()
){
    // Add Super Admin Screen

    val scope = rememberCoroutineScope()

    val context = LocalContext.current


    val firmName by viewModel.firmName.collectAsState()


    val ownerName by viewModel.ownerName.collectAsState()

    val phoneNumber by viewModel.phoneNumber.collectAsState()

    val areaPinCode by viewModel.areaPincode.collectAsState()

    val address by viewModel.address.collectAsState()

    val buttonClicked by viewModel.buttonClicked.collectAsState()

    val isNameError = buttonClicked && firmName.isEmpty()
    val phoneNumberError = buttonClicked && phoneNumber.isEmpty()
    val ownerNameError = buttonClicked && ownerName.isEmpty()
    val areaPinCodeError = buttonClicked && areaPinCode.isEmpty()
    val AddressError = buttonClicked && address.isEmpty()

    val progressBarState by viewModel.progressBarState.collectAsState()


    if (progressBarState){
        Box(modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center){
            CircularProgressIndicator()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 22.dp, end = 22.dp, top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        CustomOutlinedTextFiled(
            value = firmName,
            onValueChange = {

                viewModel.onChangeFirmName(it)

            }, label = "Enter Firm Name",
            isError = isNameError, singleLine = false,
            readOnly = false
        )

        Spacer(modifier = Modifier.height(8.dp))

        CustomOutlinedTextFiled(
            value = phoneNumber,
            onValueChange = {
                viewModel.onChangePhoneNumber(it)
            },
            label = "Phone Number",
            singleLine = true,
            isError = phoneNumberError,
            readOnly = false

        )

        Spacer(modifier = Modifier.height(8.dp))

        CustomOutlinedTextFiled(
            value = ownerName,
            onValueChange = {
                viewModel.onChangeownerName(it)
            },
            label = "Owner Name",
            singleLine = false,
            isError = ownerNameError,
            readOnly = false
        )

        Spacer(modifier = Modifier.height(8.dp))

        CustomOutlinedTextFiled(
            value = areaPinCode,
            onValueChange = {
                viewModel.onChangeAreaPinCode(it)
            },
            label = "Area Pincode",
            singleLine = true,
            isError = areaPinCodeError,
            readOnly = false
        )

        Spacer(modifier = Modifier.height(8.dp))

        CustomOutlinedTextFiled(
            value = address,
            onValueChange = {
                viewModel.onAddressChange(it)
            },
            label = "Address",
            singleLine = false,
            isError = AddressError,
            readOnly = false
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                viewModel.onButtonClickedStateChange(true)
                scope.launch {
                    viewModel.onChangeState(true)
                    viewModel.addFirm(
                        onSuccess = {
                            viewModel.onChangeState(false)
                            Toast.makeText(context, "Added", Toast.LENGTH_LONG).show()
                        },
                        onFailure = {
                            viewModel.onChangeState(false)
                            Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_LONG)
                                .show()
                        }
                    )
                }

            },
            colors = ButtonDefaults.buttonColors(
                blueAcha
            )
        ) {

            androidx.compose.material3.Text(text = "Add Firm", style = fontBablooBold)

        }

    }
}
*/

@Composable
fun FirmDetailsScreen(
    firm: NoAdminDataClass,
    onFirmChange: (NoAdminDataClass) -> Unit
) {

    Column(
        modifier = Modifier
            .padding(start = 22.dp, end = 22.dp, top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CustomOutlinedTextFiled(
            value = firm.firmName.toString(),
            onValueChange = { onFirmChange(firm.copy(firmName = it)) },
            label = "Enter Firm Name",
            isError = firm.firmName.isNullOrEmpty(),
            singleLine = false,
            readOnly = false
        )

        Spacer(modifier = Modifier.height(8.dp))

        CustomOutlinedTextFiled(
            value = firm.phoneNumber.toString(),
            onValueChange = { onFirmChange(firm.copy(phoneNumber = it)) },
            label = "Phone Number",
            singleLine = true,
            isError = firm.phoneNumber.isNullOrEmpty(),
            readOnly = false
        )

        Spacer(modifier = Modifier.height(8.dp))

        CustomOutlinedTextFiled(
            value = firm.ownerName.toString(),
            onValueChange = { onFirmChange(firm.copy(ownerName = it)) },
            label = "Owner Name",
            singleLine = false,
            isError = firm.ownerName.isNullOrEmpty(),
            readOnly = false
        )

        Spacer(modifier = Modifier.height(8.dp))

        CustomOutlinedTextFiled(
            value = firm.pinCode.toString(),
            onValueChange = { onFirmChange(firm.copy(pinCode = it)) },
            label = "Area Pincode",
            singleLine = true,
            isError = firm.pinCode.isNullOrEmpty(),
            readOnly = false
        )

        Spacer(modifier = Modifier.height(8.dp))

        CustomOutlinedTextFiled(
            value = firm.address.toString(),
            onValueChange = { onFirmChange(firm.copy(address = it)) },
            label = "Address",
            singleLine = false,
            isError = firm.address.isNullOrEmpty(),
            readOnly = false
        )
    }
}

@Composable
fun AddSuperAdmin(viewModel: AppOwnerViewModel = viewModel()) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val firm = NoAdminDataClass(
        firmName = viewModel.firmName.collectAsState().value,
        ownerName = viewModel.ownerName.collectAsState().value,
        phoneNumber = viewModel.phoneNumber.collectAsState().value,
        pinCode = viewModel.areaPincode.collectAsState().value,
        address = viewModel.address.collectAsState().value
    )

    val progressBarState by viewModel.progressBarState.collectAsState()

    if (progressBarState){
        Box(modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center){
            CircularProgressIndicator()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 22.dp, end = 22.dp, top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FirmDetailsScreen(
            firm = firm,
            onFirmChange = { updatedFirm ->
                viewModel.onChangeFirmName(updatedFirm.firmName.toString())
                viewModel.onChangeownerName(updatedFirm.ownerName.toString())
                viewModel.onChangePhoneNumber(updatedFirm.phoneNumber.toString())
                viewModel.onChangeAreaPinCode(updatedFirm.pinCode.toString())
                viewModel.onAddressChange(updatedFirm.address.toString())
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                viewModel.onButtonClickedStateChange(true)
                scope.launch {
                    viewModel.onChangeState(true)
                    viewModel.addFirm(
                        onSuccess = {
                            viewModel.onChangeState(false)
                            Toast.makeText(context, "Added", Toast.LENGTH_LONG).show()
                        },
                        onFailure = {
                            viewModel.onChangeState(false)
                            Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_LONG)
                                .show()
                        }
                    )
                }
            },
            colors = ButtonDefaults.buttonColors(blueAcha)
        ) {
            androidx.compose.material3.Text(text = "Add Firm", style = fontBablooBold)
        }
    }
}
