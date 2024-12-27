package com.rach.firmmanagement.firmAdminOwner

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rach.firmmanagement.ui.theme.blueAcha
import com.rach.firmmanagement.ui.theme.fontBablooBold
import com.rach.firmmanagement.ui.theme.fontPoppinsMedium
import com.rach.firmmanagement.viewModel.GeofenceViewModel

@Composable
fun AddGeofence(viewModel: GeofenceViewModel=viewModel(), navigateToAddGeofenceByMap: ()->Unit) {
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    var radius by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    val context = LocalContext.current
    val buttonState by viewModel.onButtonClicked.collectAsState()

    val latError = buttonState && latitude.isEmpty()
    val longError = buttonState && longitude.isEmpty()
    val radiusError = buttonState && radius.isEmpty()
    val titleError = buttonState && title.isEmpty()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth(),
            elevation = CardDefaults.elevatedCardElevation(),

            ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Add New Work Center", fontSize = 20.sp, style = fontPoppinsMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(10.dp)
                )
                Text(
                    text = "Please Fill Your Details", fontSize = 18.sp,
                    style = fontPoppinsMedium
                )


                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = titleError,
                    readOnly = false
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = latitude,
                    onValueChange = { latitude = it },
                    label = { Text("Latitude") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = latError,
                    readOnly = false
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = longitude,
                    onValueChange = { longitude = it },
                    label = { Text("Longitude") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = longError,
                    readOnly = false
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = radius,
                    onValueChange = { radius = it },
                    label = { Text("Radius") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = radiusError,
                    readOnly = false
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            viewModel.onButtonStateChange(true)
                            if (latitude.isNotEmpty() && longitude.isNotEmpty() && radius.isNotEmpty()) {
                                viewModel.saveGeofence(
                                    latitude = latitude,
                                    longitude = longitude,
                                    radius = radius,
                                    title = title,
                                    employeeNo = "All"
                                )
                            } else {
                                Toast.makeText(
                                    context,
                                    "Please fill all fields",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            blueAcha
                        )
                    ) {
                        Text(
                            "Save",
                            style = fontBablooBold,
                            color = Color.White
                        )
                    }

                    Button(
                        onClick = {
                            navigateToAddGeofenceByMap()
                        },
                        colors = ButtonDefaults.buttonColors(
                            blueAcha
                        )
                    ) {
                        Text(
                            "Using Google Map",
                            style = fontBablooBold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}