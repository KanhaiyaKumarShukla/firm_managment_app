package com.rach.firmmanagement.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.rach.firmmanagement.dataClassImp.GeofenceItems


class GeofenceRepository {
    val database = FirebaseFirestore.getInstance()

    fun saveGeofence(geofence: GeofenceItems) {
        database.collection("Members")
            .document(geofence.adminNo.toString())
            .collection("WorkCenters")
            .add(geofence)
            .addOnSuccessListener {
                Log.d("Geofence", "Geofence saved successfully")
            }
            .addOnFailureListener { e ->
                Log.e("Geofence", "Error saving geofence", e)
            }
    }

    fun getAllGeofences(adminPhoneNumber: String, onSuccess: (List<GeofenceItems>) -> Unit, onFailure: () -> Unit) {
        val updatedAdminNumber = if (adminPhoneNumber.startsWith("+91")) {
            adminPhoneNumber
        } else {
            "+91$adminPhoneNumber"
        }

        database.collection("Members")
            .document(updatedAdminNumber)
            .collection("WorkCenters")
            .get()
            .addOnSuccessListener { snapshot ->
                val geofenceList = snapshot.documents.mapNotNull { document ->
                    document.toObject(GeofenceItems::class.java)
                }
                Log.d("Geofence", "Geofences: $geofenceList")
                onSuccess(geofenceList)
            }
            .addOnFailureListener { e ->
                Log.e("Geofence", "Error fetching geofences", e)
                onFailure()
            }
    }
}