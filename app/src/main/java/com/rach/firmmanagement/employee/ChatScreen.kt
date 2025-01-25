package com.rach.firmmanagement.employee


import android.text.TextUtils.indexOf
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.rach.firmmanagement.dataClassImp.MessageDataClass
import com.rach.firmmanagement.ui.theme.blueAcha
import com.rach.firmmanagement.viewModel.ChatViewModel
import com.rach.firmmanagement.viewModel.LoginViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/*
@Composable
fun ChatScreen(viewModel: ChatViewModel= viewModel(), loginViewModel: LoginViewModel,) {
    val messages by viewModel.messages.collectAsState()
    val inputMessage by viewModel.inputMessage.collectAsState()

    val adminPhoneNumber by loginViewModel.firmOwnerNumber.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchMessages(
            adminPhoneNumber = adminPhoneNumber
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            reverseLayout = true
        ) {
            items(messages) { message ->
                if (message.senderName == viewModel.employeeNumber) {
                    // Right-aligned message (sent by user)
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "You", // Display "You" for the sender
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = message.message,
                            color = Color.White,
                            fontSize = 16.sp,
                            modifier = Modifier
                                .background(Color.Blue, RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        )
                    }
                } else {
                    // Left-aligned message (received)
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = message.senderName, // Display the sender's phone number
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = message.message,
                            color = Color.Black,
                            fontSize = 16.sp,
                            modifier = Modifier
                                .background(Color.LightGray, RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        )
                    }
                }
                // Add spacer after each item (except the last one)
                Spacer(modifier = Modifier.height(6.dp))
            }
        }


        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputMessage,
                onValueChange = { viewModel.onChangeMessage(it) },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type a message") }
            )
            IconButton(
                onClick = {
                    if (inputMessage.isNotEmpty()) {
                        viewModel.sendMessage(
                            adminPhoneNumber = adminPhoneNumber,
                            message = MessageDataClass(
                                senderName = viewModel.employeeNumber,
                                receiverName = adminPhoneNumber,
                                message = inputMessage,
                                timestamp = System.currentTimeMillis()
                            )
                        )
                        viewModel.onChangeMessage("")
                    }
                }
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send")
            }
        }
    }
}
*/
@Composable
fun ChatScreen(viewModel: ChatViewModel = viewModel(), loginViewModel: LoginViewModel) {
    val messages by viewModel.messages.collectAsState()
    val inputMessage by viewModel.inputMessage.collectAsState()
    val adminPhoneNumber by loginViewModel.firmOwnerNumber.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchMessages(adminPhoneNumber = adminPhoneNumber)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .imePadding(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .weight(1f) // Makes this part compress when the keyboard is active
                .fillMaxWidth()
        ) {
            MessageList(
                messages = messages,
                employeeNumber = viewModel.employeeNumber,
                modifier = Modifier.fillMaxSize()
            )
        }

        InputMessageBar(
            inputMessage = inputMessage,
            onMessageChange = viewModel::onChangeMessage,
            onSendMessage = {
                if (inputMessage.isNotEmpty()) {
                    viewModel.sendMessage(
                        adminPhoneNumber = adminPhoneNumber,
                        message = MessageDataClass(
                            senderName = viewModel.employeeNumber,
                            receiverName = adminPhoneNumber,
                            message = inputMessage,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                    viewModel.onChangeMessage("")
                }
            }
        )
    }
}
@Composable
fun MessageList(
    messages: List<MessageDataClass>,
    employeeNumber: String,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxHeight(), // Fixed the weight issue
        reverseLayout = true
    ) {
        items(messages) { message ->
            if (message.senderName == employeeNumber) {
                SentMessage(message = message.message)
            } else {
                ReceivedMessage(senderName = message.senderName, message = message.message)
            }
            Spacer(modifier = Modifier.height(6.dp))
        }
    }
}


@Composable
fun SentMessage(message: String) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterEnd
    ) {
        Column(
            modifier = Modifier
                .background(
                    blueAcha,
                    RoundedCornerShape(8.dp)
                ) // Apply background to the entire Column
                .padding(8.dp), // Ensure the background fills the entire width
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "You",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = message,
                color = Color.White,
                fontSize = 16.sp,
            )
        }
    }
}

@Composable
fun ReceivedMessage(senderName: String, message: String) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
    ) {
        Column(
            modifier = Modifier
                .background(Color.LightGray, RoundedCornerShape(8.dp))
                .padding(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = senderName,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = message,
                color = Color.Black,
                fontSize = 16.sp,
            )
        }
    }
}

@Composable
fun InputMessageBar(
    inputMessage: String,
    onMessageChange: (String) -> Unit,
    onSendMessage: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = inputMessage,
            onValueChange = onMessageChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Type a message") }
        )
        IconButton(onClick = onSendMessage) {
            Icon(Icons.Default.Send, contentDescription = "Send")
        }
    }
}

