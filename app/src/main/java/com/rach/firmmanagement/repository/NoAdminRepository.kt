package com.rach.firmmanagement.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.rach.firmmanagement.dataClassImp.EmployeeHomeScreenData
import com.rach.firmmanagement.dataClassImp.NoAdminDataClass
import kotlinx.coroutines.tasks.await

class NoAdminRepository {

    val currentUser = FirebaseAuth.getInstance().currentUser?.phoneNumber.toString()
    val employeeNumber = when {
        currentUser.startsWith("+91") -> currentUser.removePrefix("+91")
        else ->
            currentUser
    }


    private val database = FirebaseFirestore.getInstance()

    suspend fun raiseARequest(
        phoneNumber: String,
        noAdminDataClass: NoAdminDataClass,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {

        try {
            database.collection("Members")
                .document(phoneNumber)
                .set(noAdminDataClass)
                .await()
            onSuccess()

        } catch (e: Exception) {
            onFailure()
        }

    }

    suspend fun loadEmployee(
        adminPhoneNumber: String,
        onSuccess: (List<EmployeeHomeScreenData>) -> Unit,
        onFailure: () -> Unit
    ) {

        val updateAdminNumber = if (adminPhoneNumber.startsWith("+91")) {
            adminPhoneNumber
        } else {
            "+91$adminPhoneNumber"
        }



        try {
            val data = database.collection("Members")
                .document(updateAdminNumber)
                .collection("Employee")
                .document(employeeNumber)
                .get()
                .await()

            if (data.exists()) {

                val employeeData = data.toObject(EmployeeHomeScreenData::class.java)

                if (employeeData != null){
                    val list = listOf(employeeData)
                    onSuccess(list)
                }else{
                    onFailure()
                }


            }else{
                onFailure()
            }


        } catch (_: Exception) {
            onFailure()
        }

    }

}

