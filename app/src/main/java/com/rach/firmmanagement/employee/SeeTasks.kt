package com.rach.firmmanagement.employee

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rach.firmmanagement.ui.theme.FirmManagementTheme
import com.rach.firmmanagement.ui.theme.fontBablooBold
import com.rach.firmmanagement.viewModel.EmlAllTask
import com.rach.firmmanagement.viewModel.LoginViewModel


@Composable
fun SeeTasks(
    viewModel: EmlAllTask,
    loginViewModel: LoginViewModel
) {
    val employee = viewModel.employeeList.value
    val isLoading = viewModel.isLoading.value
    val adminPhoneNumber by loginViewModel.firmOwnerNumber.collectAsState()

    LaunchedEffect(key1 = adminPhoneNumber) {
        viewModel.loadAllTask(adminPhoneNumber = adminPhoneNumber)
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn {
            items(employee) { item ->

                SeeTasksDesign(date = item.date ?: "N/A", task = item.task ?: "N/A")

            }
        }
    }
}


@Composable
fun SeeTasksDesign(
    date: String,
    task: String
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFB2EBF2),
                        Color(0xFFE0F7FA)
                    )
                ),
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = date,
                fontSize = 16.sp,
                style = fontBablooBold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = task,
                fontSize = 20.sp,
                style = fontBablooBold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp, top = 8.dp),
                textAlign = TextAlign.Center,
                color = Color.Black
            )

        }

    }


}

@Preview(showBackground = true)
@Composable
fun SeeTaskPreview() {
    FirmManagementTheme {
        SeeTasksDesign(
            date = "156526",
            task = "ihqwddkd"
        )
    }
}