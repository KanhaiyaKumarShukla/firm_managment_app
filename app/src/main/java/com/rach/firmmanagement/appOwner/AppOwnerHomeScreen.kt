package com.rach.firmmanagement.appOwner

import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.FloatingActionButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rach.firmmanagement.dataClassImp.NoAdminDataClass
import com.rach.firmmanagement.firmAdminOwner.CustomButton
import com.rach.firmmanagement.ui.theme.blueAcha
import com.rach.firmmanagement.viewModel.AppOwnerViewModel

@Composable
fun AppOwnerHomeScreen(
    navigateToAddSuperAdmin: () -> Unit,
    viewModel: AppOwnerViewModel
){

    val scrollState = rememberScrollState()
    val scaffoldState = rememberScaffoldState()

    val firms by viewModel.firms.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedFirm by remember { mutableStateOf<NoAdminDataClass?>(null) }

    LaunchedEffect(Unit) {
        viewModel.getAllFirms()
        viewModel.listenToFirmsUpdates()
    }

    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navigateToAddSuperAdmin() },
                backgroundColor = blueAcha,
                elevation = FloatingActionButtonDefaults.elevation(10.dp)
            ) {

                Icon(
                    imageVector = Icons.Default.Add, contentDescription = "Add Employee",
                    tint = Color.White
                )

            }
        }
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                items(firms) { firm ->
                    FirmItem(
                        firm = firm,
                        onEdit = {
                            selectedFirm = firm
                            showEditDialog = true
                        },
                        onDelete = {

                            viewModel.deleteFirm(firm)

                        }
                    )
                }
            }
            if (showEditDialog && selectedFirm != null) {
                EditFirmDialog(
                    firm = selectedFirm!!,
                    onDismiss = { showEditDialog = false },
                    onUpdate = { updatedFirm ->
                        viewModel.updateFirm(updatedFirm)
                        showEditDialog = false
                    }
                )
            }
        }
    }
}

@Composable
fun EditFirmDialog(
    firm: NoAdminDataClass,
    onDismiss: () -> Unit,
    onUpdate: (NoAdminDataClass) -> Unit
) {
    var updatedFirm by remember { mutableStateOf(firm) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Firm Details") },
        text = {
            FirmDetailsScreen(
                firm = updatedFirm,
                onFirmChange = { updatedFirm = it }
            )
        },
        confirmButton = {
            CustomButton(
                onClick = {onUpdate(updatedFirm)},
                text="Update"
            )

        },
        dismissButton = {
            CustomButton(
                onClick = onDismiss,
                text="Cancel"
            )
        }
    )
}



@Composable
fun FirmItem(firm: NoAdminDataClass, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = 4.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopEnd
            ) {
                Row {
                    IconButton(onClick = { onEdit() }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Firm",
                            tint = Color.Gray
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = { onDelete() }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Firm",
                            tint = Color.Gray
                        )
                    }
                }
            }

            Text(text = firm.firmName ?: "Unknown Firm", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                Text(text = "Owner: ${firm.ownerName ?: "N/A"}", fontSize = 14.sp)
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = "Phone: ${firm.phoneNumber ?: "N/A"}", fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row {
                Text(text = "Address: ${firm.address ?: "N/A"}", fontSize = 14.sp)
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = "Area: ${firm.pinCode ?: "N/A"}", fontSize = 14.sp)
            }
        }
    }
}
