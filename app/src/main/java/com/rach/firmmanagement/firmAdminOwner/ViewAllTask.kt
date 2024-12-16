package com.rach.firmmanagement.firmAdminOwner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rach.firmmanagement.ui.theme.FirmManagementTheme
import com.rach.firmmanagement.viewModel.AdminViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.material.CircularProgressIndicator

data class ViewAllTask2(
    val date: String,
    val task: String
)

@Composable
fun ViewAllTask(adminViewModel: AdminViewModel= viewModel()) {

    val tasks = adminViewModel.tasks.collectAsState()
    val loading = adminViewModel.loading.collectAsState()

    LaunchedEffect(Unit) {
        adminViewModel.loadTasks()
    }

    if (loading.value) { // Use .value for State objects
        CircularProgressIndicator()
    } else {
        LazyColumn {
            items(tasks.value) { task -> // Access .value for the list
                ViewAllTaskDesign(
                    task = task.task.orEmpty(),
                    date = task.date.orEmpty(),
                    onDeleteClick = {
                        adminViewModel.deleteTask(task)
                    },
                    modifier = Modifier
                )
            }
        }
    }
}

@Composable
fun ViewAllTaskDesign(
    task: String,
    date: String,
    onDeleteClick: () -> Unit,
    modifier: Modifier
) {
    ElevatedCard(
        modifier = modifier.padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = modifier.weight(1f)
            ) {
                Text(
                    text = date,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .padding(bottom = 4.dp),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = task,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }

            IconButton(
                onClick = {
                    onDeleteClick()
                },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete, contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

        }
    }
}

@Preview
@Composable
private fun Preview() {
    FirmManagementTheme {
        ViewAllTask()
    }
}