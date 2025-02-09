package com.rach.firmmanagement.repository

import android.annotation.SuppressLint
import android.icu.util.Calendar
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.gson.Gson
import com.rach.firmmanagement.dataClassImp.AddTaskDataClass
import com.rach.firmmanagement.dataClassImp.AdvanceMoneyData
import com.rach.firmmanagement.dataClassImp.EmployeeLeaveData
import com.rach.firmmanagement.dataClassImp.Expense
import com.rach.firmmanagement.dataClassImp.ExpenseItem
import com.rach.firmmanagement.dataClassImp.GeofenceItems
import com.rach.firmmanagement.dataClassImp.PunchInPunchOut
import com.rach.firmmanagement.dataClassImp.Remark
import com.rach.firmmanagement.dataClassImp.ViewAllEmployeeDataClass
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.text.replace

class EmployeeRepository() {

    val dateFormat = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
    val date: String = dateFormat.format(Date())

    val database = FirebaseFirestore.getInstance()
    val currentUserNumber = FirebaseAuth.getInstance().currentUser?.phoneNumber.toString()


    val employeeNumber = when {
        currentUserNumber.startsWith("+91") -> currentUserNumber.removePrefix("+91")
        else ->
            currentUserNumber
    }

    suspend fun raiseALeave(
        adminPhoneNumber: String,
        firmName: String,
        employeeleaveData: EmployeeLeaveData,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {

        val updateAdminNumber = if (adminPhoneNumber.startsWith("+91")) {
            adminPhoneNumber
        } else {
            "+91$adminPhoneNumber"
        }

        val firmRef =database.collection("Firms")
            .document(firmName)
            .collection("pendingLeaves")
        val docRef = firmRef.add(employeeleaveData).await() // Adds a document and gets reference
        val docId = docRef.id

        // Update the document with the new ID field
        docRef.update("id", docId).await()

        val leaveDate = employeeleaveData.startingDate ?: todayDate.replace('/', '-')
        val dateParts= leaveDate.split("-")
        val targetMonth = SimpleDateFormat("MMM", Locale.getDefault())
            .format(SimpleDateFormat("M", Locale.getDefault()).parse(dateParts[1])!!) // Converts "2" → "Feb"
        val targetYear = dateParts[2]

        try {
            val yearDocRef = database.collection("Members")
                .document(updateAdminNumber)
                .collection("Employee")
                .document(employeeNumber)
                .collection("Leave")
                .document(targetYear)

            Log.d("leave","Year Doc Ref: ${yearDocRef.path}")

            val monthDocRef = yearDocRef.collection(targetMonth)
                .document(employeeleaveData.startingDate?.replace('/', '-').toString())

            yearDocRef.set(mapOf("created" to true)) // Placeholder field for the year document
                .addOnSuccessListener {
                    Log.d("leave", "Year document $currentYear created successfully.")

                    monthDocRef.set(mapOf("created" to true)) // Placeholder field for the month document
                        .addOnSuccessListener {
                            Log.d("leave", "Month document $currentMonth created successfully.")

                            // Now add the subcollection data
                            val collectionRef = monthDocRef.collection("Entries")
                            collectionRef.document(docId).set(employeeleaveData.copy(id=docId))
                                .addOnSuccessListener {
                                    onSuccess()
                                    Log.d("leave", "Document with timestamp $employeeleaveData added successfully!")
                                }
                                .addOnFailureListener { e ->
                                    Log.d("leave", "Error adding document: ${e.message}")
                                    onFailure()
                                }
                        }
                        .addOnFailureListener { e ->
                            Log.d("leave", "Error creating month document: ${e.message}")
                        }
                }
                .addOnFailureListener { e ->
                    Log.d("leave", "Error creating year document: ${e.message}")
                }

            onSuccess()
        } catch (_: Exception) {
            onFailure()
        }

    }

    suspend fun getLeaves(
        adminPhoneNumber: String,
        onSuccess: (List<EmployeeLeaveData>) -> Unit,
        onFailure: () -> Unit,
        selectedMonth: String,
        from: String,
        to: String,
    ) {
        val updateAdminNumber = if (adminPhoneNumber.startsWith("+91")) {
            adminPhoneNumber
        } else {
            "+91$adminPhoneNumber"
        }
        val leaveData = mutableListOf<EmployeeLeaveData>()
        try {
            if (selectedMonth.isEmpty()) {
                Log.d("leave", "selectedMonth is empty $from $to")
                val fromDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(from)
                val toDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(to)
                val calendar = java.util.Calendar.getInstance()

                calendar.time = fromDate
                Log.d("leave", "fromDate: $fromDate, toDate: $toDate")
                while (calendar.time <= toDate) {
                    val targetYear = calendar.get(java.util.Calendar.YEAR).toString()
                    val targetMonth =
                        SimpleDateFormat("MMM", Locale.getDefault()).format(calendar.time)
                    val targetDate =
                        SimpleDateFormat("d-M-yyyy", Locale.getDefault()).format(calendar.time)

                    val entriesSnapshot = database.collection("Members")
                        .document(updateAdminNumber)
                        .collection("Employee")
                        .document(employeeNumber.toString())
                        .collection("Leave")
                        .document(targetYear)
                        .collection(targetMonth)
                        .document(targetDate)
                        .collection("Entries")
                        .get()
                        .await()

                    for (entryDoc in entriesSnapshot.documents) {
                        val data = entryDoc.data
                        Log.d("leave", "data: $data, ${data?.get("status")}")
                        if (data != null) {
                            val leave = EmployeeLeaveData(
                                emlPhoneNumber = data["emlPhoneNumber"] as? String ?: "",
                                startingDate = data["startingDate"] as? String ?: "",
                                endDate = data["endDate"] as? String ?: "",
                                reason = data["reason"] as? String ?: "",
                                status = (data["status"] as? Number)?.toInt() ?: 0,
                                type = data["type"] as? String ?: "",
                                currentDate = data["currentDate"] as? String ?: ""
                            )
                            Log.d("leave", "leave: $leave")
                            leaveData.add(leave)
                        }
                    }

                    calendar.add(java.util.Calendar.DATE, 1)
                }

            } else {
                val parts = selectedMonth.split(" ")
                val targetMonth = parts[0]
                val targetYear = parts[1]

                val yearDocRef = database.collection("Members")
                    .document(updateAdminNumber)
                    .collection("Employee")
                    .document(employeeNumber)
                    .collection("Leave")
                    .document(targetYear)

                Log.d("leave", "Year Doc Ref: ${yearDocRef.path}")

                val monthDocSnapshot = yearDocRef.collection(targetMonth)
                    .get()
                    .await()

                Log.d("leave", "Month Doc Snapshot: ${monthDocSnapshot.documents}")
                processDateDocuments(monthDocSnapshot, leaveData)
            }

            Log.d("leave", "Final list: $leaveData")
            onSuccess(leaveData)

        } catch (e: Exception) {
            Log.e("leave", "Error fetching expenses: ${e.message}", e)
            onFailure()
        }
    }

    private suspend fun processDateDocuments(
        dateDocuments: QuerySnapshot,
        allLeaves: MutableList<EmployeeLeaveData>
    ) {
        for (dateDoc in dateDocuments.documents) {
            try {
                val entries = dateDoc.reference.collection("Entries").get().await()
                for (entryDoc in entries.documents) {
                    val data = entryDoc.data
                    if (data != null) {
                        val leave = EmployeeLeaveData(
                            emlPhoneNumber = data["emlPhoneNumber"] as? String ?: "",
                            startingDate = data["startingDate"] as? String ?: "",
                            endDate = data["endDate"] as? String ?: "",
                            reason = data["reason"] as? String ?: "",
                            status = data["status"] as? Int ?: 0,
                            type = data["type"] as? String ?: "",
                            currentDate = data["currentDate"] as? String ?: ""
                        )
                        allLeaves.add(leave)
                    }
                }
            } catch (e: Exception) {
                Log.e("leave", "Error processing date document: ${e.message}", e)
            }
        }
    }

    suspend fun raiseAdvanceMoney(
        adminPhoneNumber: String,
        firmName: String,
        advanceMoneyData: AdvanceMoneyData,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {

        val updateAdminNumber = if (adminPhoneNumber.startsWith("+91")) {
            adminPhoneNumber
        } else {
            "+91$adminPhoneNumber"
        }

        Log.d("advance", "advanceMoneyData: $advanceMoneyData, $firmName")
        val firmRef =database.collection("Firms")
            .document(firmName)
            .collection("pendingAdvances")
        val docRef = firmRef.add(advanceMoneyData).await() // Adds a document and gets reference
        val docId = docRef.id

        // Update the document with the new ID field
        docRef.update("id", docId).await()

        val advDate = advanceMoneyData.date ?: todayDate.replace('/', '-')
        val dateParts= advDate.split("-")
        val targetMonth = SimpleDateFormat("MMM", Locale.getDefault())
            .format(SimpleDateFormat("M", Locale.getDefault()).parse(dateParts[1])!!) // Converts "2" → "Feb"
        val targetYear = dateParts[2]
        try {

            val yearDocRef = database.collection("Members")
                .document(updateAdminNumber)
                .collection("Employee")
                .document(employeeNumber)
                .collection("Advance")
                .document(targetYear)

            Log.d("advance","Year Doc Ref: ${yearDocRef.path}")

            val monthDocRef = yearDocRef.collection(targetMonth)
                .document(advanceMoneyData.date?.replace('/', '-').toString())

            yearDocRef.set(mapOf("created" to true)) // Placeholder field for the year document
                .addOnSuccessListener {
                    Log.d("advance", "Year document $targetYear created successfully.")

                    monthDocRef.set(mapOf("created" to true)) // Placeholder field for the month document
                        .addOnSuccessListener {
                            Log.d("advance", "Month document $targetMonth created successfully.")

                            // Now add the subcollection data
                            val collectionRef = monthDocRef.collection("Entries")
                            collectionRef.document(docId).set(advanceMoneyData.copy(id=docId))
                                .addOnSuccessListener {
                                    onSuccess()
                                    Log.d("advance", "Document with timestamp $advanceMoneyData added successfully!")
                                }
                                .addOnFailureListener { e ->
                                    Log.d("advance", "Error adding document: ${e.message}")
                                    onFailure()
                                }
                        }
                        .addOnFailureListener { e ->
                            Log.d("advance", "Error creating month document: ${e.message}")
                        }
                }
                .addOnFailureListener { e ->
                    Log.d("advance", "Error creating year document: ${e.message}")
                }

            onSuccess()

        } catch (_: Exception) {
            onFailure()

        }

    }

    suspend fun getAdvance(
        adminPhoneNumber: String,
        onSuccess: (List<AdvanceMoneyData>) -> Unit,
        onFailure: () -> Unit,
        selectedMonth: String,
        from: String,
        to: String,
    ) {
        val updateAdminNumber = if (adminPhoneNumber.startsWith("+91")) {
            adminPhoneNumber
        } else {
            "+91$adminPhoneNumber"
        }
        val leaveData = mutableListOf<AdvanceMoneyData>()
        try {
            if (selectedMonth.isEmpty()) {
                Log.d("leave", "selectedMonth is empty $from $to")
                val fromDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(from)
                val toDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(to)
                val calendar = java.util.Calendar.getInstance()

                calendar.time = fromDate
                Log.d("advance", "fromDate: $fromDate, toDate: $toDate")
                while (calendar.time <= toDate) {
                    val targetYear = calendar.get(java.util.Calendar.YEAR).toString()
                    val targetMonth =
                        SimpleDateFormat("MMM", Locale.getDefault()).format(calendar.time)
                    val targetDate =
                        SimpleDateFormat("d-M-yyyy", Locale.getDefault()).format(calendar.time)

                    val entriesSnapshot = database.collection("Members")
                        .document(updateAdminNumber)
                        .collection("Employee")
                        .document(employeeNumber.toString())
                        .collection("Advance")
                        .document(targetYear)
                        .collection(targetMonth)
                        .document(targetDate)
                        .collection("Entries")
                        .get()
                        .await()

                    for (entryDoc in entriesSnapshot.documents) {
                        val data = entryDoc.data
                        if (data != null) {
                            val advance = AdvanceMoneyData(
                                reason = data["reason"] as? String ?: "",
                                amount = data["amount"] as? String ?: "",
                                date = data["date"] as? String ?: "",
                                emplPhoneNumber = data["emplPhoneNumber"] as? String ?: "",
                                status = (data["status"] as? Number)?.toInt() ?: 0,
                                time = data["time"] as? String ?: ""
                            )
                            leaveData.add(advance)
                        }
                    }

                    calendar.add(java.util.Calendar.DATE, 1)
                }

            } else {
                val parts = selectedMonth.split(" ")
                val targetMonth = parts[0]
                val targetYear = parts[1]

                val yearDocRef = database.collection("Members")
                    .document(updateAdminNumber)
                    .collection("Employee")
                    .document(employeeNumber)
                    .collection("Advance")
                    .document(targetYear)

                Log.d("advance", "Year Doc Ref: ${yearDocRef.path}")

                val monthDocSnapshot = yearDocRef.collection(targetMonth)
                    .get()
                    .await()

                Log.d("advance", "Month Doc Snapshot: ${monthDocSnapshot.documents}")
                for (dateDoc in monthDocSnapshot.documents) {
                    try {
                        val entries = dateDoc.reference.collection("Entries").get().await()
                        for (entryDoc in entries.documents) {
                            val data = entryDoc.data
                            if (data != null) {
                                val advance = AdvanceMoneyData(
                                    reason = data["reason"] as? String ?: "",
                                    amount = data["amount"] as? String ?: "",
                                    date = data["date"] as? String ?: "",
                                    emplPhoneNumber = data["emplPhoneNumber"] as? String ?: "",
                                    status = (data["status"] as? Number)?.toInt() ?: 0,
                                    time = data["time"] as? String ?: ""
                                )
                                leaveData.add(advance)
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("advance", "Error processing date document: ${e.message}", e)
                    }
                }

            }

            Log.d("advance", "Final list: $leaveData")
            onSuccess(leaveData)

        } catch (e: Exception) {
            Log.e("advance", "Error fetching expenses: ${e.message}", e)
            onFailure()
        }
    }

    suspend fun loadTask(
        adminPhoneNumber: String,
        onSuccess: (List<AddTaskDataClass>) -> Unit,
        onFailure: () -> Unit
    ) {
        val updateAdminNumber = if (adminPhoneNumber.startsWith("+91")) {
            adminPhoneNumber
        } else {
            "+91$adminPhoneNumber"
        }

        try {
            val combinedTaskList = mutableListOf<AddTaskDataClass>()

            // Fetch employee-specific tasks
            val employeeTasksRef = database.collection("Members")
                .document(updateAdminNumber)
                .collection("Employee")
                .document(employeeNumber)
                .collection("Task")

            val employeeTaskSnapshot = employeeTasksRef.get().await()

            if (!employeeTaskSnapshot.isEmpty) {
                val employeeTaskList = employeeTaskSnapshot.documents.mapNotNull { doc ->
                    parseAddTaskDataClass(doc)
                }
                combinedTaskList.addAll(employeeTaskList)
            }

            Log.d("Task", "Combined task list: $combinedTaskList")
            // Pass the combined task list to onSuccess
            if (combinedTaskList.isNotEmpty()) {
                onSuccess(combinedTaskList)
            } else {
                onFailure()
            }
        } catch (e: Exception) {
            // Handle failure
            onFailure()
        }
    }

    suspend fun addRemark(
        adminPhoneNumber: String,
        employeePhone: String, // Required for non-common tasks
        taskId: String,
        isCommon: Boolean,
        newRemark: Remark,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        try {
            val updateAdminNumber = if (adminPhoneNumber.startsWith("+91")) {
                adminPhoneNumber
            } else {
                "+91$adminPhoneNumber"
            }
            val taskRef = database.collection("Members")
                    .document(updateAdminNumber)
                    .collection("Employee")
                    .document(employeePhone)
                    .collection("Task")
                    .document(taskId)
            Log.d("TAG", "fetched successful 1: ${taskRef.path}")
            // Fetch the current task data
            val snapshot = taskRef.get().await()
            Log.d("TAG", "fetched successful snapshot 1: ${snapshot.data}")
            val taskData = parseAddTaskDataClass(snapshot)
            Log.d("TAG", "fetched successful taskData 1: ${taskData.remarks}")

            // Append the new remark to the existing list
            val updatedRemarks = taskData.remarks.toMutableList()
            Log.d("TAG", "fetched successful updatedRemarks 1: ${updatedRemarks}")
            updatedRemarks.add(newRemark)
            Log.d("TAG", "fetched successful updatedRemarks 1: ${updatedRemarks}")
            Log.d("TAG", "fetched successful 1: ${updatedRemarks.map { remark -> remark.message }}")

            // Update Firestore with the new remarks list
            taskRef.update("remarks", updatedRemarks.map { remark ->
                mapOf(
                    "date" to remark.date,
                    "message" to remark.message,
                    "person" to remark.person
                )
            }).await()

            onSuccess()
        } catch (e: Exception) {
            onFailure()
        }
    }

    suspend fun fetchRemarks(
        adminPhoneNumber: String,
        isCommon: Boolean,
        taskId: String,
        employeePhone: String,
    ): List<Remark> {
        return try {
            val updateAdminNumber = if (adminPhoneNumber.startsWith("+91")) {
                adminPhoneNumber
            } else {
                "+91$adminPhoneNumber"
            }
            val taskRef=database.collection("Members")
                    .document(updateAdminNumber)
                    .collection("Employee")
                    .document(employeePhone)
                    .collection("Task")
                    .document(taskId)

            // Fetch the current task data from Firestore
            val snapshot = taskRef.get().await()
            val taskData = parseAddTaskDataClass(snapshot)


            Log.d("TAG", "fetched successful: ${taskData.remarks}")
            taskData.remarks

        } catch (e: Exception) {
            Log.e("TAG", "Failed to fetch remarks: ${e.message}")
            emptyList()
        }
    }
    /**
     * Manually parses a Firestore document into an AddTaskDataClass object.
     */
    fun parseAddTaskDataClass(document: DocumentSnapshot): AddTaskDataClass {
        return AddTaskDataClass(
            id = document.id, // Use the document ID as the task ID
            employeePhoneNumber = document.getString("employeePhoneNumber") ?: "",
            assignDate = document.getString("assignDate") ?: "",
            task = document.getString("task") ?: "",
            submitDate = document.getString("submitDate") ?: "",
            isCommon = document.getBoolean("isCommon") ?: false,
            status = document.getString("status") ?: "Open",
            remarks = parseRemarks(document.get("remarks"))
        )
    }

    /**
     * Manually parses a list of remarks.
     */
    private fun parseRemarks(remarksObject: Any?): List<Remark> {
        return if (remarksObject is List<*>) {
            remarksObject.mapNotNull { remark ->
                if (remark is Map<*, *>) {
                    Remark(
                        date = remark["date"] as? String ?: "",
                        message = remark["message"] as? String ?: "",
                        person = remark["person"] as? String ?: ""
                    )
                } else null
            }
        } else {
            emptyList()
        }
    }


    suspend fun punchInPunchOutEmplo(
        adminPhoneNumber: String,
        punchInPunchOut: PunchInPunchOut,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {

        val updateAdminNumber = if (adminPhoneNumber.startsWith("+91")) {
            adminPhoneNumber
        } else {
            "+91$adminPhoneNumber"
        }

        val formatDate = date.replace("/", "-")

        try {
            database.collection("Members")
                .document(updateAdminNumber)
                .collection("Attendance")
                .document(formatDate)
                .collection("Attendance History")
                .add(punchInPunchOut)
                .await()

            onSuccess()

        } catch (_: Exception) {
            onFailure()
        }


    }

    private val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    private val currentMonth =
        SimpleDateFormat("MMM", Locale.getDefault()).format(Calendar.getInstance().time)

    @SuppressLint("NewApi")
    private val todayDate = date.replace('/', '-')

    private val tag = "TAG"
    /*
    suspend fun raiseExpense(
        adminPhoneNumber: String,
        expense: Expense,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        try {
            val updateAdminNumber = if (adminPhoneNumber.startsWith("+91")) {
                adminPhoneNumber
            } else {
                "+91$adminPhoneNumber"
            }
            val yearRef = database.collection("Members")
                .document(updateAdminNumber)
                .collection("Employee")
                .document(employeeNumber)
                .collection("Expense")
                .document("$currentYear")
            val monthDocRef = yearRef.collection(currentMonth)
                .document(todayDate) // Assuming `todayDate` is unique per day

            Log.d(tag, "Employee: $employeeNumber, Admin: $adminPhoneNumber")
            // Ensure year and month documents exist
            yearRef.set(mapOf("created" to true)) // Placeholder field for the year document
                .addOnSuccessListener {
                    Log.d(tag, "Year document $currentYear created successfully.")

                    monthDocRef.set(mapOf("created" to true)) // Placeholder field for the month document
                        .addOnSuccessListener {
                            Log.d(tag, "Month document $currentMonth created successfully.")

                            // Now add the subcollection data
                            val collectionRef = monthDocRef.collection("Entries")
                            collectionRef.document(Timestamp.now().seconds.toString()).set(expense)
                                .addOnSuccessListener {
                                    onSuccess()
                                    Log.d(tag, "added successfully!")
                                }
                                .addOnFailureListener { e ->
                                    Log.d(tag, "Error adding document: ${e.message}")
                                    onFailure()
                                }
                        }
                        .addOnFailureListener { e ->
                            Log.d(tag, "Error creating month document: ${e.message}")
                        }
                }
                .addOnFailureListener { e ->
                    Log.d(tag, "Error creating year document: ${e.message}")
                }


        } catch (e: Exception) {
            Log.d(tag, "Failed to punchIn- ${e.message}")
            onFailure()
        }
    }

    */

    suspend fun raiseExpense(
        adminPhoneNumber: String,
        expense: Expense,
        firmName:String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {

        try {
            val updateAdminNumber = if (adminPhoneNumber.startsWith("+91")) {
                adminPhoneNumber
            } else {
                "+91$adminPhoneNumber"
            }

            if (expense.selectedDate.contains("/")) {
                Log.d("expense", "Expense date contains '/': ${expense.selectedDate}")
                Log.d("expense", "replace: ${expense.selectedDate.replace("/", "-")}")
                expense.selectedDate = expense.selectedDate.replace("/", "-")
                Log.d("expense", "Expense date after replace: ${expense.selectedDate}")
            }
            if (expense.selectedDate.split("-").size != 3) {
                expense.selectedDate=todayDate.toString()
            }

            val firmRef =database.collection("Firms")
                .document(firmName)
                .collection("pendingExpenses")
            val docRef = firmRef.add(expense).await() // Adds a document and gets reference
            val docId = docRef.id

            // Update the document with the new ID field
            docRef.update("id", docId).await()


            Log.d("expense", "Expense added successfully with ID: $docId, $expense")
            val expenseDate = expense.selectedDate
            val dateParts=expenseDate.split("-")
            val targetMonth = SimpleDateFormat("MMM", Locale.getDefault())
                .format(SimpleDateFormat("M", Locale.getDefault()).parse(dateParts[1])!!) // Converts "2" → "Feb"
            val targetYear = dateParts[2]
            val yearDocRef = database.collection("Members")
                .document(updateAdminNumber)
                .collection("Employee")
                .document(employeeNumber)
                .collection("Expense")
                .document(targetYear)


            val monthDocRef = yearDocRef.collection(targetMonth).document(expenseDate) // Assuming `todayDate` is unique per day

// Ensure year and month documents exist
            yearDocRef.set(mapOf("created" to true)) // Placeholder field for the year document
                .addOnSuccessListener {
                    Log.d("expense", "Year document $currentYear created successfully.")

                    monthDocRef.set(mapOf("created" to true)) // Placeholder field for the month document
                        .addOnSuccessListener {
                            Log.d("expense", "Month document $currentMonth created successfully.")

                            // Now add the subcollection data
                            val collectionRef = monthDocRef.collection("Entries")
                            collectionRef.document(docId).set(expense.copy(id=docId))
                                .addOnSuccessListener {
                                    onSuccess()
                                    Log.d("expense", "Document with timestamp $expense added successfully!")
                                }
                                .addOnFailureListener { e ->
                                    Log.d("expense", "Error adding document: ${e.message}")
                                    onFailure()
                                }
                        }
                        .addOnFailureListener { e ->
                            Log.d("expense", "Error creating month document: ${e.message}")
                        }
                }
                .addOnFailureListener { e ->
                    Log.d("expense", "Error creating year document: ${e.message}")
                }


        }catch (e:Exception){
            Log.d("expense", "Failed to punchIn- ${e.message}")
            onFailure()
        }
    }
    suspend fun getExpensesForMonth(
        adminPhoneNumber: String,
        year: String,
        month: String,
        onSuccess: (List<Expense>) -> Unit,
        onFailure: () -> Unit
    ) {
        try {
            val updateAdminNumber = if (adminPhoneNumber.startsWith("+91")) {
                adminPhoneNumber
            } else {
                "+91$adminPhoneNumber"
            }

            val monthDocRef = database.collection("Members")
                .document(updateAdminNumber)
                .collection("Employee")
                .document(employeeNumber)
                .collection("Expense")
                .document(year)
                .collection(month)
            Log.d("ExpensesData", "$updateAdminNumber, $employeeNumber, $year, $month, ${monthDocRef.path}")

            val dateDocuments = monthDocRef.get().await()
            val allExpenses = mutableListOf<Expense>()

            Log.d("ExpensesData", dateDocuments.size().toString())
            for (dateDoc in dateDocuments.documents) {
                Log.d("ExpensesData", dateDoc.data.toString())
                val entries = dateDoc.reference.collection("Entries").get().await()
                Log.d("ExpensesData", "${entries.documents}, ${entries.isEmpty}, ${entries.metadata}")
                val expenses = entries.documents.mapNotNull { entryDoc ->
                    val data = entryDoc.data
                    if (data != null) {
                        Expense(
                            employeeNumber = data["employeeNumber"] as? String ?: "",
                            items = (data["items"] as? List<Map<String, Any>>)?.map {
                                ExpenseItem(
                                    name = it["name"] as? String ?: "",
                                    value = it["value"] as? String ?: ""
                                )
                            } ?: emptyList(),
                            moneyRaise = data["moneyRaise"] as? String ?: "",
                            remaining = data["remaining"] as? String ?: "",
                            selectedDate = data["selectedDate"] as? String ?: ""
                        )
                    }else null
                }
                allExpenses.addAll(expenses)
            }
            Log.d("ExpensesData", "All Expenses: $allExpenses")
            onSuccess(allExpenses)

        } catch (e: Exception) {
            Log.d("ExpensesData", "Error in getExpensesForMonth: ${e.message}")
            onFailure()
        }
    }

    suspend fun getEmployeeExpense(
        adminPhoneNumber: String,
        from: String,
        to: String,
        selectedMonth: String,
        onSuccess: (List<Expense>) -> Unit,
        onFailure: () -> Unit
    ) {
        try {
            val updateAdminNumber = if (adminPhoneNumber.startsWith("+91")) {
                adminPhoneNumber
            } else {
                "+91$adminPhoneNumber"
            }
            val attendanceList = mutableListOf<Expense>()
            Log.d("Attendance", "Fetching attendance for: $employeeNumber, From: $from, To: $to, Month: $selectedMonth")

            if (selectedMonth.isEmpty()) {
                val fromDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(from)
                val toDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(to)
                val calendar = java.util.Calendar.getInstance()
                    calendar.time = fromDate

                    while (calendar.time <= toDate) {
                        val targetYear = calendar.get(java.util.Calendar.YEAR).toString()
                        val targetMonth = SimpleDateFormat("MMM", Locale.getDefault()).format(calendar.time)
                        val targetDate = SimpleDateFormat("d-M-yyyy", Locale.getDefault()).format(calendar.time)

                        val entriesSnapshot = database.collection("Members")
                            .document(updateAdminNumber)
                            .collection("Employee")
                            .document(employeeNumber.toString())
                            .collection("Expense")
                            .document(targetYear)
                            .collection(targetMonth)
                            .document(targetDate)
                            .collection("Entries")
                            .get()
                            .await()

                        for (entryDoc in entriesSnapshot.documents) {
                            val data = entryDoc.data
                            if (data != null) {
                                val expense = Expense(
                                    employeeNumber = data["employeeNumber"] as? String ?: "",
                                    items = (data["items"] as? List<Map<String, Any>>)?.map {
                                        ExpenseItem(
                                            name = it["name"] as? String ?: "",
                                            value = it["value"] as? String ?: ""
                                        )
                                    } ?: emptyList(),
                                    moneyRaise = data["moneyRaise"] as? String ?: "",
                                    remaining = data["remaining"] as? String ?: "",
                                    selectedDate = data["selectedDate"] as? String ?: "",
                                    status = data["status"] as? Boolean == true
                                )
                                attendanceList.add(expense)
                            }
                        }

                        calendar.add(java.util.Calendar.DATE, 1)
                    }
            } else {
                val parts = selectedMonth.split(" ")
                val targetMonth = parts[0]
                val targetYear = parts[1]

                    val yearDocRef = database.collection("Members")
                        .document(updateAdminNumber)
                        .collection("Employee")
                        .document(employeeNumber.toString())
                        .collection("Expense")
                        .document(targetYear)

                    Log.d("Attendance", "Year Doc Ref: ${yearDocRef.path}")

                    val monthDocSnapshot = yearDocRef.collection(targetMonth)
                        .get()
                        .await()

                    Log.d("Attendance", "Month Doc Snapshot: ${monthDocSnapshot.documents}")
                    //processDateDocuments(monthDocSnapshot, attendanceList)
                    for (dateDoc in monthDocSnapshot.documents) {
                        try {
                            val entries = dateDoc.reference.collection("Entries").get().await()
                            for (entryDoc in entries.documents) {
                                val data = entryDoc.data
                                if (data != null) {
                                    val expense = Expense(
                                        employeeNumber = data["employeeNumber"] as? String ?: "",
                                        items = (data["items"] as? List<Map<String, Any>>)?.map {
                                            ExpenseItem(
                                                name = it["name"] as? String ?: "",
                                                value = it["value"] as? String ?: ""
                                            )
                                        } ?: emptyList(),
                                        moneyRaise = data["moneyRaise"] as? String ?: "",
                                        remaining = data["remaining"] as? String ?: "",
                                        selectedDate = data["selectedDate"] as? String ?: "",
                                        status = data["status"] as? Boolean == true
                                    )
                                    attendanceList.add(expense)
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("Attendance", "Error processing date document: ${e.message}", e)
                        }
                    }
                }

            Log.d("Attendance", "Final list: $attendanceList")
            onSuccess(attendanceList)

        } catch (e: Exception) {
            Log.e("Attendance", "Error fetching expenses: ${e.message}", e)
            onFailure()
        }
    }

    // attendance:
    suspend fun fetchAttendance(
        adminPhoneNumber: String,
        from: String,
        to: String,
        selectedMonth: String,
        onSuccess: (List<PunchInPunchOut>) -> Unit,
        onFailure: () -> Unit
    ){

        val updateAdminNumber = if (adminPhoneNumber.startsWith("+91")) {
            adminPhoneNumber
        } else {
            "+91$adminPhoneNumber"
        }
        try {

            val attendanceList = mutableListOf<PunchInPunchOut>()
            Log.d("Attendance", "Fetching attendance for: $employeeNumber, From: $from, To: $to, Month: $selectedMonth")
            if(selectedMonth.isEmpty()){
                val fromDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(from)
                val toDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(to)
                val calendar = java.util.Calendar.getInstance()
                    calendar.time = fromDate

                    while (calendar.time <= toDate) {
                        val targetYear = calendar.get(java.util.Calendar.YEAR).toString()
                        val targetMonth =
                            SimpleDateFormat("MMM", Locale.getDefault()).format(calendar.time)
                        val targetDate =
                            SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(calendar.time)

                        val entriesSnapshot = database.collection("Members")
                            .document(updateAdminNumber)
                            .collection("Employee")
                            .document(employeeNumber)
                            .collection("Attendance")
                            .document(targetYear)
                            .collection(targetMonth)
                            .document(targetDate)
                            .collection("Entries")
                            .orderBy(FieldPath.documentId(), Query.Direction.DESCENDING)
                            .limit(1)
                            .get()
                            .await()
                        Log.d("Attendance", "Entries Snapshot: ${entriesSnapshot.documents}")
                        if (!entriesSnapshot.isEmpty) {
                            val latestEntry = entriesSnapshot.documents.firstOrNull()?.let { document ->
                                parsePunchInPunchOut(document)
                            }
                            latestEntry?.let { attendanceList.add(it) }
                        }
                        calendar.add(java.util.Calendar.DATE, 1)
                    }
            }else{
                val parts = selectedMonth.split(" ")
                val targetMonth = parts[0]
                val targetYear = parts[1]

                    val yearDocRef = database.collection("Members")
                        .document(updateAdminNumber)
                        .collection("Employee")
                        .document(employeeNumber)
                        .collection("Attendance")
                        .document(targetYear)
                    Log.d("Attendance", "Year Doc Ref: ${yearDocRef.path}")
                    val monthDocSnapshot = yearDocRef.collection(targetMonth)
                        .get()
                        .await()
                    Log.d("Attendance", "Month Doc Snapshot: ${monthDocSnapshot.documents}")

                    for (dateDoc in monthDocSnapshot.documents) {
                        val entriesSnapshot = dateDoc.reference.collection("Entries")
                            .orderBy(FieldPath.documentId(), Query.Direction.DESCENDING)
                            .limit(1)
                            .get()
                            .await()

                        Log.d("Attendance", "Entries Snapshot: ${entriesSnapshot.documents}, ${entriesSnapshot.isEmpty}")

                        if (!entriesSnapshot.isEmpty) {
                            val latestEntry = entriesSnapshot.documents.firstOrNull()?.let { document ->
                                parsePunchInPunchOut(document)
                            }
                            Log.d("Attendance", "Latest Entry: $latestEntry")
                            latestEntry?.let { attendanceList.add(it) }
                        }
                    }
            }
            Log.d("Attendance", "final list: $attendanceList")
            onSuccess(attendanceList)

        }catch (e: Exception) {
            onFailure()
        }
    }
    private fun parsePunchInPunchOut(document: DocumentSnapshot): PunchInPunchOut {
        return PunchInPunchOut(
            currentTime = document.getString("currentTime")?:"",
            absent = document.getString("absent") ?: "Present",
            date = document.getString("date")?:"",
            punchTime = document.getString("punchTime")?:"",
            punchOutTime = document.getString("punchOutTime")?:"",
            locationPunchTime = document.get("locationPunchTime")?.let { locationData ->
                if (locationData is Map<*, *>) {
                    GeofenceItems(
                        title = locationData["title"] as? String ?: "",
                        latitude = locationData["latitude"] as? String?: "",
                        longitude = locationData["longitude"] as? String?: "",
                        radius = locationData["radius"] as? String?: "",
                        adminNo = locationData["adminNo"] as? String?: "",
                        firmName = locationData["firmName"] as? String?: ""
                    )
                } else {
                    GeofenceItems()
                }
            } ?: GeofenceItems(),
            name = document.getString("name")?:"",
            phoneNumberString = document.getString("phoneNumberString")?:"",
            totalMinutes = document.getLong("totalMinutes")?.toInt() ?: 0
        )
    }


}