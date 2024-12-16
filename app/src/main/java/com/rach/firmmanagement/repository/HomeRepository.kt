package com.rach.firmmanagement.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class HomeRepository {

    val database = FirebaseFirestore.getInstance()

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

            if (genderState == "Employer") {
                val isExits = database.collection("Members")
                    .document(phoneNumber)
                    .get()
                    .await()



                if (isExits.exists()) {

                    val status = isExits.getString("status")

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


                } else {
                    noDataFound()
                }
            } else {

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

        } catch (_: Exception) {


        }


    }

}