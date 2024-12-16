package com.rach.firmmanagement.repository

import android.annotation.SuppressLint
import android.icu.util.Calendar
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.rach.firmmanagement.dataClassImp.OutForWork
import com.rach.firmmanagement.dataClassImp.PunchInPunchOut
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale

class PunchInPunchOutRepository {

    private val database = FirebaseFirestore.getInstance()

    private val currentUser = FirebaseAuth.getInstance().currentUser?.phoneNumber.toString()

    private val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    private val currentMonth =
        SimpleDateFormat("MMM", Locale.getDefault()).format(Calendar.getInstance().time)

    val updateCurrentUserNumber = when {
        currentUser.startsWith("+91") -> currentUser.removePrefix("+91")
        else ->
            currentUser
    }

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy",Locale.getDefault())
    private val AjjDate = dateFormat.format(Date())

    @SuppressLint("NewApi")
    private val todayDate = AjjDate.replace('/', '-')


    /*
    suspend fun functionPunchIn(
        adminPhoneNumber: String,
        punchInData:PunchInPunchOut,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {

        try {
            val newNumberOfAdmin = when {
                adminPhoneNumber.startsWith("+91") -> adminPhoneNumber
                else ->
                    "+91$adminPhoneNumber"
            }

            /*
            val data = database.collection("Members")
                .document(newNumberOfAdmin)
                .collection("Employee")
                .document(updateCurrentUserNumber)
                .collection("Attendance")
                .document("$currentYear")
                .collection(currentMonth)
                .document(todayDate)


            data.set(punchInData.copy(punchOutTime = null)).await()
            onSuccess()
            */
            val collectionRef = database.collection("Members")
                .document(newNumberOfAdmin)
                .collection("Employee")
                .document(updateCurrentUserNumber)
                .collection("Attendance")
                .document("$currentYear")
                .collection(currentMonth)
                .document()
                .collection("Entries")
            Log.d("Att", todayDate)
            collectionRef.document(punchInData.punchTime.toString()).set(punchInData)
                .addOnSuccessListener {
                    onSuccess()
                    Log.d("Att", "Document with timestamp ${punchInData.punchTime} added successfully!")
                }
                .addOnFailureListener { e ->
                    Log.d("Att", "Error adding document: ${e.message}")
                    onFailure()
                }
            //Log.d("Att", "$adminPhoneNumber - $updateCurrentUserNumber - $currentYear - $currentMonth - $punchInData = ${data.get()}")

        }catch (e:Exception){
            Log.d("Att", "Failed to punchIn- ${e.message}")
            onFailure()
        }

    }

     */
    suspend fun functionPunchIn(
        adminPhoneNumber: String,
        punchInData:PunchInPunchOut,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {

        try {
            val newNumberOfAdmin = when {
                adminPhoneNumber.startsWith("+91") -> adminPhoneNumber
                else ->
                    "+91$adminPhoneNumber"
            }

            val yearDocRef = database.collection("Members")
                .document(newNumberOfAdmin)
                .collection("Employee")
                .document(updateCurrentUserNumber)
                .collection("Attendance")
                .document("$currentYear")

            val monthDocRef = yearDocRef.collection(currentMonth).document(todayDate) // Assuming `todayDate` is unique per day

// Ensure year and month documents exist
            yearDocRef.set(mapOf("created" to true)) // Placeholder field for the year document
                .addOnSuccessListener {
                    Log.d("Att", "Year document $currentYear created successfully.")

                    monthDocRef.set(mapOf("created" to true)) // Placeholder field for the month document
                        .addOnSuccessListener {
                            Log.d("Att", "Month document $currentMonth created successfully.")

                            // Now add the subcollection data
                            val collectionRef = monthDocRef.collection("Entries")
                            collectionRef.document(Timestamp.now().seconds.toString()).set(punchInData)
                                .addOnSuccessListener {
                                    onSuccess()
                                    Log.d("Att", "Document with timestamp ${punchInData.punchTime} added successfully!")
                                }
                                .addOnFailureListener { e ->
                                    Log.d("Att", "Error adding document: ${e.message}")
                                    onFailure()
                                }
                        }
                        .addOnFailureListener { e ->
                            Log.d("Att", "Error creating month document: ${e.message}")
                        }
                }
                .addOnFailureListener { e ->
                    Log.d("Att", "Error creating year document: ${e.message}")
                }


        }catch (e:Exception){
            Log.d("Att", "Failed to punchIn- ${e.message}")
            onFailure()
        }
    }

    suspend fun isPunchInPossible(
        adminPhoneNumber: String,
    ): Boolean {
        val newNumberOfAdmin = if (adminPhoneNumber.startsWith("+91")) {
            adminPhoneNumber
        } else {
            "+91$adminPhoneNumber"
        }

        return try {
            val collectionRef = database.collection("Members")
                .document(newNumberOfAdmin)
                .collection("Employee")
                .document(updateCurrentUserNumber)
                .collection("Attendance")
                .document("$currentYear")
                .collection(currentMonth)
                .document(todayDate)
                .collection("Entries")
            Log.d("Attt", todayDate)

            val querySnapshot = collectionRef
                .orderBy(FieldPath.documentId(), Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .await() // Use coroutine-friendly await() to fetch data

            val mostRecentDoc = querySnapshot.documents.firstOrNull()
            Log.d("Attt", "${querySnapshot.isEmpty}")
            Log.d("Attt", "Most recent document: ${mostRecentDoc?.get("punchOutTime")}, ${mostRecentDoc?.get("punchTime")}")
            mostRecentDoc?.get("punchOutTime") != null || mostRecentDoc?.get("punchTime") == null
        } catch (e: Exception) {
            Log.d("Attt", "Error fetching most recent document: ${e.message}")
            false // Return false in case of an error
        }
    }

    suspend fun updateMostRecentPunchIn(
        adminPhoneNumber: String,
        punchInData: PunchInPunchOut,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ){
        val newNumberOfAdmin = if (adminPhoneNumber.startsWith("+91")) {
            adminPhoneNumber
        } else {
            "+91$adminPhoneNumber"
        }

        try {
            val collectionRef = database.collection("Members")
                .document(newNumberOfAdmin)
                .collection("Employee")
                .document(updateCurrentUserNumber)
                .collection("Attendance")
                .document("$currentYear")
                .collection(currentMonth)
                .document(todayDate)
                .collection("Entries")

            Log.d("Atty", todayDate)
            val querySnapshot = collectionRef
                .orderBy(FieldPath.documentId(), Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .await()

            val mostRecentDoc = querySnapshot.documents.firstOrNull()
            if (mostRecentDoc != null) {
                val documentRef = collectionRef.document(mostRecentDoc.id)

                // Update the document with the new punch-in data
                documentRef.set(punchInData, SetOptions.merge()).await()
                Log.d("Atty", "Most recent entry updated successfully!")
                onSuccess()
            }else{
                Log.d("Atty", "No most recent entry found.")
                onFailure()
            }
        } catch (e: Exception) {
            Log.d("Atty", "Error updating most recent entry: ${e.message}")
            onFailure()
        }
    }


    suspend fun punchOut(
        adminPhoneNumber: String,
        punchOutTime: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ){

       val updateAdminPhoneNumber = when{
           adminPhoneNumber.startsWith("+91") -> adminPhoneNumber
           else ->
               "+91$adminPhoneNumber"
       }

        /*
       try {
           val data = database.collection("Members")
               .document(updateAdminPhoneNumber)
               .collection("Employee")
               .document(updateCurrentUserNumber)
               .collection("Attendance")
               .document("$currentYear")
               .collection(currentMonth)
               .document(todayDate)


           val result = data.get().await()

           if (result.exists() && result.get("punchOutTime") == null){
               data.update("punchOutTime",punchOutTime).await()
               onSuccess()
           }else{
               onFailure()
           }


       }catch (_:Exception){
           onFailure()
       }
         */
    try{
        val collectionRef = database.collection("Members")
            .document(updateAdminPhoneNumber)
            .collection("Employee")
            .document(updateCurrentUserNumber)
            .collection("Attendance")
            .document("$currentYear")
            .collection(currentMonth)
            .document(todayDate)
            .collection("Entries")

        Log.d("Att", todayDate)

        Log.d("Att1", "Month reference: $collectionRef")
        // Fetch the most recent document
        val querySnapshot = collectionRef
            .orderBy(FieldPath.documentId(), Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .await()

        val mostRecentDoc = querySnapshot.documents.firstOrNull()

        if (mostRecentDoc != null) {
            val documentRef = collectionRef.document(mostRecentDoc.id)

            // Check if the punchOutTime field is already set
            val punchOutTimeExists = mostRecentDoc.get("punchOutTime") != null

            if (!punchOutTimeExists) {
                // Update the punchOutTime field
                documentRef.update("punchOutTime", punchOutTime).await()
                Log.d("Att", "Punch out time updated successfully for the most recent entry!")
                onSuccess()
            } else {
                Log.d("Att", "Punch out time already exists.")
                onFailure()
            }
            updateTotalWorkDuration(
                adminPhoneNumber,
                calculateMinutesBetweenTimes(
                    mostRecentDoc.get("punchTime").toString(),
                    punchOutTime
                )
            )
        } else {
            Log.d("Att", "No entries found to update punch out.")
            onFailure()
        }

    } catch (e: Exception) {
        Log.d("Att", "Error punching out: ${e.message}")
        onFailure()
    }


    }

    suspend fun setOutForWork(
        adminPhoneNumber: String,
        newValue:Int,
        name: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ){

        val updateAdminPhoneNumber = when{
            adminPhoneNumber.startsWith("+91") -> adminPhoneNumber
            else ->
                "+91$adminPhoneNumber"
        }

        val TAG="Atto"

        try {
            val documentRef = database.collection("Members")
                .document(updateAdminPhoneNumber)
                .collection("Employee")
                .document(updateCurrentUserNumber)
                .collection("Attendance")
                .document("$currentYear")
                .collection(currentMonth)
                .document(todayDate)

            val newOutOfWork = OutForWork(
                date = todayDate,
                duration = newValue, // This is the new value to be added
                name = name // Replace with the actual name if needed
            )
            updateTotalWorkDuration(adminPhoneNumber, newValue.toLong()*60)
            documentRef.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // Get the previous value or default to 0 if not present
                    val currentOutOfWork = documentSnapshot.toObject(OutForWork::class.java)
                    val previousDuration = currentOutOfWork?.duration ?: 0

                    // Calculate the updated duration
                    val updatedDuration = previousDuration + (newOutOfWork.duration ?: 0)

                    // Create a new OutOfWork object with the updated duration
                    val updatedOutOfWork = newOutOfWork.copy(duration = updatedDuration)

                    // Update the document
                    documentRef.set(updatedOutOfWork, SetOptions.merge())
                        .addOnSuccessListener {
                            // Successfully updated
                            Log.d(TAG, "OutForWork updated to $updatedOutOfWork")
                            onSuccess()
                        }
                        .addOnFailureListener { e ->
                            // Handle the error
                            Log.e(TAG, "Error updating OutForWork", e)
                            onFailure()
                        }
                } else {
                    // Document does not exist, initialize the value
                    documentRef.set(newOutOfWork)
                        .addOnSuccessListener {
                            Log.d(TAG, "OutForWork initialized to $newOutOfWork")
                            onSuccess()
                        }
                        .addOnFailureListener { e ->
                            Log.e("Atto", "Error initializing OutForWork", e)
                            onFailure()
                        }
                }
            }.addOnFailureListener { e ->
                // Handle error in fetching the document
                Log.e("Atto", "Error fetching document", e)
                onFailure()
            }

        }catch (e: Exception) {
            Log.d("Att", "Error in out of Work: ${e.message}")
            onFailure()
        }
    }


    suspend fun updateTotalWorkDuration(
        adminPhoneNumber: String,
        newDuration: Long,
    ) {
        val updateAdminPhoneNumber = if (adminPhoneNumber.startsWith("+91")) adminPhoneNumber else "+91$adminPhoneNumber"

        try {
            val collectionRef = database.collection("Members")
                .document(updateAdminPhoneNumber)
                .collection("Employee")
                .document(updateCurrentUserNumber)
                .collection("Attendance")
                .document("$currentYear")
                .collection(currentMonth)
                .document(todayDate)

            // Access the "TotalWorkDuration" field in the document
            val snapshot = collectionRef.get().await()

            // Fetch the current value of "TotalWorkDuration" or default to 0
            val previousDuration = snapshot.getLong("TotalWorkDuration") ?: 0

              // Calculate the updated duration
            val updatedDuration = previousDuration + newDuration

            // Update the "TotalWorkDuration" field in the document
            collectionRef.update("TotalWorkDuration", updatedDuration).await()

            // If successful, invoke the success callback
            Log.d("Att", "TotalWorkDuration updated to $updatedDuration, $newDuration")

        } catch (e: Exception) {
            Log.d("Att", "Error updating TotalWorkDuration: ${e.message}")
        }
    }


    @SuppressLint("NewApi")
    private fun calculateMinutesBetweenTimes(startTime: String, endTime: String): Long {
        // Define the formatter for the time strings
        val formatter = DateTimeFormatter.ofPattern("hh:mm:ss a")

        // Parse the time strings into LocalTime
        val start = LocalTime.parse(startTime, formatter)
        val end = LocalTime.parse(endTime, formatter)

        // Calculate the difference in minutes
        val totalMinutes = ChronoUnit.MINUTES.between(start, end)

        // Handle cases where the end time is on the next day
        val adjustedMinutes = if (totalMinutes < 0) totalMinutes + 1440 else totalMinutes


        return adjustedMinutes.toLong()
    }

    /*
    suspend fun loadDaysWiseAttendance(
        adminPhoneNumber: String,
        onSuccess: (List<PunchInPunchOut>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val formattedAdminNumber = when {
            adminPhoneNumber.startsWith("+91") -> adminPhoneNumber
            else -> "+91$adminPhoneNumber"
        }

        try {
            val dataRef = database.collection("Members")
                .document(formattedAdminNumber)
                .collection("Employee")
                .document(updateCurrentUserNumber)
                .collection("Attendance")
                .document("$currentYear")
                .collection(currentMonth)


            val result = dataRef.orderBy("date").get().await()

            Log.d("Att", "$adminPhoneNumber - $updateCurrentUserNumber - $currentYear - $currentMonth - ${result.documents.toString()}")
            // Convert the Firestore documents to a list of PunchInPunchOut objects
            val punchInPunchOutList = result.documents.mapNotNull { documentSnapshot ->
                documentSnapshot.toObject(PunchInPunchOut::class.java)
            }

            // Pass the sorted list of PunchInPunchOut objects to the UI
            onSuccess(punchInPunchOutList)

        } catch (e: Exception) {
            onFailure(e)
        }
    }
     */

    suspend fun loadOutOfWorkData(
        adminPhoneNumber: String,
        onSuccess: (List<OutForWork>) -> Unit,
        onFailure: () -> Unit
    ) {
        val newNumberOfAdmin = if (adminPhoneNumber.startsWith("+91")) {
            adminPhoneNumber
        } else {
            "+91$adminPhoneNumber"
        }

        try {
            // Mutable list to collect OutOfWork objects
            val outOfWorkList = mutableListOf<OutForWork>()

            // Reference to the Firestore collection path
            val monthRef = database.collection("Members")
                .document(newNumberOfAdmin)
                .collection("Employee")
                .document(updateCurrentUserNumber)
                .collection("Attendance")
                .document("$currentYear")
                .collection(currentMonth)

            // Fetch all documents in the current month collection
            val dailySnapshots = monthRef.get().await()

            // Iterate through all documents
            for (document in dailySnapshots.documents) {
                val outOfWorkData = document.toObject(OutForWork::class.java)
                if (outOfWorkData != null && !outOfWorkData.date.isNullOrEmpty()) {
                    outOfWorkList.add(outOfWorkData)
                }
            }

            // Call the success callback with the collected data
            Log.d("Atto", outOfWorkList.toString())
            onSuccess(outOfWorkList)
        } catch (e: Exception) {
            // Log the exception and call the failure callback
            Log.e("Atto", "Error loading OutOfWork data", e)
            onFailure()
        }
    }

    suspend fun loadDaysWiseAttendance(
        adminPhoneNumber: String,
        onSuccess: (List<PunchInPunchOut>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {


        val newNumberOfAdmin = if (adminPhoneNumber.startsWith("+91")) {
            adminPhoneNumber
        } else {
            "+91$adminPhoneNumber"
        }

        try {
            val attendanceData = mutableListOf<PunchInPunchOut>()

            // Reference to the monthly collection
            val monthRef = database.collection("Members")
                .document(newNumberOfAdmin)
                .collection("Employee")
                .document(updateCurrentUserNumber)
                .collection("Attendance")
                .document("$currentYear")
                .collection(currentMonth)

            Log.d("Att2", "Querying path: ${monthRef.path}")

            // Fetch all documents for the current month
            val dailySnapshots = monthRef.get().await() // Use await() for coroutine

            if (dailySnapshots.isEmpty) {
                Log.d("Att", "No daily snapshots found for this month.")
            } else {
                // Iterate over each daily document
                for (dailyDocument in dailySnapshots.documents) {
                    val todayDate = dailyDocument.id // Get the date for the current document
                    val entriesRef = dailyDocument.reference.collection("Entries")

                    // Fetch all entries within the daily collection
                    val entriesSnapshot = entriesRef.get().await() // Use await()
                    Log.d("Att", "id: $todayDate")
                    if (entriesSnapshot.isEmpty) {
                        Log.d("Att", "No entries found for date")
                    } else {
                        // Convert each entry to PunchInPunchOut objects and add to the list
                        entriesSnapshot.documents.mapNotNullTo(attendanceData) { entryDocument ->
                            try {
                                entryDocument.toObject(PunchInPunchOut::class.java)?.apply {
                                    Log.d("Att", "Parsed PunchInPunchOut: $this from $todayDate")
                                }
                            } catch (e: Exception) {
                                Log.d("Att", "Error parsing document ${entryDocument.id}: ${e.message}")
                                null
                            }
                        }
                    }
                }
                Log.d("Att", "Successfully loaded ${attendanceData.size} attendance records.")
                onSuccess(attendanceData)
            }
        } catch (e: Exception) {
            Log.d("Att", "Error loading attendance data: ${e.message}")
            onFailure(e)
        }
    }

}