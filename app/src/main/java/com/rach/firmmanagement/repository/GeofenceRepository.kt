package com.rach.firmmanagement.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.rach.firmmanagement.dataClassImp.GeofenceItems


class GeofenceRepository {
    val database = FirebaseFirestore.getInstance()

    fun saveGeofence(geofence: GeofenceItems, onSuccess: (GeofenceItems) -> Unit, onFailure: () -> Unit) {
        database.collection("Firms")
            .document(geofence.firmName.toString())
            .collection("WorkCenters")
            .add(geofence)
            .addOnSuccessListener {
                Log.d("Geofence", "Geofence saved successfully")
                onSuccess(geofence)
            }
            .addOnFailureListener { e ->
                Log.e("Geofence", "Error saving geofence", e)
                onFailure()
            }
    }

    fun getAllGeofences(firmName: String, onSuccess: (List<GeofenceItems>) -> Unit, onFailure: () -> Unit) {
        database.collection("Firms")
            .document(firmName)
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