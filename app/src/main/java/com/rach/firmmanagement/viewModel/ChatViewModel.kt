package com.rach.firmmanagement.viewModel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.rach.firmmanagement.dataClassImp.MessageDataClass
import com.rach.firmmanagement.repository.ChatRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val _messages = MutableStateFlow<List<MessageDataClass>>(emptyList())
    val messages: StateFlow<List<MessageDataClass>> = _messages
    private val repository = ChatRepository()

    val currentUserNumber = FirebaseAuth.getInstance().currentUser?.phoneNumber.toString()


    // Use SavedStateHandle to store and retrieve inputMessage
    //private val savedStateHandle: SavedStateHandle = savedStateHandle()
    private val _inputMessage = MutableStateFlow("")
    val inputMessage: StateFlow<String> = _inputMessage

    fun onChangeMessage(newReason: String) {
        _inputMessage.value = newReason
    }

    val employeeNumber = when {
        currentUserNumber.startsWith("+91") -> currentUserNumber.removePrefix("+91")
        else ->
            currentUserNumber
    }

    fun fetchMessages(
        adminPhoneNumber: String
    ) {
        viewModelScope.launch {
            repository.fetchMessages(
                adminPhoneNumber,
                employeeNumber,
                onSuccess = { fetchedMessages ->
                    _messages.value = fetchedMessages
                },
                onFailure = {
                    _messages.value= emptyList()
                }
            )
        }
    }

    fun sendMessage(
        adminPhoneNumber: String,
        message: MessageDataClass,
    ) {
        Log.d("Chat", "Sending message: $message")
        viewModelScope.launch {
            repository.sendMessage(
                adminPhoneNumber = adminPhoneNumber,
                employeeNumber = employeeNumber,
                message = message,
                onSuccess = {
                    _messages.update { messages ->
                        listOf(message) + messages
                    }
                }, {
                    // Handle failure
                }
            )
        }
    }
}
