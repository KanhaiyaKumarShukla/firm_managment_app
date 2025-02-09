package com.rach.firmmanagement.repository

import android.util.Log
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.rach.firmmanagement.dataClassImp.AddStaffDataClass
import com.rach.firmmanagement.dataClassImp.AdvanceMoneyData
import com.rach.firmmanagement.dataClassImp.EmployeeLeaveData
import com.rach.firmmanagement.dataClassImp.Expense
import com.rach.firmmanagement.dataClassImp.ExpenseItem
import com.rach.firmmanagement.dataClassImp.OutForWork
import com.rach.firmmanagement.login.DataClassRegister
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class RegularizationRepository() {

    private val firestore= FirebaseFirestore.getInstance()

    suspend fun getPendingRequests(collection: String): List<DocumentSnapshot> {
        return try {
            val snapshot = firestore.collection(collection).get().await()
            snapshot.documents
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun approveRequest(collection: String, documentId: String) {
        firestore.collection(collection).document(documentId).update("status", true).await()
    }

    suspend fun rejectRequest(collection: String, documentId: String) {
        firestore.collection(collection).document(documentId).delete().await()
    }
    suspend fun fetchPendingEmployee(firmName: String): List<DataClassRegister> {
        val roleCollection = "pendingEmployees"
        val firmRef = firestore.collection("Firms")
            .document(firmName)
            .collection(roleCollection)

        Log.d("PendingEmployees", "FirmRef: ${firmRef.path}")

        return try {
            val snapshot = firmRef.get().await()
            Log.d("PendingEmployees", "Snapshot: ${snapshot.documents}")
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(DataClassRegister::class.java)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun approvePendingEmployee(firmName: String, data: AddStaffDataClass) {
        val firmRef = firestore.collection("Firms")
            .document(firmName)

        // Set placeholder to ensure document exists
        firmRef.set(mapOf("placeholder" to true)).await()

        // Add the employee to the respective role collection
        firmRef.collection(data.role.toString())
            .document(data.phoneNumber.toString())
            .set(data)
            .await()

        firestore.collection("Employee")
            .document(data.phoneNumber.toString())
            .set(data)
            .await()

        // Delete from the pending employees collection
        val roleCollection =  "pendingEmployees"
        firmRef.collection(roleCollection)
            .document(data.phoneNumber.toString())
            .delete()
            .await()
    }

    // Function to reject a pending employee
    suspend fun rejectPendingEmployee(firmName: String, data: DataClassRegister) {
        val roleCollection =  "pendingEmployees"
        val firmRef = firestore.collection("Firms")
            .document(firmName)

        // Delete the employee from the pending collection
        firmRef.collection(roleCollection)
            .document(data.mobileNumber.toString())
            .delete()
            .await()
    }
    fun listenForEmployeeUpdates(
        firmName: String,
        category: String,
        onUpdate: (String, List<DataClassRegister>) -> Unit
    ): ListenerRegistration {
        return firestore.collection("Firms")
            .document(firmName)
            .collection(category)
            .addSnapshotListener { snapshot, error ->
                Log.d("PendingEmployees", "Snapshot: ${snapshot?.documents}, Error: $error")
                if (error != null) {
                    return@addSnapshotListener
                }

                snapshot?.let {
                    val employees = it.documents.mapNotNull { doc -> doc.toObject(DataClassRegister::class.java) }
                    if (it.documentChanges.isNotEmpty()) {
                        val message = when (it.documentChanges.first().type) {
                            DocumentChange.Type.ADDED -> "Employee loaded!"
                            DocumentChange.Type.MODIFIED -> "Employee data updated!"
                            DocumentChange.Type.REMOVED -> "Employee removed!"
                        }
                        onUpdate(message, employees)
                    }
                }
            }
    }

    /*
    suspend fun fetchPendingExpenses(firmName: String): List<Expense> {
        val roleCollection = "pendingExpenses"
        val firmRef = firestore.collection("Firms")
            .document(firmName)
            .collection(roleCollection)

        Log.d("PendingExpenses", "FirmRef: ${firmRef.path}")

        return try {

            val snapshot = firmRef.get().await()
            Log.d("PendingExpenses", "Snapshot: ${snapshot.documents}")
            val expensesList = mutableListOf<Expense>()

            for (entryDoc in snapshot.documents) {
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
                    expensesList.add(expense)
                }
            }
            Log.d("PendingExpenses", "ExpensesList: $expensesList")
            expensesList
        } catch (e: Exception) {
            emptyList()
        }


    }
     */
    suspend fun fetchPendingExpenses(firmName: String): List<Expense> {
        val roleCollection = "pendingExpenses"
        val firmRef = firestore.collection("Firms")
            .document(firmName)
            .collection(roleCollection)

        Log.d("PendingExpenses", "FirmRef: ${firmRef.path}")

        return try {
            val snapshot = firmRef.get().await()
            Log.d("PendingExpenses", "Snapshot: ${snapshot.documents}")

            val expensesList = mutableListOf<Expense>()

            for (entryDoc in snapshot.documents) {
                val data = entryDoc.data
                if (data != null) {
                    try {
                        val itemsList = mutableListOf<ExpenseItem>()
                        val rawItems = data["items"] as? List<Map<String, Any>> ?: emptyList()

                        for (item in rawItems) {
                            val name = item["name"] as? String ?: ""
                            val value = item["value"] as? String ?: ""
                            itemsList.add(ExpenseItem(name, value))
                        }

                        val expense = Expense(
                            id=data["id"] as? String ?: "",
                            employeeNumber = data["employeeNumber"] as? String ?: "",
                            items = itemsList,
                            moneyRaise = data["moneyRaise"] as? String ?: "",
                            remaining = data["remaining"] as? String ?: "",
                            selectedDate = data["selectedDate"] as? String ?: ""
                        )
                        expensesList.add(expense)
                    } catch (e: Exception) {
                        Log.e("PendingExpenses", "Error parsing expense document: ${e.message}", e)
                    }
                }
            }
            Log.d("PendingExpenses", "ExpensesList: $expensesList")
            expensesList
        } catch (e: Exception) {
            Log.e("PendingExpenses", "Error fetching expenses: ${e.message}", e)
            emptyList()
        }
    }


    suspend fun approvePendingExpenses(employeeIdentity: AddStaffDataClass, data: Expense, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val firmName=employeeIdentity.firmName.toString()
        val adminPhoneNumber=employeeIdentity.adminNumber.toString()
        val updateAdminNumber = if (adminPhoneNumber.startsWith("+91")) {
            adminPhoneNumber
        } else {
            "+91$adminPhoneNumber"
        }
        val firmRef = firestore.collection("Firms")
            .document(firmName)

        // Delete from the pending employees collection
        val roleCollection =  "pendingExpenses"
        firmRef.collection(roleCollection)
            .document(data.id.toString())
            .delete()
            .await()
        val calendar = Calendar.getInstance()
        val employeePhoneNumber=data.employeeNumber.toString().removePrefix("+91")
        val expenseId=data.id.toString()

        val dateParts = data.selectedDate.split("-")
        if (dateParts.size != 3) {
            Log.e("ExpenseUpdate", "Invalid selectedDate format: ${data.selectedDate}")
            onFailure(Exception("Invalid selectedDate format"))
            return
        }

        val targetDate = data.selectedDate.toString()
        val targetMonth = SimpleDateFormat("MMM", Locale.getDefault())
            .format(SimpleDateFormat("M", Locale.getDefault()).parse(dateParts[1])!!) // Converts "2" → "Feb"
        val targetYear = dateParts[2]

        val expenseRef = firestore.collection("Members")
            .document(updateAdminNumber)
            .collection("Employee")
            .document(employeePhoneNumber)
            .collection("Expense")
            .document(targetYear)
            .collection(targetMonth)
            .document(targetDate)
            .collection("Entries")
            .document(expenseId) // Directly referencing the document using ID

        try {
            expenseRef.update("status", true).await()
            Log.d("ExpenseUpdate", "Successfully updated status for Expense ID: $expenseId")
            val roleCollection =  "pendingExpenses"
            val firmRef = firestore.collection("Firms")
                .document(firmName)

            // Delete the employee from the pending collection
            firmRef.collection(roleCollection)
                .document(data.id.toString())
                .delete()
                .await()

            onSuccess()
        } catch (e: Exception) {
            onFailure(e)
            Log.e("ExpenseUpdate", "Error updating status for Expense ID: $expenseId - ${e.message}", e)
        }
    }

    // Function to reject a pending employee
    suspend fun rejectPendingExpenses(firmName: String, data: Expense, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val roleCollection =  "pendingExpenses"
        val firmRef = firestore.collection("Firms")
            .document(firmName)

        try {
            // Delete the employee from the pending collection
            firmRef.collection(roleCollection)
                .document(data.id.toString())
                .delete()
                .await()
            onSuccess()
        }catch (e: Exception){
            onFailure(e)
        }


    }
    fun listenForExpensesUpdates(
        firmName: String,
        category: String,
        onUpdate: (String, List<Expense>) -> Unit
    ): ListenerRegistration {
        return firestore.collection("Firms")
            .document(firmName)
            .collection(category)
            .addSnapshotListener { snapshot, error ->
                Log.d("PendingExpenses", "Snapshot: ${snapshot?.documents}, Error: $error")
                if (error != null) {
                    return@addSnapshotListener
                }

                snapshot?.let {
                    val employees = it.documents.mapNotNull { doc -> doc.toObject(Expense::class.java) }
                    if (it.documentChanges.isNotEmpty()) {
                        val message = when (it.documentChanges.first().type) {
                            DocumentChange.Type.ADDED -> "Expenses loaded!"
                            DocumentChange.Type.MODIFIED -> "Expenses data updated!"
                            DocumentChange.Type.REMOVED -> "Expenses removed!"
                        }
                        onUpdate(message, employees)
                    }
                }
            }
    }



    suspend fun fetchPendingLeaves(firmName: String): List<EmployeeLeaveData> {
        val roleCollection = "pendingLeaves"
        val firmRef = firestore.collection("Firms")
            .document(firmName)
            .collection(roleCollection)

        Log.d("PendingLeaves", "FirmRef: ${firmRef.path}")

        return try {
            val snapshot = firmRef.get().await()
            Log.d("PendingLeaves", "Snapshot: ${snapshot.documents}")

            val leavesList = mutableListOf<EmployeeLeaveData>()

            for (entryDoc in snapshot.documents) {
                val data = entryDoc.data
                if (data != null) {
                    try {

                        val leave = EmployeeLeaveData(
                            id=data["id"] as? String ?: "",
                            emlPhoneNumber = data["emlPhoneNumber"] as? String ?: "",
                            startingDate = data["startingDate"] as? String ?: "",
                            endDate = data["endDate"] as? String ?: "",
                            reason = data["reason"] as? String ?: "",
                            status = (data["status"] as? Number)?.toInt() ?: 0,
                            type = data["type"] as? String ?: "",
                            currentDate = data["currentDate"] as? String ?: ""
                        )
                        leavesList.add(leave)
                    } catch (e: Exception) {
                        Log.e("PendingLeaves", "Error parsing Leave document: ${e.message}", e)
                    }
                }
            }
            Log.d("PendingLeaves", "LeavesList: $leavesList")
            leavesList
        } catch (e: Exception) {
            Log.e("PendingLeaves", "Error fetching Leaves: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun approvePendingLeaves(employeeIdentity: AddStaffDataClass, data: EmployeeLeaveData, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        updatePendingLeaves(employeeIdentity, data, onSuccess, onFailure, 1)
    }
    suspend fun updatePendingLeaves(employeeIdentity: AddStaffDataClass, data: EmployeeLeaveData, onSuccess: () -> Unit, onFailure: (Exception) -> Unit, value:Int) {
        val firmName=employeeIdentity.firmName.toString()
        val adminPhoneNumber=employeeIdentity.adminNumber.toString()
        val updateAdminNumber = if (adminPhoneNumber.startsWith("+91")) {
            adminPhoneNumber
        } else {
            "+91$adminPhoneNumber"
        }
        val firmRef = firestore.collection("Firms")
            .document(firmName)

        val employeePhoneNumber=data.emlPhoneNumber.toString().removePrefix("+91")

        // Delete from the pending employees collection
        val roleCollection =  "pendingLeaves"
        firmRef.collection(roleCollection)
            .document(data.id.toString())
            .delete()
            .await()
        val calendar = Calendar.getInstance()
        val leaveId=data.id.toString()

        val targetDate = data.startingDate.toString()
        val dateParts = targetDate.split("-")
        if (dateParts.size != 3) {
            Log.e("LeaveUpdate", "Invalid selectedDate format: ${data.startingDate}")
            onFailure(Exception("Invalid selectedDate format"))
            return
        }

        val targetMonth = SimpleDateFormat("MMM", Locale.getDefault())
            .format(SimpleDateFormat("M", Locale.getDefault()).parse(dateParts[1])!!) // Converts "2" → "Feb"
        val targetYear = dateParts[2]

        val leaveRef = firestore.collection("Members")
            .document(updateAdminNumber)
            .collection("Employee")
            .document(employeePhoneNumber)
            .collection("Leave")
            .document(targetYear)
            .collection(targetMonth)
            .document(targetDate)
            .collection("Entries")
            .document(leaveId) // Directly referencing the document using ID

        try {
            leaveRef.update("status", value).await()
            Log.d("LeaveUpdate", "Successfully updated status for Leave ID: $leaveId")
            val roleCollection =  "pendingLeaves"
            val firmRef = firestore.collection("Firms")
                .document(firmName)

            // Delete the employee from the pending collection
            firmRef.collection(roleCollection)
                .document(data.id.toString())
                .delete()
                .await()

            onSuccess()
        } catch (e: Exception) {
            onFailure(e)
            Log.e("LeaveUpdate", "Error updating status for Leave ID: $leaveId - ${e.message}", e)
        }
    }

    // Function to reject a pending employee
    suspend fun rejectPendingLeaves(employeeIdentity: AddStaffDataClass, data: EmployeeLeaveData, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        updatePendingLeaves(employeeIdentity, data, onSuccess, onFailure, 2)

    }
    fun listenForLeavesUpdates(
        firmName: String,
        category: String,
        onUpdate: (String, List<EmployeeLeaveData>) -> Unit
    ): ListenerRegistration {
        Log.d("PendingLeaves", "FirmName: $firmName, Category: $category")
        return firestore.collection("Firms")
            .document(firmName)
            .collection(category)
            .addSnapshotListener { snapshot, error ->
                Log.d("PendingLeaves", "Snapshot: ${snapshot?.documents}, Error: $error")
                if (error != null) {
                    return@addSnapshotListener
                }

                snapshot?.let {
                    Log.d("PendingLeaves", "Documents: ${it.documents}")
                    val employees = it.documents.mapNotNull { doc -> doc.toObject(EmployeeLeaveData::class.java) }
                    Log.d("PendingLeaves", "Employees: $employees")
                    if (it.documentChanges.isNotEmpty()) {
                        val message = when (it.documentChanges.first().type) {
                            DocumentChange.Type.ADDED -> "Leaves loaded!"
                            DocumentChange.Type.MODIFIED -> "Leaves data updated!"
                            DocumentChange.Type.REMOVED -> "Leaves removed!"
                        }
                        Log.d("PendingLeaves", "Message: $message")
                        onUpdate(message, employees)
                    }
                }
            }
    }

    suspend fun fetchPendingAdvance(firmName: String): List<AdvanceMoneyData> {
        val roleCollection = "pendingAdvances"
        val firmRef = firestore.collection("Firms")
            .document(firmName)
            .collection(roleCollection)

        Log.d("PendingAdvances", "FirmRef: ${firmRef.path}")

        return try {
            val snapshot = firmRef.get().await()
            Log.d("PendingAdvances", "Snapshot: ${snapshot.documents}")

            val advList = mutableListOf<AdvanceMoneyData>()

            for (entryDoc in snapshot.documents) {
                val data = entryDoc.data
                if (data != null) {
                    try {

                        val adv = AdvanceMoneyData(
                            id=data["id"] as? String ?: "",
                            reason = data["reason"] as? String ?: "",
                            amount = data["amount"] as? String ?: "",
                            date = data["date"] as? String ?: "",
                            emplPhoneNumber = data["emplPhoneNumber"] as? String ?: "",
                            status = (data["status"] as? Number)?.toInt() ?: 0,
                            time = data["time"] as? String ?: ""
                        )
                        advList.add(adv)
                    } catch (e: Exception) {
                        Log.e("PendingAdvance", "Error parsing Advance document: ${e.message}", e)
                    }
                }
            }
            Log.d("PendingAdvance", "AdvanceList: $advList")
            advList
        } catch (e: Exception) {
            Log.e("PendingAdvance", "Error fetching Advance: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun approvePendingAdvance(employeeIdentity: AddStaffDataClass, data: AdvanceMoneyData, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        updatePendingAdvance(employeeIdentity, data, onSuccess, onFailure, 1)
    }
    suspend fun updatePendingAdvance(employeeIdentity: AddStaffDataClass, data: AdvanceMoneyData, onSuccess: () -> Unit, onFailure: (Exception) -> Unit, value:Int) {
        val firmName=employeeIdentity.firmName.toString()
        val adminPhoneNumber=employeeIdentity.adminNumber.toString()
        val updateAdminNumber = if (adminPhoneNumber.startsWith("+91")) {
            adminPhoneNumber
        } else {
            "+91$adminPhoneNumber"
        }
        val firmRef = firestore.collection("Firms")
            .document(firmName)

        val employeePhoneNumber=data.emplPhoneNumber.toString().removePrefix("+91")

        // Delete from the pending employees collection
        val roleCollection =  "pendingAdvances"
        firmRef.collection(roleCollection)
            .document(data.id.toString())
            .delete()
            .await()
        val calendar = Calendar.getInstance()
        val advanceId=data.id.toString()

        val targetDate = data.date.toString()
        val dateParts = targetDate.split("-")
        if (dateParts.size != 3) {
            Log.e("AdvanceUpdate", "Invalid selectedDate format: ${data.date}")
            onFailure(Exception("Invalid selectedDate format"))
            return
        }

        val targetMonth = SimpleDateFormat("MMM", Locale.getDefault())
            .format(SimpleDateFormat("M", Locale.getDefault()).parse(dateParts[1])!!) // Converts "2" → "Feb"
        val targetYear = dateParts[2]

        val leaveRef = firestore.collection("Members")
            .document(updateAdminNumber)
            .collection("Employee")
            .document(employeePhoneNumber)
            .collection("Advance")
            .document(targetYear)
            .collection(targetMonth)
            .document(targetDate)
            .collection("Entries")
            .document(advanceId) // Directly referencing the document using ID

        try {
            leaveRef.update("status", value).await()
            Log.d("AdvanceUpdate", "Successfully updated status for Leave ID: $advanceId")
            val roleCollection =  "pendingAdvances"
            val firmRef = firestore.collection("Firms")
                .document(firmName)

            // Delete the employee from the pending collection
            firmRef.collection(roleCollection)
                .document(data.id.toString())
                .delete()
                .await()

            onSuccess()
        } catch (e: Exception) {
            onFailure(e)
            Log.e("AdvanceUpdate", "Error updating status for Advance ID: $advanceId - ${e.message}", e)
        }
    }

    // Function to reject a pending employee
    suspend fun rejectPendingAdvance(employeeIdentity: AddStaffDataClass, data: AdvanceMoneyData, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        updatePendingAdvance(employeeIdentity, data, onSuccess, onFailure, 2)

    }
    fun listenForAdvanceUpdates(
        firmName: String,
        category: String,
        onUpdate: (String, List<AdvanceMoneyData>) -> Unit
    ): ListenerRegistration {
        Log.d("PendingAdvance", "FirmName: $firmName, Category: $category")
        return firestore.collection("Firms")
            .document(firmName)
            .collection(category)
            .addSnapshotListener { snapshot, error ->
                Log.d("PendingAdvance", "Snapshot: ${snapshot?.documents}, Error: $error")
                if (error != null) {
                    return@addSnapshotListener
                }

                snapshot?.let {
                    Log.d("PendingAdvance", "Documents: ${it.documents}")
                    val employees = it.documents.mapNotNull { doc -> doc.toObject(AdvanceMoneyData::class.java) }
                    Log.d("PendingAdvance", "Employees: $employees")
                    if (it.documentChanges.isNotEmpty()) {
                        val message = when (it.documentChanges.first().type) {
                            DocumentChange.Type.ADDED -> "Leaves loaded!"
                            DocumentChange.Type.MODIFIED -> "Leaves data updated!"
                            DocumentChange.Type.REMOVED -> "Leaves removed!"
                        }
                        Log.d("PendingAdvance", "Message: $message")
                        onUpdate(message, employees)
                    }
                }
            }
    }


    suspend fun fetchPendingAttendance(firmName: String): List<OutForWork> {
        val roleCollection = "pendingAttendance"
        val firmRef = firestore.collection("Firms")
            .document(firmName)
            .collection(roleCollection)

        Log.d("PendingAttendance", "FirmRef: ${firmRef.path}")

        return try {
            val snapshot = firmRef.get().await()
            Log.d("PendingAttendance", "Snapshot: ${snapshot.documents}")

            val attList = mutableListOf<OutForWork>()

            for (entryDoc in snapshot.documents) {
                val data = entryDoc.data
                if (data != null) {
                    try {

                        val att = OutForWork(
                            id = entryDoc.id, // Use the document ID as the ID
                            date = data["date"] as? String ?: "", // Safely cast to String
                            duration = (data["duration"] as? Number)?.toInt() ?: 0, // Safely cast to Int
                            name = data["name"] as? String ?: "", // Safely cast to String
                            firmName = data["firmName"] as? String ?: "", // Safely cast to String
                            adminPhoneNumber = data["adminPhoneNumber"] as? String ?: "", // Safely cast to String
                            phoneNumber = data["phoneNumber"] as? String ?: "" // Safely cast to String
                        )
                        attList.add(att)
                    } catch (e: Exception) {
                        Log.e("PendingAttendance", "Error parsing Attendance document: ${e.message}", e)
                    }
                }
            }
            Log.d("PendingAttendance", "AdvanceList: $attList")
            attList
        } catch (e: Exception) {
            Log.e("PendingAttendance", "Error fetching Attendance: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun approvePendingAttendance(employeeIdentity: AddStaffDataClass, data: OutForWork, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val adminPhoneNumber = data.adminPhoneNumber.toString()
        val currentUserNumber = data.phoneNumber.toString()

        val updateAdminPhoneNumber = if (adminPhoneNumber.startsWith("+91")) adminPhoneNumber else "+91$adminPhoneNumber"

        val targetDate = data.date.toString()
        val dateParts = targetDate.split("-")
        if (dateParts.size != 3) {
            Log.e("AttendanceUpdate", "Invalid selectedDate format: ${data.date}")
            onFailure(Exception("Invalid selectedDate format"))
            return
        }

        val targetMonth = SimpleDateFormat("MMM", Locale.getDefault())
            .format(SimpleDateFormat("M", Locale.getDefault()).parse(dateParts[1])!!) // Converts "2" → "Feb"
        val targetYear = dateParts[2]


        try {
            val collectionRef = firestore.collection("Members")
                .document(updateAdminPhoneNumber)
                .collection("Employee")
                .document(currentUserNumber)
                .collection("Attendance")
                .document(targetYear)
                .collection(targetMonth)
                .document(targetDate)

            Log.d("Att", "collectionRef: ${collectionRef.path}")

            // Access the "TotalWorkDuration" field in the document
            val snapshot = collectionRef.get().await()

            // Fetch the current value of "TotalWorkDuration" or default to 0
            val previousDuration = snapshot.getLong("TotalWorkDuration") ?: 0
            val newDuration = data.duration?.toInt() ?:0

            // Calculate the updated duration
            val updatedDuration = previousDuration + newDuration

            // Update the "TotalWorkDuration" field in the document
            collectionRef.update("TotalWorkDuration", updatedDuration).await()

            // If successful, invoke the success callback
            Log.d("Att", "TotalWorkDuration updated to $updatedDuration, $newDuration")
            deletePendingAttendance(employeeIdentity, data, onSuccess, onFailure)

        } catch (e: Exception) {
            Log.d("Att", "Error updating TotalWorkDuration: ${e.message}")
        }

    }
    suspend fun deletePendingAttendance(employeeIdentity: AddStaffDataClass, data: OutForWork, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val firmName=employeeIdentity.firmName.toString()
        val firmRef = firestore.collection("Firms")
            .document(firmName)

        // Delete from the pending employees collection
        val roleCollection =  "pendingAttendance"
        try {

            firmRef.collection(roleCollection)
                .document(data.id.toString())
                .delete()
                .await()
            onSuccess()
        }catch (e: Exception){
            onFailure(e)
        }

    }

    // Function to reject a pending employee
    suspend fun rejectPendingAttendance(employeeIdentity: AddStaffDataClass, data: OutForWork, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        deletePendingAttendance(employeeIdentity, data, onSuccess, onFailure)

    }
    fun listenForAttendanceUpdates(
        firmName: String,
        category: String,
        onUpdate: (String, List<OutForWork>) -> Unit
    ): ListenerRegistration {
        Log.d("PendingAttendance", "FirmName: $firmName, Category: $category")
        return firestore.collection("Firms")
            .document(firmName)
            .collection(category)
            .addSnapshotListener { snapshot, error ->
                Log.d("PendingAttendance", "Snapshot: ${snapshot?.documents}, Error: $error")
                if (error != null) {
                    return@addSnapshotListener
                }

                snapshot?.let {
                    Log.d("PendingAttendance", "Documents: ${it.documents}")
                    val employees = it.documents.mapNotNull { doc -> doc.toObject(OutForWork::class.java) }
                    Log.d("PendingAttendance", "Employees: $employees")
                    if (it.documentChanges.isNotEmpty()) {
                        val message = when (it.documentChanges.first().type) {
                            DocumentChange.Type.ADDED -> "Leaves loaded!"
                            DocumentChange.Type.MODIFIED -> "Leaves data updated!"
                            DocumentChange.Type.REMOVED -> "Leaves removed!"
                        }
                        Log.d("PendingAttendance", "Message: $message")
                        onUpdate(message, employees)
                    }
                }
            }
    }

}
