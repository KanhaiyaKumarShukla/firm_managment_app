package com.rach.firmmanagement.repository

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.rach.firmmanagement.dataClassImp.AddStaffDataClass
import com.rach.firmmanagement.dataClassImp.NoAdminDataClass

class AppOwnerRepository {
    private val database = FirebaseFirestore.getInstance()

    fun listenToFirms(
        onSuccess: (List<NoAdminDataClass>) -> Unit,
        onFailure: (Exception) -> Unit
    ): ListenerRegistration {
        return database.collection("App Owner")
            .addSnapshotListener { snapshots, exception ->
                if (exception != null) {
                    Log.e("FirmRepository", "Snapshot listener error: ${exception.message}")
                    onFailure(exception)
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    val firmList = snapshots.documents.mapNotNull { it.toObject(NoAdminDataClass::class.java) }
                        .filter { !it.phoneNumber.isNullOrBlank() }
                    Log.d("FirmRepository", "Firms updated: $firmList")
                    onSuccess(firmList)
                }
            }
    }

    suspend fun addFirm(
        phoneNumber: String,
        noAdminDataClass: NoAdminDataClass,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        val genderCollection = database.collection("Gender")

        // Find the single document inside the "Gender" collection
        genderCollection.get()
            .addOnSuccessListener { documents ->
                val document = documents.documents.firstOrNull() // Get the first document
                if (document != null) {
                    val genderDocRef = genderCollection.document(document.id)

                    val addStaffDataClass= AddStaffDataClass(
                        firmName = noAdminDataClass.firmName,
                        phoneNumber = noAdminDataClass.phoneNumber,
                        newPhoneNumber = noAdminDataClass.phoneNumber,
                        name = noAdminDataClass.ownerName,
                        role="Super Admin"
                    )
                    // Add firm details to "App Owner"
                    database.collection("App Owner").document(phoneNumber).set(noAdminDataClass)
                        .addOnSuccessListener {
                            // Also update the Gender collection with phoneNumber as key and "Super Admin" as value
                            genderDocRef.update(mapOf(phoneNumber to "Super Admin"))
                                .addOnSuccessListener { onSuccess() }
                                .addOnFailureListener { onFailure() }
                            database.collection("Employee").document(phoneNumber).set(addStaffDataClass)
                                .addOnSuccessListener {
                                    Log.d("FirmRepository", "Firm added successfully: $phoneNumber")
                                }
                        }
                        .addOnFailureListener { onFailure() }
                } else {
                    onFailure() // No document found in the "Gender" collection
                }
            }
            .addOnFailureListener { onFailure() }
    }

    fun deleteFirm(phoneNumber: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val genderCollection = database.collection("Gender")

        // Find the single document inside the "Gender" collection
        genderCollection.get()
            .addOnSuccessListener { documents ->
                val document = documents.documents.firstOrNull()
                if (document != null) {
                    val genderDocRef = genderCollection.document(document.id)

                    // Delete firm from "App Owner"
                    database.collection("App Owner")
                        .document(phoneNumber)
                        .delete()
                        .addOnSuccessListener {
                            Log.d("FirmRepository", "Firm deleted successfully: $phoneNumber")

                            // Also remove the phoneNumber field from the Gender document
                            genderDocRef.update(mapOf(phoneNumber to FieldValue.delete()))
                                .addOnSuccessListener { onSuccess() }
                                .addOnFailureListener { onFailure(it) }
                            database.collection("Employee")
                                .document(phoneNumber)
                                .delete()
                                .addOnSuccessListener {
                                    Log.d("FirmRepository", "Firm deleted successfully: $phoneNumber")
                                }
                                .addOnFailureListener { onFailure(it) }
                        }
                        .addOnFailureListener { onFailure(it) }
                } else {
                    onFailure(Exception("No document found in Gender collection"))
                }
            }
            .addOnFailureListener { onFailure(it) }
    }



    fun getAllFirms(
        onSuccess: (List<NoAdminDataClass>) -> Unit,
        onFailure: () -> Unit
    ) {
        database.collection("App Owner")
            .get()
            .addOnSuccessListener { documents ->
                val firmList = documents.mapNotNull { it.toObject(NoAdminDataClass::class.java) }
                    .filter { !it.phoneNumber.isNullOrBlank() }
                onSuccess(firmList)
                Log.d("FirmRepository", "Firms fetched successfully: $firmList")
            }
            .addOnFailureListener { exception ->
                Log.e("FirmRepository", "Error fetching firms: ${exception.message}")
                onFailure()
            }
    }


    fun updateFirm(phoneNumber: String, updatedFirm: NoAdminDataClass, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        database.collection("App Owner")
            .document(phoneNumber)
            .set(updatedFirm)
            .addOnSuccessListener {
                Log.d("FirmRepository", "Firm updated successfully: $updatedFirm")
                onSuccess()
            }
            .addOnFailureListener {
                Log.e("FirmRepository", "Error updating firm: ${it.message}")
                onFailure(it)
            }
    }

    /*
    fun deleteFirm(phoneNumber: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        database.collection("App Owner")
            .document(phoneNumber)
            .delete()
            .addOnSuccessListener {
                Log.d("FirmRepository", "Firm deleted successfully: $phoneNumber")
                onSuccess()
            }
            .addOnFailureListener {
                Log.e("FirmRepository", "Error deleting firm: ${it.message}")
                onFailure(it)
            }
    }

     */



}