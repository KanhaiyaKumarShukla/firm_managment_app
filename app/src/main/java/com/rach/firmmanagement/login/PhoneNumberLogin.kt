package com.rach.firmmanagement.login

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.api.Context
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import com.rach.firmmanagement.R
import com.rach.firmmanagement.necessaryItem.RadioButton1
import com.rach.firmmanagement.ui.theme.FirmManagementTheme
import com.rach.firmmanagement.ui.theme.blueAcha
import com.rach.firmmanagement.ui.theme.fontBablooBold
import com.rach.firmmanagement.ui.theme.progressBarBgColor
import com.rach.firmmanagement.viewModel.LoginViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@Composable
fun PhoneNumberLogin(
    navigateToOtp: () -> Unit,
    navigateToRegister: () -> Unit,
    viewModel: LoginViewModel
) {

    val context = LocalContext.current

    val phoneNumber by viewModel.phoneNumberInput.collectAsState()

    val ownerPhoneNumber by viewModel.firmOwnerNumber.collectAsState()

    val progressBarState by viewModel.checkLoginedProgressBar.collectAsState()

    val scope = rememberCoroutineScope()

    var buttonAlpha by remember {
        mutableFloatStateOf(0.7f)
    }

    val gender = listOf(
        "Employee",
        "Employer"
    )

    val genderState by viewModel.selectGenderState.collectAsState()

    val textAccept = buildAnnotatedString {
        append("By continuing you accept our ")
        withStyle(
            style = SpanStyle(
                color = colorResource(id = R.color.GrayColor),
                textDecoration = TextDecoration.Underline
            )
        ) {
            append("Privacy Policy ")
        }
        append("and")
    }

    val textTerms = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                color = colorResource(id = R.color.GrayColor),
                textDecoration = TextDecoration.Underline
            )
        ) {
            append("Terms of Use")
        }
    }




    Box(modifier = Modifier.fillMaxSize()
        .background(Color.White)){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 22.dp, end = 22.dp,
                    top = 60.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {




            Image(
                painter = painterResource(id = R.drawable.logo), contentDescription = "Logo",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.heightIn(15.dp))


            OutlinedTextField(
                value = phoneNumber,
                onValueChange = {
                    viewModel.onPhoneNumberChanged(it)
                },
                modifier = Modifier
                    .fillMaxWidth(),
                label = { Text(text = "Enter Phone Number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)

            )

            Spacer(modifier = Modifier.heightIn(10.dp))

            if (genderState == "Employee") {
                OutlinedTextField(
                    value = ownerPhoneNumber, onValueChange = {
                        viewModel.onFirmOwnerChangedNumber(it)
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    label = { Text(text = "Enter Firm Owner Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)


                )
            }

            Spacer(modifier = Modifier.heightIn(15.dp))

            Button(
                onClick = {
                    //if(phoneNumber.isEmpty() and isValidPhoneNumber(phoneNumber)) {
                        scope.launch {
                            buttonAlpha = 1.0f
                            viewModel.OnChangeProgressBar(true)
                            viewModel.checkLoginOrNot(
                                role = genderState,
                                mobileNumber = phoneNumber,
                                onSuccess = {
                                    viewModel.saveOrUpdateData()
                                    navigateToOtp()
                                    viewModel.OnChangeProgressBar(false)
                                },
                                onFailure = {
                                    Toast.makeText(
                                        context,
                                        "You are not registered",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    viewModel.OnChangeProgressBar(false)
                                    navigateToRegister()
                                }
                            )
                        }
                    //}else{
                       // Toast.makeText(context, "Invalid Phone Number", Toast.LENGTH_SHORT).show()
                    //}
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(buttonAlpha),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    blueAcha
                )

            ) {

                Text(text = "SEND OTP", style = fontBablooBold)

            }

            Spacer(modifier = Modifier.height(15.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "Don't have Account yet?")
                Spacer(modifier = Modifier.width(15.dp))
                Text(
                    text = "Register",
                    style = fontBablooBold,
                    fontSize = 18.sp,
                    color = colorResource(id = R.color.GrayColor),
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable {
                        navigateToRegister()
                    }
                )
            }

            Spacer(modifier = Modifier.height(15.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                gender.forEach {
                    RadioButton1(isSelected = genderState == it, title = it) { data ->

                        viewModel.onGenderChanged(data)

                    }
                }


            }

            Spacer(modifier = Modifier.height(35.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = textAccept, fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = textTerms, fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Medium,
                    color = colorResource(id = R.color.GrayColor)
                )
            }




        }

        if (progressBarState) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(progressBarBgColor.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }

}

private fun isValidPhoneNumber(phoneNumber: String, countryCode: String = "+91"): Boolean {
    val phoneUtil = PhoneNumberUtil.getInstance()
    return try {
        val numberProto: Phonenumber.PhoneNumber = phoneUtil.parse(phoneNumber, countryCode)
        phoneUtil.isValidNumber(numberProto)
    } catch (e: Exception) {
        false
    }
}

@Preview(showBackground = true)
@Composable
fun PhonePreview() {
    FirmManagementTheme {
        PhoneNumberLogin({}, {}, viewModel = LoginViewModel())
    }
}
