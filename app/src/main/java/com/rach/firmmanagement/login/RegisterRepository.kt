package com.rach.firmmanagement.login

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class RegisterRepository {


    private val database = FirebaseDatabase.getInstance().reference
    val firestoreDB = FirebaseFirestore.getInstance()

    suspend fun saveUserData(
        role: String,
        dataClassRegister: DataClassRegister,
        phoneNumber: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {

        try {
            val roleCollection = "pendingEmployees"

            val userRef = database.child(roleCollection).child(phoneNumber)
            userRef.setValue(dataClassRegister).await()

            val firmRef=firestoreDB.collection("Firms")
                .document(dataClassRegister.firmName.toString())
            firmRef.set(mapOf("placeholder" to true)) // Placeholder to ensure document exists
                .await()
            firmRef.collection(roleCollection)
                .document(dataClassRegister.mobileNumber.toString())
                .set(dataClassRegister)
                .await()
            onSuccess()

        } catch (e: Exception) {

            onFailure()

        }


    }

    suspend fun checkRegisterOrNot(
        role: String,
        phoneNumber: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {

        val roleCollection = "pendingEmployees"
        val cleanPhoneNumber = phoneNumber.trim()
        val userRef = database.child(roleCollection).child(cleanPhoneNumber)
        Log.d("kan", "User data exists $phoneNumber: $userRef")
        userRef.get()
            .addOnSuccessListener { snapshot ->

                Log.d("kan", "User data exists: $snapshot")
                if (snapshot.exists() && snapshot.value != null) {
                    // Convert snapshot data to your data class
                    val userData = snapshot.getValue(DataClassRegister::class.java)
                    Log.d("kan", "User data exists: $userData")

                    onSuccess() // Callback for success
                } else {
                    Log.d("kan", "No data found for the user.")
                    onFailure() // Callback for failure
                }
            }
            .addOnFailureListener { exception ->
                Log.e("kan", "Error retrieving user data: ${exception.message}")
                onFailure() // Callback for failure
            }



    }

}