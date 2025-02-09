package com.rach.firmmanagement.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class HomeRepository {

    val database = FirebaseFirestore.getInstance()

    suspend fun getGender(
        phoneNumber: String,
        dataFound: (String) -> Unit,
        noDataFound: () -> Unit
    ) {
        try {

            Log.d("App Owner", "getGender: $phoneNumber")

            val updatePhoneNumber = when {
                phoneNumber.startsWith("+91") -> phoneNumber.removePrefix("+91")
                else -> phoneNumber
            }
            val isExits = database.collection("Gender")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val gender = document.getString(updatePhoneNumber) ?: ""
                        if (gender.isNotEmpty()) {
                            Log.d("App Owner", "Phone number matched with document: ${phoneNumber}, $gender")
                            dataFound(gender)
                            return@addOnSuccessListener // Stop further checking once found
                        }
                    }
                    Log.d("App Owner", "No matching phone number found gender")
                    noDataFound()
                }
                .addOnFailureListener { exception ->
                    Log.e("App Owner", "Error fetching documents: ${exception.message}")
                    noDataFound()
                }

        }catch (e:Exception){
            Log.d("Hins", "getGender: $e")
        }

    }

    suspend fun checkEmployeeOrEmployee(
        phoneNumber: String,
        genderState: String,
        adminNumber: String,
        dataFound: () -> Unit,
        noDataFound: () -> Unit,
        pendingDataFound: () -> Unit

    ) {
        Log.d("Hins", "check empl or er: $genderState + $phoneNumber + $adminNumber")

        try {
            val cleanedPhoneNumber = phoneNumber.removePrefix("+91")

            if(genderState=="App Owner"){
                val isExits = database.collection("App Owner")
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            val storedPhoneNo = document.getString("PhoneNo") ?: ""
                            if (storedPhoneNo == phoneNumber) {
                                Log.d("App Owner", "Phone number matched with document: ${document.id}")
                                dataFound()
                                return@addOnSuccessListener // Stop further checking once found
                            }
                        }
                        Log.d("App Owner", "No matching phone number found")
                        noDataFound()
                    }
                    .addOnFailureListener { exception ->
                        Log.e("App Owner", "Error fetching documents: ${exception.message}")
                        noDataFound()
                    }

                Log.d("App Owner", "check empl or er")
            }

            else {
                val isExits = database.collection("Employee")
                    .document(cleanedPhoneNumber)
                    .get()
                    .await()

                Log.d("Hins", "check empl or er **$4 : $isExits")

                if (isExits.exists()) {

                    val status = isExits.getString("status")

                    // Check if the document exists
                    if (isExits.exists()) {
                        val data = isExits.data // Get the document data as a Map
                        val role = data?.get("role") as? String // Safely cast the role to String

                        Log.d("Hins", "check empl or er **: ${isExits.data}, $role")

                        // Check the role and call the appropriate function
                        when (role) {
                            "Employee", "Admin", "Super Admin" -> {
                                dataFound() // Call this if the role is valid
                            }
                            else -> {
                                noDataFound() // Call this if the role is invalid or not found
                            }
                        }
                    } else {
                        noDataFound() // Call this if the document does not exist
                    }
                    /*
                    when (status) {
                        "Approved" -> {
                            dataFound()
                        }

                        "pending" -> {
                            pendingDataFound()
                        }

                        else -> {
                            noDataFound()
                        }
                    }

                     */


                } else {
                    noDataFound()
                }
            }
            /*
            else {


                val updatedAdminNumber = if (adminNumber.startsWith("+91")) {
                    adminNumber
                } else {
                    "+91$adminNumber"
                }

                val updatedPhoneNumber = when {
                    phoneNumber.startsWith("+91") -> phoneNumber.removePrefix("+91")
                    else -> phoneNumber
                }


                val data = database.collection("Members")
                    .document(updatedAdminNumber)
                    .collection("Employee")
                    .document(updatedPhoneNumber)
                    .get()
                    .await()



                if (data.exists()) {
                    dataFound()
                } else {
                    noDataFound()
                }


            }

             */

        } catch (_: Exception) {

            noDataFound()
        }


    }

}