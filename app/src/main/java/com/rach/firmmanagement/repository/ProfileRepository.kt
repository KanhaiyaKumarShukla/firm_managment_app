package com.rach.firmmanagement.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.rach.firmmanagement.dataClassImp.AddStaffDataClass
import com.rach.firmmanagement.dataClassImp.EmployeeIdentity
import com.rach.firmmanagement.login.DataClassRegister
import kotlinx.coroutines.tasks.await

class ProfileRepository {

    val currentUser = FirebaseAuth.getInstance().currentUser?.phoneNumber?.removePrefix("+91") ?:""
    val database = FirebaseDatabase.getInstance().reference
    val firestoreDB= FirebaseFirestore.getInstance()


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

    suspend fun getEmployeeIdentity(
        onSuccess: (AddStaffDataClass) -> Unit,
        onFailure: () -> Unit
    ){
        try {
            firestoreDB.collection("Employee")
                .document(currentUser)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val identity = documentSnapshot.toObject(AddStaffDataClass::class.java)
                        if (identity != null) {
                            Log.d("EmployeeIdentity", "Identity: $identity")
                            onSuccess(identity)
                        } else {
                            Log.d("EmployeeIdentity", "Identity is null")
                            onFailure()
                        }
                    }else{
                        Log.d("EmployeeIdentity", "Document does not exist")
                        onFailure()
                    }
                }
                .addOnFailureListener {
                    Log.e("EmployeeIdentity", "Error getting identity", it)
                    onFailure()
                }
        }catch (e:Exception){
            Log.e("EmployeeIdentity", "Error getting identity", e)
            e.printStackTrace()
            onFailure()
        }
    }


}