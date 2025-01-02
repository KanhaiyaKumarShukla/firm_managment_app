package com.rach.firmmanagement.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.rach.firmmanagement.dataClassImp.Festival
import com.rach.firmmanagement.dataClassImp.RegularHolidayItems
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class HolidayRepository {

    val database = FirebaseFirestore.getInstance()
    val currentUserPhoneNumber = FirebaseAuth.getInstance().currentUser?.phoneNumber.toString()
    val updateAdminNumber = if (currentUserPhoneNumber.startsWith("+91")) {
        currentUserPhoneNumber
    } else {
        "+91$currentUserPhoneNumber"
    }
    suspend fun getFestivals(
        year: String,
        onSuccess: (List<Festival>) -> Unit,
        onFailure: () -> Unit
    ){
        try {
            val documentSnapshot = database
                .collection("Members")
                .document(updateAdminNumber)
                .collection("Holiday")
                .document(year)
                .get()
                .await()

            val festivalsMap = documentSnapshot.data ?: emptyMap()

            // Convert the map to a list of Festival objects
            val festivals = festivalsMap.values.mapNotNull {
                it as? Map<*, *> // Safely cast to a map
            }.map { map ->
                Festival(
                    name = map["name"] as? String ?: "",
                    date = map["date"] as? String ?: "",
                    year = map["year"] as? String ?: "",
                    month = map["month"] as? String ?: "",
                    selected = map["selected"] as? Boolean ?: false
                )
            }
            onSuccess(festivals)

        } catch (e: Exception) {
            onFailure()
        }
    }

    suspend fun addFestival(
        festival: Festival,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        try {
            database
                .collection("Members")
                .document(updateAdminNumber)
                .collection("Holiday")
                .document(festival.year)
                .set(mapOf(festival.name to festival), SetOptions.merge())
                .await()
            onSuccess()
        } catch (e: Exception) {
            onFailure()
        }
    }

    suspend fun fetchAdditionalHolidays(
        onSuccess: (List<String>) -> Unit,
        onFailure: () -> Unit
    ) {

        try {
            val querySnapshot = database
                .collection("Members")
                .document(updateAdminNumber)
                .collection("AdditionalHolidays")
                .get()
                .await()

            val holidays = querySnapshot.documents.map { it.getString("holiday") ?: "" }
            onSuccess(holidays)
        } catch (e: Exception) {
            onFailure()
        }
    }

    suspend fun addAdditionalHolidays(
        holiday: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        try{
            database
                .collection("Members")
                .document(updateAdminNumber)
                .collection("AdditionalHolidays")
                .document(UUID.randomUUID().toString())
                .set(mapOf("holiday" to holiday)).await()
            onSuccess()
        }catch (e:Exception){
            onFailure()
        }
    }

    suspend fun saveFestivals(
        festivals: List<Festival>,
        employeeNumber: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        try {
            Log.d("Holiday", "$festivals, $employeeNumber")
            val batch = database.batch()
            festivals.forEach { festival ->
                // Reference for year document
                val yearDocRef = database
                    .collection("Members")
                    .document(updateAdminNumber)
                    .collection("Employee")
                    .document(employeeNumber)
                    .collection("Holidays")
                    .document(festival.year)

                // Reference for month document under the year
                val monthDocRef = yearDocRef
                    .collection("Festival")
                    .document(festival.month)

                // Reference for the actual festival document
                val festivalDocRef = monthDocRef
                    .collection(festival.name)
                    .document("Details")

                // Ensure year document exists
                batch.set(yearDocRef, mapOf("created" to true), SetOptions.merge())

                // Ensure month document exists
                batch.set(monthDocRef, mapOf("created" to true), SetOptions.merge())

                // Add the actual festival data
                batch.set(festivalDocRef, festival)
            }
            batch.commit().await()
            onSuccess()
        } catch (e: Exception) {
            Log.d("Holiday", e.message.toString())
            onFailure()
        }
    }

    suspend fun saveRegularHolidays(
        regularHolidays: RegularHolidayItems,
        employeeNumber: String,
        year: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        try {
            val batch = database.batch()
            Log.d("Holiday", "$employeeNumber, $year, $regularHolidays")
            val yearDocRef = database
                .collection("Members")
                .document(updateAdminNumber)
                .collection("Employee")
                .document(employeeNumber)
                .collection("Holidays")
                .document(year)
            // Reference for regular holidays collection
            val regularHolidaysDocRef = yearDocRef
                .collection("RegularHolidays")
                .document(System.currentTimeMillis().toString())

            // Ensure year document exists
            batch.set(yearDocRef, mapOf("created" to true), SetOptions.merge())
            Log.d("Holiday", "Regular Path: ${regularHolidaysDocRef.path}")
            batch.set(regularHolidaysDocRef, regularHolidays)
            batch.commit().await()
            onSuccess()
        } catch (e: Exception) {
            Log.d("Holiday", e.message.toString())
            onFailure()
        }
    }
}