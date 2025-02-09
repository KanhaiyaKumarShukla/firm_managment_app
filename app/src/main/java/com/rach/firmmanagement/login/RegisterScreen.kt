package com.rach.firmmanagement.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rach.firmmanagement.R
import com.rach.firmmanagement.necessaryItem.LoadProgressBar
import com.rach.firmmanagement.necessaryItem.RadioButton1
import com.rach.firmmanagement.ui.theme.FirmManagementTheme
import com.rach.firmmanagement.ui.theme.blueAcha
import com.rach.firmmanagement.ui.theme.fontBablooBold
import com.rach.firmmanagement.ui.theme.red
import com.rach.firmmanagement.viewModel.LoginViewModel

@Composable
fun RegisterScreen(
    viewModel: LoginViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    navigateToPhoneNumber:() -> Unit
) {

    val gender = listOf(
        "Employee",
        "Employer"
    )

    val context  = LocalContext.current

    val genderState by viewModel.registerGenderState.collectAsState()

    val checkedBoxState by viewModel.isCheckedBoxCheck.collectAsState()

    val scrollState = rememberScrollState()

    val name by viewModel.registerName.collectAsState()

    val mobileNumber by viewModel.registerMobileNumber.collectAsState()

    val email by viewModel.registerEmail.collectAsState()

    val firmName by viewModel.registerFirmName.collectAsState()

    val address by viewModel.registeredAddress.collectAsState()

    val city by viewModel.cityName.collectAsState()

    val pinCode by viewModel.pincode.collectAsState()

    val progressBarState by viewModel.progressBarState.collectAsState()

    val terms = buildAnnotatedString {
        append("By continuing you accept our ")
        withStyle(
            style = SpanStyle(
                color = colorResource(id = R.color.GrayColor),
                textDecoration = TextDecoration.Underline
            )
        ) {

            append("Privacy Policy ")

        }
        append("and ")
        withStyle(
            style = SpanStyle(
                color = colorResource(id = R.color.GrayColor),
                textDecoration = TextDecoration.Underline
            )
        ) {
            append("Terms of Use")
        }
    }





    if (progressBarState){

        Box(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
        ) {
            CircularProgressIndicator(
                color = blueAcha,
                strokeWidth = 4.dp
            )
        }

    }else{
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 22.dp, end = 22.dp,
                    top = 60.dp
                )
                .verticalScroll(scrollState)
                .padding(bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(15.dp))
            Text(
                text = "Register Your Account", style = fontBablooBold,
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.heightIn(15.dp))


            OutlinedTextField(
                value = name, onValueChange = {
                    viewModel.onChangeRegisterName(it)
                },
                modifier = Modifier
                    .fillMaxWidth(),
                label = { Text(text = "Enter Name") }

            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(value = mobileNumber, onValueChange = {
                viewModel.onChangeRegisterMobileNumber(it)
            },

                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Mobile Number") })

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(value = email, onValueChange = {
                viewModel.onChangeRegisterEmail(it)
            },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Enter Email") })

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(value = firmName, onValueChange = {
                viewModel.onChangeFirmName(it)
            },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Firm Name") })

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(value = address, onValueChange = {
                viewModel.onChangeAddress(it)
            },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Address") })

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(value = city, onValueChange = {
                viewModel.onChangeCityName(it)
            },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "City") })

            Spacer(modifier = Modifier.height(10.dp))


            OutlinedTextField(value = pinCode, onValueChange = {
                viewModel.onChangePinCode(it)
            },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "PinCode") })

            Spacer(modifier = Modifier.height(20.dp))

            Row {
                Checkbox(
                    checked = checkedBoxState, onCheckedChange = {
                        viewModel.onCheckBox(it)
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color.Black,
                        uncheckedColor = Color.Black
                    )
                )

                Text(
                    text = terms, fontSize = 16.sp,
                    modifier = Modifier.align(CenterVertically)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {

                    if (mobileNumber.isEmpty() && !checkedBoxState){
                        Toast.makeText(context,"Please Fill Mobile Number \nand Click Check Box",Toast.LENGTH_LONG).show()

                    }else{
                        viewModel.onProgressBarState(true)
                        val role = genderState
                        viewModel.saveUserRegisterData(
                            role = role,
                            name = name,
                            mobileNumber = mobileNumber,
                            email = email,
                            firmName = firmName,
                            address = address,
                            city = city,
                            pinCode = pinCode,
                            onSuccess = {

                                viewModel.onProgressBarState(false)
                                Toast.makeText(context,"Registration SuccessFul",Toast.LENGTH_SHORT).show()
                                navigateToPhoneNumber()

                            },
                            onFailure = {
                                viewModel.onProgressBarState(false)
                                Toast.makeText(context,"Registration Failed",Toast.LENGTH_SHORT).show()
                            }

                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    blueAcha
                )
            ) {

                Text(
                    text = "Register", style = fontBablooBold,
                    fontSize = 16.sp,
                    color = Color.White
                )

            }

            Spacer(modifier = Modifier.height(20.dp))
            Row {

                Text(
                    text = "Already have an account ? ",
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(7.dp))
                Text(
                    text = "Log-in",
                    fontSize = 16.sp,
                    textDecoration = TextDecoration.Underline,
                    color = colorResource(id = R.color.GrayColor),
                    modifier = Modifier.clickable {
                        navigateToPhoneNumber()
                    }
                )
            }


        }
    }

}

@Preview(showBackground = true)
@Composable
fun RegisterPreview() {
    FirmManagementTheme {
        RegisterScreen(viewModel = LoginViewModel(),{})
    }
}
