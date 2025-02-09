package com.rach.firmmanagement.HomeScreen

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.rach.firmmanagement.R
import com.rach.firmmanagement.necessaryItem.CustomOutlinedTextFiled
import com.rach.firmmanagement.viewModel.NoAdminViewModel
import com.rach.firmmanagement.ui.theme.FirmManagementTheme
import com.rach.firmmanagement.ui.theme.blueAcha
import com.rach.firmmanagement.ui.theme.fontBablooBold
import kotlinx.coroutines.launch

@Composable
fun NoAdmin(navigationToRaiseRequest: () -> Unit) {

    var isAddItemVisible by remember {
        mutableStateOf(false)
    }


    Box(modifier = Modifier.fillMaxSize()) {

        AnimatedVisibility(
            visible = isAddItemVisible,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(durationMillis = 1000)
            ),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(durationMillis = 1000)
            ),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {

            AddRequest()

        }

        FloatingActionButton(
            onClick = {
                isAddItemVisible = true
                navigationToRaiseRequest()
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = blueAcha,
            shape = RoundedCornerShape(25.dp)
        ) {

            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Icon Floating Action Button",
                tint = Color.White
            )


        }

        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.undraw_agreement_re_d4dv),
                contentDescription = "Ju",
                modifier = Modifier
                    .padding(bottom = 60.dp)
                    .padding(8.dp)
            )

            Text(text = "You are not registered Firm Owner", style = fontBablooBold)
            Text(text = "Please Fill Form And  ", style = fontBablooBold)
            Text(text = "Our Team Contacts in 24 hour", style = fontBablooBold)
        }


    }

}

@Composable
fun AddRequest(
    noAdminViewModel: NoAdminViewModel = viewModel()
) {

    val scope = rememberCoroutineScope()

    val context = LocalContext.current


    val firmName by noAdminViewModel.firmName.collectAsState()


    val ownerName by noAdminViewModel.ownerName.collectAsState()

    val phoneNumber by noAdminViewModel.phoneNumber.collectAsState()

    val areaPinCode by noAdminViewModel.areaPincode.collectAsState()

    val address by noAdminViewModel.address.collectAsState()

    val buttonClicked by noAdminViewModel.buttonClicked.collectAsState()

    val isNameError = buttonClicked && firmName.isEmpty()
    val phoneNumberError = buttonClicked && phoneNumber.isEmpty()
    val ownerNameError = buttonClicked && ownerName.isEmpty()
    val areaPinCodeError = buttonClicked && areaPinCode.isEmpty()
    val AddressError = buttonClicked && address.isEmpty()

    val progressBarState by noAdminViewModel.progressBarState.collectAsState()


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

                noAdminViewModel.onChangeFirmName(it)

            }, label = "Enter Firm Name",
            isError = isNameError, singleLine = false,
            readOnly = false
        )

        Spacer(modifier = Modifier.height(8.dp))

        CustomOutlinedTextFiled(
            value = phoneNumber,
            onValueChange = {
                noAdminViewModel.onChangePhoneNumber(it)
            },
            label = "Phone Number",
            singleLine = true,
            isError = phoneNumberError,
            readOnly = true

        )

        Spacer(modifier = Modifier.height(8.dp))

        CustomOutlinedTextFiled(
            value = ownerName,
            onValueChange = {
                noAdminViewModel.onChangeownerName(it)
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
                noAdminViewModel.onChangeAreaPinCode(it)
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
                noAdminViewModel.onAddressChange(it)
            },
            label = "Address",
            singleLine = false,
            isError = AddressError,
            readOnly = false
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                noAdminViewModel.onButtonClickedStateChange(true)
                scope.launch {
                    noAdminViewModel.onChangeState(true)
                    noAdminViewModel.raiseRequest(
                        onSuccess = {
                            noAdminViewModel.onChangeState(false)
                            Toast.makeText(context, "Added", Toast.LENGTH_LONG).show()
                        },
                        onFailure = {
                            noAdminViewModel.onChangeState(false)
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

            Text(text = "Add Firm", style = fontBablooBold)

        }

    }


}

@Preview(showBackground = true)
@Composable
fun NoAdminPreview() {
    FirmManagementTheme {
        AddRequest(noAdminViewModel = NoAdminViewModel())
    }
}