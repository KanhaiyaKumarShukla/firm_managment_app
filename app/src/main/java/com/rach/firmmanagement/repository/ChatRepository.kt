package com.rach.firmmanagement.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.rach.firmmanagement.dataClassImp.MessageDataClass
import kotlinx.coroutines.tasks.await

class ChatRepository () {

    val database = FirebaseFirestore.getInstance()
    val currentUserNumber = FirebaseAuth.getInstance().currentUser?.phoneNumber.toString()

    suspend fun fetchMessages(
        adminPhoneNumber: String,
        employeeNumber: String,
        onSuccess: (List<MessageDataClass>) -> Unit,
        onFailure: () -> Unit
    ) {
        val updateAdminNumber = if (adminPhoneNumber.startsWith("+91")) {
            adminPhoneNumber
        } else {
            "+91$adminPhoneNumber"
        }
        try {
            val messages = database.collection("Members")
                .document(updateAdminNumber)
                .collection("Employee")
                .document(employeeNumber)
                .collection("Messages")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()
                .toObjects(MessageDataClass::class.java)

            onSuccess(messages)
        } catch (e: Exception) {
            e.printStackTrace()
            onFailure()
        }
    }

    suspend fun sendMessage(
        adminPhoneNumber: String,
        employeeNumber: String,
        message: MessageDataClass,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        val updateAdminNumber = if (adminPhoneNumber.startsWith("+91")) {
            adminPhoneNumber
        } else {
            "+91$adminPhoneNumber"
        }
        try {
            database.collection("Members")
                .document(updateAdminNumber)
                .collection("Employee")
                .document(employeeNumber)
                .collection("Messages")
                .add(message)
                .await()
            onSuccess()
        } catch (e: Exception) {
            e.printStackTrace()
            onFailure()
        }
    }
}
