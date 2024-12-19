package com.rach.firmmanagement.employee

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rach.firmmanagement.notification.MyNotification
import com.rach.firmmanagement.ui.theme.FirmManagementTheme
import com.rach.firmmanagement.ui.theme.blueAcha
import com.rach.firmmanagement.ui.theme.fontBablooBold
import com.rach.firmmanagement.ui.theme.progressBarBgColor
import com.rach.firmmanagement.viewModel.EmployeeViewModel1
import com.rach.firmmanagement.viewModel.LoginViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvanceMoneyRequestScreen(
    viewModel1: EmployeeViewModel1 = viewModel(),
    loginViewModel: LoginViewModel
) {
    val context = LocalContext.current
    val amount by viewModel1.amount.collectAsState()
    val reason by viewModel1.reasonAdvance.collectAsState()
    val state by viewModel1.circularBarState.collectAsState()
    val adminPhoneNumber by loginViewModel.firmOwnerNumber.collectAsState()
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(Color.White),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Advance Money Request",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Amount Field
            OutlinedTextField(
                value = amount,
                onValueChange = { viewModel1.onChangeAmount(it) },
                label = { Text("Requested Amount") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                colors = TextFieldDefaults.outlinedTextFieldColors(),
                shape = RoundedCornerShape(8.dp),
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Reason Field
            OutlinedTextField(
                value = reason,
                onValueChange = { viewModel1.onChangeResAdvance(it) },
                label = { Text("Reason") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(),
                shape = RoundedCornerShape(8.dp),
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Submit Button
            Button(
                onClick = {

                          scope.launch {
                              if (amount.isEmpty() && reason.isEmpty()){
                                  Toast.makeText(context,"Please Fill all the fields",Toast.LENGTH_LONG).show()
                              }else{

                                  viewModel1.advanceMoney(
                                      adminPhoneNumber = adminPhoneNumber,
                                      onSuccess = {
                                          val notification = MyNotification(context,
                                              title = "Firm Management App",
                                              message = "Request Added")

                                          notification.fireNotification()
                                      },
                                      onFailure = {
                                          val notification = MyNotification(context,
                                              title = "Firm Management App",
                                              message = "Request Added Failed")

                                          notification.fireNotification()
                                      }
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
                    text = "Submit",
                    color = Color.White,
                    style = fontBablooBold
                )

            }
        }

        if (state) {
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

@Preview(showBackground = true)
@Composable
fun Previeww19191() {
    FirmManagementTheme {
        AdvanceMoneyRequestScreen(loginViewModel = LoginViewModel())
    }
}