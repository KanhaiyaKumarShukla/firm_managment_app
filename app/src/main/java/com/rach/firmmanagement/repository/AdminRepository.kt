package com.rach.firmmanagement.repository

import android.annotation.SuppressLint
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.gson.Gson
import com.google.i18n.phonenumbers.Phonenumber
import com.rach.firmmanagement.dataClassImp.AddStaffDataClass
import com.rach.firmmanagement.dataClassImp.AddTaskDataClass
import com.rach.firmmanagement.dataClassImp.AddWorkingHourDataClass
import com.rach.firmmanagement.dataClassImp.Expense
import com.rach.firmmanagement.dataClassImp.ExpenseItem
import com.rach.firmmanagement.dataClassImp.GeofenceItems
import com.rach.firmmanagement.dataClassImp.HolidayAndHoursDataClass
import com.rach.firmmanagement.dataClassImp.MessageDataClass
import com.rach.firmmanagement.dataClassImp.PunchInPunchOut
import com.rach.firmmanagement.dataClassImp.Remark
import com.rach.firmmanagement.dataClassImp.ViewAllEmployeeDataClass
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.YearMonth
import java.util.Calendar
import java.util.Locale

class AdminRepository {

    val database = FirebaseFirestore.getInstance()
    val currentUserPhoneNumber = FirebaseAuth.getInstance().currentUser?.phoneNumber.toString()

    suspend fun saveAdminPermissions(firmName: String, phoneNumber: String, permissions: List<String>) {
        val data = mapOf("permissions" to permissions)
        database.collection("Firms").document(firmName)
            .collection("Admin").document(phoneNumber)
            .collection("Permissions").document(System.currentTimeMillis().toString())
            .set(data)
            .await()
    }

    suspend fun getAdminPermissions(firmName: String, phoneNumber: String): List<String>? {
        return try {
            Log.d("permission", "Fetching latest permissions for $phoneNumber, $firmName")
            val snapshot = database.collection("Firms").document(firmName)
                .collection("Admin").document(phoneNumber)
                .collection("Permissions")
                .orderBy(FieldPath.documentId(), Query.Direction.DESCENDING) // Order by document ID in descending order
                .limit(1) // Get only the most recent document
                .get()
                .await()

            Log.d("permission", snapshot.documents.toString())

            if (!snapshot.isEmpty) {
                Log.d("permission", "document: ${snapshot.documents.first()}, ${snapshot.documents.first().get("permissions")}")
                (snapshot.documents.first().get("permissions") as? List<*>)?.map { it.toString() }
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("permission", "Error fetching latest permissions: ${e.message}", e)
            null
        }
    }


    suspend fun addStaff(
        addStaffDataClass: AddStaffDataClass,
        employeePhoneNumber: String,
        adminPhoneNumber: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {

        try {

            /*
            if(addStaffDataClass.role=="Employee"){
                val data = database.collection("Members").document(addStaffDataClass.adminNumber.toString())
                data.collection("Employee")
                    .document(employeePhoneNumber)
                    .set(addStaffDataClass)
                    .await()
            }

             */
            val firmRef=database.collection("Firms")
                .document(addStaffDataClass.firmName.toString())

            // Set placeholder to ensure document exists
            firmRef.set(mapOf("placeholder" to true)).await()

            // Add the employee to the respective role collection
            firmRef.collection(addStaffDataClass.role.toString())
                .document(addStaffDataClass.phoneNumber.toString())
                .set(addStaffDataClass)
                .await()

            database.collection("Employee")
                .document(addStaffDataClass.phoneNumber.toString())
                .set(addStaffDataClass)
                .await()

            val genderCollection = database.collection("Gender")
            genderCollection.get()
                .addOnSuccessListener { documents ->
                    val document = documents.documents.firstOrNull() // Get the first document
                    if (document != null) {
                        val genderDocRef = genderCollection.document(document.id)
                        genderDocRef.update(mapOf(addStaffDataClass.phoneNumber to addStaffDataClass.role.toString()))
                            .addOnSuccessListener { onSuccess() }
                            .addOnFailureListener { onFailure() }
                    }else {
                        onFailure() // No document found in the "Gender" collection
                    }
                }
                .addOnFailureListener { onFailure() }


        } catch (_: Exception) {

            onFailure()

        }

    }

    suspend fun addHoliday(
        addHolidayDataClass: HolidayAndHoursDataClass,
        adminPhoneNumber: String,
        date: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {

        val updatedAdminPhoneNumber = if (adminPhoneNumber.startsWith("+91")) {
            adminPhoneNumber
        } else {
            "+91$adminPhoneNumber"
        }

        try {
            val formattedDate = date.replace("/", "-")

            val data = database.collection("Members")
                .document(updatedAdminPhoneNumber)

            data.collection("Holiday")
                .document(formattedDate)
                .set(addHolidayDataClass)
                .await()

            onSuccess()


        } catch (_: Exception) {

            onFailure()

        }


    }
    /*
    suspend fun addTask(
        addTaskDataClass: AddTaskDataClass,
        adminPhoneNumber: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {

        try {

            val data = database.collection("Members")
                .document(adminPhoneNumber)


            data.collection("Tasks")
                .add(addTaskDataClass)
                .await()

            onSuccess()

        } catch (_: Exception) {

            onFailure()

        }

    }
    */
    suspend fun addTask(
        addTaskDataClass: AddTaskDataClass,
        adminPhoneNumber: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        val updatedAdminPhoneNumber = if (adminPhoneNumber.startsWith("+91")) {
            adminPhoneNumber
        } else {
            "+91$adminPhoneNumber"
        }
        try {
            // Reference to the document with a generated ID
            val taskRef = database.collection("Members")
                .document(updatedAdminPhoneNumber)
                .collection("Tasks")
                .document() // Generate a unique document ID

            val taskId = taskRef.id // Retrieve the generated task ID

            // Create a copy of the data class with the task ID
            val updatedTaskData = addTaskDataClass.copy(id = taskId, remarks = emptyList(), isCommon = true)

            // Upload the data with the generated task ID
            taskRef.set(updatedTaskData).await()

            onSuccess()

        } catch (e: Exception) {
            onFailure()
        }
    }

    suspend fun assignTask(
        selectedEmployees: Set<AddStaffDataClass>,
        addTaskDataClass: AddTaskDataClass,
        adminPhoneNumber:String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ){
        val updatedAdminPhoneNumber = if (adminPhoneNumber.startsWith("+91")) {
            adminPhoneNumber
        } else {
            "+91$adminPhoneNumber"
        }

        try {

            val batch=database.batch()
            selectedEmployees.forEach { employee ->
                val employeeDocRef = database
                    .collection("Members")
                    .document(updatedAdminPhoneNumber)
                    .collection("Employee")
                    .document(employee.phoneNumber.toString())
                    .collection("Task")
                    .document(System.currentTimeMillis().toString())

                // Ensure parent documents exist
                val adminDocRef = database
                    .collection("Members")
                    .document(updatedAdminPhoneNumber)

                Log.d("Work Hours", "Adding work hours for: $employee")
                batch.set(adminDocRef, mapOf("created" to true), SetOptions.merge())
                batch.set(employeeDocRef, addTaskDataClass.copy(id = employeeDocRef.id, employeePhoneNumber = employee.phoneNumber.toString()))
            }

            batch.commit().addOnSuccessListener {
                onSuccess()
            }.addOnFailureListener {
                onFailure()
            }
        }catch (e: Exception) {
            onFailure()
        }
    }

    suspend fun addWorkingHour(
        addWorkingHourDataClass: AddWorkingHourDataClass,
        adminPhoneNumber:String,
        date: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        val updatedAdminPhoneNumber = if (adminPhoneNumber.startsWith("+91")) {
            adminPhoneNumber
        } else {
            "+91$adminPhoneNumber"
        }

        try {

            val formattedDate = date.replace("/", "-")

            val data = database.collection("Members")
                .document(updatedAdminPhoneNumber)

            data.collection("WorkingHour")
                .document(formattedDate)
                .set(addWorkingHourDataClass)
                .await()

            onSuccess()

        } catch (_: Exception) {

            onFailure()

        }

    }

    fun addWorkHoursForEmployees(
        selectedEmployees: Set<AddStaffDataClass>,
        adminPhoneNumber:String,
        workHourData: AddWorkingHourDataClass,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {

        val updatedAdminPhoneNumber = if (adminPhoneNumber.startsWith("+91")) {
            adminPhoneNumber
        } else {
            "+91$adminPhoneNumber"
        }
        val db = FirebaseFirestore.getInstance()
        val batch = db.batch()

        try {
            selectedEmployees.forEach { employee ->
                val employeeDocRef = db
                    .collection("Members")
                    .document(updatedAdminPhoneNumber)
                    .collection("Employee")
                    .document(employee.phoneNumber.toString())
                    .collection("WorkHour")
                    .document(System.currentTimeMillis().toString())

                // Ensure parent documents exist
                val yearDocRef = db
                    .collection("Members")
                    .document(updatedAdminPhoneNumber)

                Log.d("Work Hours", "Adding work hours for: $employee")
                batch.set(yearDocRef, mapOf("created" to true), SetOptions.merge())
                batch.set(employeeDocRef, workHourData)
            }

            batch.commit().addOnSuccessListener {
                onSuccess()
            }.addOnFailureListener {
                onFailure()
            }
        } catch (e: Exception) {
            onFailure()
        }
    }


    suspend fun loadTasks(
        employees: List<AddStaffDataClass>
    ): List<AddTaskDataClass> {

        val taskList = mutableListOf<AddTaskDataClass>()
        for (employee in employees) {
            val updateAdminNumber = if (employee.adminNumber?.startsWith("+91")==true) {
                employee.adminNumber
            } else {
                "+91${employee.adminNumber}"
            }
            val employeeTasksData = database.collection("Members")
                .document(updateAdminNumber)
                .collection("Employee")
                .document(employee.phoneNumber.toString())
                .collection("Task")
                .get()
                .await()
            Log.d("Task", employee.toString())

            if (!employeeTasksData.isEmpty) {
                Log.d("Task", "not empty")
                val employeeTasks = employeeTasksData.documents.mapNotNull { document ->
                    document.toObject(AddTaskDataClass::class.java)?.copy(id = document.id)
                }
                taskList.addAll(employeeTasks)
                Log.d("Task", "$taskList")
            }
        }
        return taskList
    }
    suspend fun loadOneEmployeeTask(
        adminPhoneNumber: String,
        employee: AddStaffDataClass
    ): List<AddTaskDataClass> {

        val updateAdminNumber = if (adminPhoneNumber.startsWith("+91")) {
            adminPhoneNumber
        } else {
            "+91$adminPhoneNumber"
        }
        val taskList = mutableListOf<AddTaskDataClass>()
        val employeeTasksData = database.collection("Members")
            .document(updateAdminNumber)
            .collection("Employee")
            .document(employee.phoneNumber.toString())
            .collection("Task")
            .get()
            .await()
        if (!employeeTasksData.isEmpty) {
            Log.d("Task", "not empty")
            val employeeTasks = employeeTasksData.documents.mapNotNull { document ->
                document.toObject(AddTaskDataClass::class.java)?.copy(id = document.id)
            }
            taskList.addAll(employeeTasks)
            Log.d("Task", "$taskList")
        }
        return taskList
    }


    suspend fun deleteTask(adminPhoneNumber: String, task: AddTaskDataClass) {
        val updateAdminNumber = if (adminPhoneNumber.startsWith("+91")) {
            adminPhoneNumber
        } else {
            "+91$adminPhoneNumber"
        }

        val taskDocRef = database.collection("Members")
            .document(updateAdminNumber)
            .collection("Employee")
            .document(task.employeePhoneNumber)
            .collection("Task")
            .document(task.id)

        // Delete the task document
        taskDocRef.delete().await()
    }

    suspend fun addRemark(
        adminPhoneNumber: String,
        employeePhone: String="", // Required for non-common tasks
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
            val taskRef = if (isCommon) {
                // Path for common tasks
                database.collection("Members")
                    .document(updateAdminNumber)
                    .collection("Tasks")
                    .document(taskId)
            } else {
                // Path for employee-specific tasks
                database.collection("Members")
                    .document(updateAdminNumber)
                    .collection("Employee")
                    .document(employeePhone)
                    .collection("Task")
                    .document(taskId)
            }

            Log.d("Task","$taskId, $isCommon, $employeePhone, $taskRef, $adminPhoneNumber")

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

    suspend fun fetchRemarks(
        adminPhoneNumber: String,
        isCommon: Boolean,
        taskId: String,
        employeePhone:String,
    ): List<Remark> {
        return try {
            val updateAdminNumber = if (adminPhoneNumber.startsWith("+91")) {
                adminPhoneNumber
            } else {
                "+91$adminPhoneNumber"
            }
            val taskRef = if (isCommon) {
                // Path for common tasks
                database.collection("Members")
                    .document(updateAdminNumber)
                    .collection("Tasks")
                    .document(taskId)
            } else {
                // Path for employee-specific tasks
                database.collection("Members")
                    .document(updateAdminNumber)
                    .collection("Employee")
                    .document(employeePhone)
                    .collection("Task")
                    .document(taskId)
            }
            // Fetch the current task data from Firestore
            val snapshot = taskRef.get().await()
            val taskData = snapshot.toObject(AddTaskDataClass::class.java)

            // Extract remarks from the fetched task data
            val remarks = taskData?.remarks ?: emptyList()
            Log.d("Task", "fetched successful")
            remarks

        } catch (e: Exception) {
            Log.e("Task", "Failed to fetch remarks: ${e.message}")
            emptyList()
        }
    }

    suspend fun getExpensesForMonth(
        adminPhoneNumber: String,
        employeeNumber:String,
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

    // Attendance :

    suspend fun fetchAttendance(
        adminPhoneNumber: String,
        selectedEmployees: List<AddStaffDataClass>,
        from: String,
        to: String,
        selectedMonth: String,
        onSuccess: (List<PunchInPunchOut>) -> Unit,
        onFailure: () -> Unit
    ){
        try {
            val attendanceList = mutableListOf<PunchInPunchOut>()
            Log.d("Attendance", "Fetching attendance for: $selectedEmployees, From: $from, To: $to, Month: $selectedMonth")
            if(selectedMonth.isEmpty()){
                val fromDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(from)
                val toDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(to)
                val calendar = Calendar.getInstance()
                for (employee in selectedEmployees) {
                    calendar.time = fromDate
                    val updateAdminNumber = if (employee.adminNumber?.startsWith("+91")==true) {
                        employee.adminNumber
                    } else {
                        "+91${employee.adminNumber}"
                    }

                    while (calendar.time <= toDate) {
                        val targetYear = calendar.get(Calendar.YEAR).toString()
                        val targetMonth =
                            SimpleDateFormat("MMM", Locale.getDefault()).format(calendar.time)
                        val targetDate =
                            SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(calendar.time)

                        val entriesSnapshot = database.collection("Members")
                            .document(updateAdminNumber)
                            .collection("Employee")
                            .document(employee.phoneNumber.toString())
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
                        calendar.add(Calendar.DATE, 1)
                    }
                }
            }else{
                val parts = selectedMonth.split(" ")
                val targetMonth = parts[0]
                val targetYear = parts[1]

                for (employee in selectedEmployees) {

                    val updateAdminNumber = if (employee.adminNumber?.startsWith("+91")==true) {
                        employee.adminNumber
                    } else {
                        "+91${employee.adminNumber}"
                    }

                    val yearDocRef = database.collection("Members")
                        .document(updateAdminNumber)
                        .collection("Employee")
                        .document(employee.phoneNumber.toString())
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

                        if (!entriesSnapshot.isEmpty) {
                            val latestEntry = entriesSnapshot.documents.firstOrNull()?.let { document ->
                                parsePunchInPunchOut(document)
                            }
                            latestEntry?.let { attendanceList.add(it) }
                        }
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

    // employee profile
    suspend fun getStaff(
        employeePhoneNumber: String,
        onSuccess: (AddStaffDataClass) -> Unit,
        onFailure: () -> Unit
    ) {
        try {
            val documentSnapshot = database.collection("Employee")
                .document(employeePhoneNumber)
                .get()
                .await()

            val staff = documentSnapshot.toObject(AddStaffDataClass::class.java)
            if (staff != null) {
                Log.d("Staff", "Staff fetched successfully: $staff")
                onSuccess(staff)
            } else {
                Log.d("Staff", "Staff not found")
                onFailure()
            }
        } catch (_: Exception) {
            Log.d("Staff", "Staff fetch failed")
            onFailure()
        }
    }

    suspend fun updateStaff(
        employeePhoneNumber: String,
        updatedStaff: AddStaffDataClass,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        try {
            database.collection("Employee")
                .document(employeePhoneNumber)
                .set(updatedStaff)
                .await()
            Log.d("Staff", "Staff updated successfully")
            onSuccess()
        } catch (_: Exception) {
            Log.d("Staff", "Staff update failed")
            onFailure()
        }
    }

    /*
    @SuppressLint("NewApi")
    suspend fun getEmployeeExpense(
        employeePhoneNumber: String?,
        adminPhoneNumber:String,
        from: String,
        to: String,
        selectedMonth: String,
        onSuccess: (List<Expense>) -> Unit,
        onFailure: () -> Unit
    ) {
        if (employeePhoneNumber.isNullOrEmpty()) {
            onFailure()
            return
        }
        try {
            val allExpenses = mutableListOf<Expense>()

            val updateAdminNumber = if (adminPhoneNumber.startsWith("+91")) {
                adminPhoneNumber
            } else {
                "+91$adminPhoneNumber"
            }

            if (selectedMonth.isNotEmpty()) {
                val (month, year) = selectedMonth.split(" ")

                val monthDocRef = database.collection("Members")
                    .document(updateAdminNumber)
                    .collection("Employee")
                    .document(employeePhoneNumber)
                    .collection("Expense")
                    .document(year)
                    .collection(month)

                val dateDocuments = monthDocRef.get().await()
                processDateDocuments(dateDocuments, allExpenses)
            } else {
                val fromDate = LocalDate.parse(from)
                val toDate = LocalDate.parse(to)

                val startYearMonth = YearMonth.from(fromDate)
                val endYearMonth = YearMonth.from(toDate)

                var currentYearMonth = startYearMonth
                while (!currentYearMonth.isAfter(endYearMonth)) {
                    val year = currentYearMonth.year.toString()
                    val month = currentYearMonth.monthValue.toString().padStart(2, '0')

                    val monthDocRef = database.collection("Members")
                        .document(updateAdminNumber)
                        .collection("Employee")
                        .document(employeePhoneNumber)
                        .collection("Expense")
                        .document(year)
                        .collection(month)

                    val dateDocuments = monthDocRef.get().await()
                    processDateDocuments(dateDocuments, allExpenses)

                    currentYearMonth = currentYearMonth.plusMonths(1)
                }
            }

            onSuccess(allExpenses)
        } catch (e: Exception) {
            Log.d("ExpensesData", "Error in getEmployeeExpense: ${e.message}")
            onFailure()
        }
    }
    */


    suspend fun getEmployeeExpense(
        selectedEmployees: List<AddStaffDataClass>,
        from: String,
        to: String,
        selectedMonth: String,
        onSuccess: (List<Expense>) -> Unit,
        onFailure: () -> Unit
    ) {
        try {
            val attendanceList = mutableListOf<Expense>()
            Log.d("Attendance", "Fetching attendance for: $selectedEmployees, From: $from, To: $to, Month: $selectedMonth")

            if (selectedMonth.isEmpty()) {
                val fromDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(from)
                val toDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(to)
                val calendar = Calendar.getInstance()

                for (employee in selectedEmployees) {
                    calendar.time = fromDate
                    val updateAdminNumber = if (employee.adminNumber?.startsWith("+91") == true) {
                        employee.adminNumber
                    } else {
                        "+91${employee.adminNumber}"
                    }

                    while (calendar.time <= toDate) {
                        val targetYear = calendar.get(Calendar.YEAR).toString()
                        val targetMonth = SimpleDateFormat("MMM", Locale.getDefault()).format(calendar.time)
                        val targetDate = SimpleDateFormat("d-M-yyyy", Locale.getDefault()).format(calendar.time)

                        val entriesSnapshot = database.collection("Members")
                            .document(updateAdminNumber)
                            .collection("Employee")
                            .document(employee.phoneNumber.toString())
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

                        calendar.add(Calendar.DATE, 1)
                    }
                }
            } else {
                val parts = selectedMonth.split(" ")
                val targetMonth = parts[0]
                val targetYear = parts[1]

                for (employee in selectedEmployees) {
                    val updateAdminNumber = if (employee.adminNumber?.startsWith("+91") == true) {
                        employee.adminNumber
                    } else {
                        "+91${employee.adminNumber}"
                    }

                    val yearDocRef = database.collection("Members")
                        .document(updateAdminNumber)
                        .collection("Employee")
                        .document(employee.phoneNumber.toString())
                        .collection("Expense")
                        .document(targetYear)

                    Log.d("Attendance", "Year Doc Ref: ${yearDocRef.path}")

                    val monthDocSnapshot = yearDocRef.collection(targetMonth)
                        .get()
                        .await()

                    Log.d("Attendance", "Month Doc Snapshot: ${monthDocSnapshot.documents}")
                    processDateDocuments(monthDocSnapshot, attendanceList)
                }
            }

            Log.d("Attendance", "Final list: $attendanceList")
            onSuccess(attendanceList)

        } catch (e: Exception) {
            Log.e("Attendance", "Error fetching expenses: ${e.message}", e)
            onFailure()
        }
    }

    private suspend fun processDateDocuments(
        dateDocuments: QuerySnapshot,
        allExpenses: MutableList<Expense>
    ) {
        for (dateDoc in dateDocuments.documents) {
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
                            selectedDate = data["selectedDate"] as? String ?: ""
                        )
                        allExpenses.add(expense)
                    }
                }
            } catch (e: Exception) {
                Log.e("Attendance", "Error processing date document: ${e.message}", e)
            }
        }
    }


    // chat

    suspend fun sendMessage(
        adminPhoneNumber: String,
        employeeNumber: String,
        message: MessageDataClass,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        val updateAdminNumber = if (adminPhoneNumber.startsWith("+91")) {
            adminPhoneNumber
        } else {
            "+91$adminPhoneNumber"
        }
        try {
            database.collection("Members")
                .document(updateAdminNumber)
                .collection("Employee")
                .document(employeeNumber)
                .collection("Messages")
                .add(message)
                .await()
            onSuccess()
        } catch (e: Exception) {
            e.printStackTrace()
            onFailure()
        }
    }

    suspend fun fetchMessages(
        adminPhoneNumber: String,
        employeeNumber: String,
        onSuccess: (List<MessageDataClass>) -> Unit,
        onFailure: () -> Unit
    ): ListenerRegistration {
        val updateAdminNumber = if (adminPhoneNumber.startsWith("+91")) {
            adminPhoneNumber
        } else {
            "+91$adminPhoneNumber"
        }
        return database.collection("Members")
                .document(updateAdminNumber)
                .collection("Employee")
                .document(employeeNumber)
                .collection("Messages")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        exception.printStackTrace()
                        onFailure()
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        val messages = snapshot.toObjects(MessageDataClass::class.java)
                        onSuccess(messages)
                    } else {
                        onFailure()
                    }
                }
    }




}