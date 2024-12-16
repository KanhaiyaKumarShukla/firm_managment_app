package com.rach.firmmanagement.testing

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.rach.firmmanagement.notification.MyNotification


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun NotiUi() {
    val context = LocalContext.current
    val myNotification = MyNotification(
        context,
        title = "Important Notification",
        message = "Hi, I am Tom, a new subscriber."
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Button(onClick = {
            // Request notification permission on Android 13 and above
            // Show a toast to confirm the action
            Toast.makeText(context, "Attempting to send notification", Toast.LENGTH_SHORT).show()
            // Send the notification
            myNotification.fireNotification()
        }) {
            Text(text = "Send Notification")
        }



    }
}
