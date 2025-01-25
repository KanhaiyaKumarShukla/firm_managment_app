package com.rach.firmmanagement.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.rach.firmmanagement.dataClassImp.AddStaffDataClass
import com.rach.firmmanagement.dataClassImp.AddWorkingHourDataClass
import com.rach.firmmanagement.dataClassImp.EmployeeLeaveData
import com.rach.firmmanagement.dataClassImp.Festival
import com.rach.firmmanagement.dataClassImp.RegularHolidayItems
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
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



                // Ensure year document exists
                batch.set(yearDocRef, mapOf("created" to true), SetOptions.merge())

                // Ensure month document exists
                batch.set(monthDocRef, mapOf("created" to true), SetOptions.merge())

                // Reference for the Details collection
                val detailsCollectionRef = monthDocRef.collection("Details")

                // Add the actual festival data as a unique document
                val festivalDocRef = detailsCollectionRef.document() // Auto-generate a unique document ID
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
    private var tag="Salary"
    suspend fun fetchEmployeeData(
        employeePhoneNumber: String
    ): AddStaffDataClass {
        val document = database.collection("Members")
            .document(updateAdminNumber)
            .collection("Employee")
            .document(employeePhoneNumber)
            .get()
            .await()

        Log.d(tag, "Employee Data: " + document.data.toString() + document.toObject(AddStaffDataClass::class.java))

        return document.toObject(AddStaffDataClass::class.java) ?: AddStaffDataClass()
    }

    suspend fun getFestivalsForMonthAndYear(
        employeeNumber: String,
        year: String,
        month: String,
        onSuccess: (List<Festival>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            val monthNumber = SimpleDateFormat("MMM", Locale.ENGLISH).parse(month)?.let {
                SimpleDateFormat("M", Locale.ENGLISH).format(it)
            } ?: throw IllegalArgumentException("Invalid month format")
            val detailsCollectionRef = database
                .collection("Members")
                .document(updateAdminNumber)
                .collection("Employee")
                .document(employeeNumber)
                .collection("Holidays")
                .document(year)
                .collection("Festival")
                .document(monthNumber.toString())
                .collection("Details")


            // Fetch the data
            val snapshot = detailsCollectionRef.get().await()

            // Map the documents to the Festival objects
            val festivals = snapshot.documents.mapNotNull { it.toObject(Festival::class.java) }

            Log.d(tag, "Festivals: $festivals")
            onSuccess(festivals)


            Log.d(tag, "Festivals: $festivals")
            onSuccess(festivals)


        } catch (e: Exception) {
            Log.d("Holiday", e.message.toString())
            onFailure(e)
        }
    }

    suspend fun getRegularHolidaysForYear(
        employeeNumber: String,
        year: String,
        onSuccess: (RegularHolidayItems?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            val holidaysRef = database
                .collection("Members")
                .document(updateAdminNumber)
                .collection("Employee")
                .document(employeeNumber)
                .collection("Holidays")
                .document(year)
                .collection("RegularHolidays") // Assuming you stored holidays here

            // Fetch all documents
            val querySnapshot = holidaysRef.get().await()

            if (querySnapshot.isEmpty) {
                Log.d(tag, "No regular holidays found")
                onSuccess(null)
            } else {
                // Sort the documents by document ID (ascending)
                val sortedDocuments = querySnapshot.documents.sortedByDescending { it.id.toLong() }
                Log.d(tag, "Regular: ${sortedDocuments.size}")
                val mostRecentHoliday = sortedDocuments.firstOrNull()?.toObject(RegularHolidayItems::class.java)
                Log.d(tag, "Fetched Most Recent Regular Holiday: $mostRecentHoliday")
                onSuccess(mostRecentHoliday)
            }

        } catch (e: Exception) {
            Log.d("Holiday", e.message.toString())
            onFailure(e)
        }
    }



    suspend fun getMonthlyWorkHours(
        employeePhoneNumber: String,
        year: String,
        month: String
    ): Map<String, Long> {
        val resultMap = mutableMapOf<String, Long>()

        try {
            val collectionRef = database.collection("Members")
                .document(updateAdminNumber)
                .collection("Employee")
                .document(employeePhoneNumber)
                .collection("Attendance")
                .document(year)
                .collection(month)

            // Fetch all documents in the collection
            val querySnapshot = collectionRef.get().await()

            // Map each document to {date, totalWorkDuration}
            for (document in querySnapshot.documents) {
                val date = document.id // The document ID is the date (e.g., "1", "2", etc.)
                val totalWorkDuration = document.getLong("TotalWorkDuration") ?: 0L
                resultMap[date] = totalWorkDuration
            }
            Log.d(tag, "Monthly work hours: $resultMap")
        } catch (e: Exception) {
            Log.e("AttendanceRepository", "Error fetching monthly work hours: ${e.message}")
        }

        return resultMap
    }



    suspend fun getLeavesForMonthAndYear(
        employeePhoneNumber: String,
        year: String,
        month: String
    ): List<EmployeeLeaveData> {
        val leaveList = mutableListOf<EmployeeLeaveData>()
        try {
            val yearDocRef = database.collection("Members")
                .document(updateAdminNumber)
                .collection("Employee")
                .document(employeePhoneNumber)
                .collection("Leave")
                .document(year)

            val monthDocRef = yearDocRef.collection(month)
            Log.d(tag, "Month Document Reference Leave: ${monthDocRef.path}")
            val querySnapshot = monthDocRef.get().await()
            Log.d(tag, "Query Snapshot Leave: ${querySnapshot.documents}")

            for (monthDocument in querySnapshot.documents) {
                Log.d(tag, "Month Document Leave: ${monthDocument.data}")
                val entriesCollection = monthDocument.reference.collection("Entries")
                val entriesSnapshot = entriesCollection.get().await()
                Log.d(tag, "Entries Snapshot Leave: ${entriesSnapshot.documents}")

                for (entry in entriesSnapshot.documents) {
                    Log.d(tag, "Entry Leave: ${entry.data}, ${entry.reference}")
                    val leaveData = entry.toObject(EmployeeLeaveData::class.java)
                    leaveData?.let { leaveList.add(it) }
                }
            }
            Log.d(tag, "Leave List: $leaveList")
        } catch (e: Exception) {
            Log.e(tag, "Error retrieving leaves: ${e.message}")
        }
        return leaveList
    }

    suspend fun fetchRecentWorkHourData(
        employeePhoneNumber: String,
        month: String,
        year: String
    ): AddWorkingHourDataClass? {
        return try {
            val workHourCollectionRef = database
                .collection("Members")
                .document(updateAdminNumber)
                .collection("Employee")
                .document(employeePhoneNumber)
                .collection("WorkHour")

            val querySnapshot = workHourCollectionRef.get().await()


            // Parse and filter the documents
            val filteredData = querySnapshot.documents.mapNotNull { document ->
                val documentId = document.id
                try {
                    // Parse document ID as date (d/M/yyyy)
                    val workHourData = document.toObject(AddWorkingHourDataClass::class.java)
                    Log.d(tag, "Document ID: $documentId, $workHourData")
                    workHourData?.let {
                        val documentDate =
                            SimpleDateFormat("d/M/yyyy", Locale.ENGLISH).parse(it.date)
                        Log.d(tag, "Document Date: $documentDate")

                        // Extract the document's month and year for comparison
                        val calendar = Calendar.getInstance().apply { time = documentDate }
                        val documentMonth =
                            SimpleDateFormat("MMM", Locale.ENGLISH).format(calendar.time)
                        val documentYear = calendar.get(Calendar.YEAR).toString()
                        Log.d(tag, "Document Month: $documentMonth, Document Year: $documentYear")

                        // Match month and year
                        if (documentMonth == month && documentYear == year) {
                            document.toObject(AddWorkingHourDataClass::class.java)
                        } else {
                            null
                        }
                    }
                } catch (e: Exception) {
                    null // Skip invalid document IDs
                }
            }
            Log.d(tag, "Recent Work Hour Data: $filteredData")
            // Return the most recent entry (sorted by date)
            val workHour=filteredData.maxByOrNull { workHour ->
                SimpleDateFormat("d/M/yyyy", Locale.ENGLISH).parse(workHour.date)?.time ?: 0
            }
            Log.d(tag, "Most Recent Work Hour Data: $workHour")
            workHour

        } catch (e: Exception) {
            throw e // Re-throw exception for the ViewModel to handle
        }
    }


}