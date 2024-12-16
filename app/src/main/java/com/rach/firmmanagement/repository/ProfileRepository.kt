package com.rach.firmmanagement.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.rach.firmmanagement.login.DataClassRegister
import kotlinx.coroutines.tasks.await

class ProfileRepository {

    val currentUser = FirebaseAuth.getInstance().currentUser?.phoneNumber?.removePrefix("+91") ?:""
    val database = FirebaseDatabase.getInstance().reference


    suspend fun getProfile(
        role: String,
        onSuccess: (DataClassRegister) -> Unit,
        onFailure: () -> Unit
    ): DataClassRegister? {

        try {

            val roleCollection = if (role == "Employee") "employees" else "employers"


            val data = database.child(roleCollection).child(currentUser)
                .get()
                .await()


            if (data.exists()) {

                val profile = data.getValue(DataClassRegister::class.java)

                profile?.let {
                    onSuccess(it)
                }

            }else{
                onFailure()
            }


        } catch (e: Exception) {

            e.printStackTrace()
            onFailure()
        }
        return null
    }

    suspend fun updateProfile(
        role: String,
        updatedProfile: DataClassRegister,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        try {
            val roleCollection = if (role == "Employee") "employees" else "employers"

            // Update data at the specific reference
            database.child(roleCollection).child(currentUser)
                .setValue(updatedProfile)
                .await()

            onSuccess()
        } catch (e: Exception) {
            e.printStackTrace()
            onFailure()
        }
    }


}