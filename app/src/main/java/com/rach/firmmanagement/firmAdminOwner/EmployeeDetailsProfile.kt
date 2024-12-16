package com.rach.firmmanagement.firmAdminOwner

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.maps.android.compose.Circle
import com.rach.firmmanagement.R
import com.rach.firmmanagement.ui.theme.FirmManagementTheme
import com.rach.firmmanagement.viewModel.AllEmployeeViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EmployeeDetailsProfile(viewModel: AllEmployeeViewModel) {

    val fields by viewModel.employeeHistoryFields.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Card(
            modifier = Modifier.size(120.dp),
            shape = CircleShape
        ) {
            Image(
                painter = painterResource(R.drawable.logo), contentDescription = "Logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(25.dp))

        fields.forEach { emply ->
            EmpoyeeItemScreen(
                modifier = Modifier,
                label = emply.label,
                title = emply.title
            )
            Spacer(modifier = Modifier.height(18.dp))

        }

    }

}

@Composable
fun EmpoyeeItemScreen(
    modifier: Modifier,
    label: String,
    title: String
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {

        Text(
            text = label, fontSize = 14.sp,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = title, fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun Previewww() {
    FirmManagementTheme {
        EmployeeDetailsProfile(viewModel = AllEmployeeViewModel())
    }
}