package com.rach.firmmanagement.login

import android.app.Activity
import android.content.Context
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.util.Log
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.rach.firmmanagement.necessaryItem.CustomOutlinedTextFiled
import com.rach.firmmanagement.necessaryItem.LoadProgressBar
import com.rach.firmmanagement.ui.theme.FirmManagementTheme
import com.rach.firmmanagement.ui.theme.blueAcha
import com.rach.firmmanagement.ui.theme.fontBablooBold
import com.rach.firmmanagement.ui.theme.fontPoppinsMedium
import com.rach.firmmanagement.ui.theme.red
import com.rach.firmmanagement.viewModel.LoginViewModel
import java.util.concurrent.TimeUnit

@Composable
fun OtpScreen(
    naviagteToHome: () -> Unit,
    loginViewModel: LoginViewModel
) {

    val otpText by loginViewModel.otp.collectAsState()
    val phoneNumber by loginViewModel.phoneNumberInput.collectAsState()
    Log.d("Hins", phoneNumber)

    val context = LocalContext.current

    val auth = FirebaseAuth.getInstance()

    val verificationId by loginViewModel.verificationId.collectAsState()

    var isError by remember {
        mutableStateOf(false)
    }

    var showProgressState by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(verificationId) {


        if (verificationId == null){
            showProgressState = true
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber("+91$phoneNumber")
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(context as Activity)
                .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(credential: PhoneAuthCredential) {

                        showProgressState = false

                        auth.signInWithCredential(credential)
                            .addOnCompleteListener { task ->

                                if (task.isSuccessful) {
                                    naviagteToHome()
                                    Toast.makeText(
                                        context,
                                        "OTP Verification Successful",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                } else {
                                    Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_LONG)
                                        .show()
                                }
                            }

                    }

                    override fun onVerificationFailed(p0: FirebaseException) {

                        showProgressState = false
                        Log.w("Hins", "onVerificationFailed: ${p0.message}")
                        Toast.makeText(context,"Verification Failed :${p0.message}",Toast.LENGTH_LONG).show()

                    }

                    override fun onCodeSent(sentVerficationCode: String, token: PhoneAuthProvider.ForceResendingToken) {
                        super.onCodeSent(sentVerficationCode, token)
                        Log.w("Hins", "Code Send Successfull")
                        showProgressState = false
                        loginViewModel.onVerficationIdChanged(sentVerficationCode)
                    }


                }).build()

            PhoneAuthProvider.verifyPhoneNumber(options)
        }

    }

    if (showProgressState){
        LoadProgressBar()
    }else{
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Enter Your OTP Code", style = fontBablooBold, fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 40.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            CustomOutlinedTextFiled(
                value = otpText,
                onValueChange = {
                                loginViewModel.onChangeOtp(it)
                },
                label = "Enter OTP",
                singleLine = true ,
                isError = isError,
                modifier = Modifier.padding(start = 24.dp, end = 24.dp),
                readOnly = false
            )

            Spacer(modifier = Modifier.height(30.dp))


            Button(
                onClick = {

                   if (otpText.isEmpty()){

                       isError = true
                       Toast.makeText(context,"Please Enter OTP",Toast.LENGTH_LONG).show()

                   }else{
                       showProgressState = true

                       verificationId?.let { id ->
                           val credential = PhoneAuthProvider.getCredential(id,otpText)
                           Log.d("Hins", "${credential.toString()}, ${id.toString()}, ${otpText.toString()}")
                           signInWithPhoneAuthCredential(
                               credential = credential,
                               naviagteToHome,
                               context,
                               {showProgressState = false}
                           )
                       }

                   }
                },
                modifier = Modifier.width(120.dp),
                colors = ButtonDefaults.buttonColors(
                    blueAcha
                )
            ) {

                Text(
                    text = "Verify", fontSize = 16.sp, color = Color.White,
                    style = fontBablooBold
                )

            }
            }





        }
    }


private fun signInWithPhoneAuthCredential(
    credential: PhoneAuthCredential,
    navigateToHome: () -> Unit,
    context: Context,
    showProgressBarCallback : () -> Unit
){

    FirebaseAuth.getInstance().signInWithCredential(credential)
        .addOnCompleteListener { task ->
            if (task.isSuccessful){
                navigateToHome()
            }else{
                showProgressBarCallback()
                Log.d("Hins", "otp verification failed in sign in with phone auth credential function in otp screen")
                Toast.makeText(context,"OTP Verification Failed",Toast.LENGTH_SHORT).show()
            }

        }

}

@Preview(showBackground = true)
@Composable
fun OtpScreenPreview() {
    FirmManagementTheme {
        OtpScreen({}, loginViewModel = LoginViewModel())
    }
}