package com.rach.firmmanagement.repository

import android.annotation.SuppressLint
import android.icu.util.Calendar
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.rach.firmmanagement.dataClassImp.AddTaskDataClass
import com.rach.firmmanagement.dataClassImp.AdvanceMoneyData
import com.rach.firmmanagement.dataClassImp.EmployeeSectionData
import com.rach.firmmanagement.dataClassImp.Expense
import com.rach.firmmanagement.dataClassImp.ExpenseItem
import com.rach.firmmanagement.dataClassImp.PunchInPunchOut
import com.rach.firmmanagement.dataClassImp.Remark
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EmployeeRepository(
) {

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
        employeeSectionData: EmployeeSectionData,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {

        val updateAdminNumber = if (adminPhoneNumber.startsWith("+91")) {
            adminPhoneNumber
        } else {
            "+91$adminPhoneNumber"
        }

        try {
            val data = database.collection("Members")
                .document(updateAdminNumber)
                .collection("RaiseAleave")
                .add(employeeSectionData)
                .await()

            onSuccess()
        } catch (_: Exception) {
            onFailure()
        }

    }

    suspend fun raiseAdvanceMoney(
        adminPhoneNumber: String,
        advanceMoneyData: AdvanceMoneyData,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {

        val updateAdminNumber = if (adminPhoneNumber.startsWith("+91")) {
            adminPhoneNumber
        } else {
            "+91$adminPhoneNumber"
        }

        try {

            val data = database.collection("Members")
                .document(updateAdminNumber)
                .collection("AdvanceMoney")
                .add(advanceMoneyData)
                .await()

            onSuccess()

        } catch (_: Exception) {
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

            // Fetch common tasks
            val commonTaskData = database.collection("Members")
                .document(updateAdminNumber)
                .collection("Tasks")
                .get()
                .await()

            if (!commonTaskData.isEmpty) {
                val commonTasks = commonTaskData.documents.mapNotNull { document ->
                    document.toObject(AddTaskDataClass::class.java)?.copy(id = document.id)
                }
                combinedTaskList.addAll(commonTasks)
            }

            // Fetch employee-specific tasks
            val employeeTasksRef = database.collection("Members")
                .document(updateAdminNumber)
                .collection("Employee")
                .document(employeeNumber)
                .collection("tasks")

            val employeeTaskSnapshot = employeeTasksRef.get().await()

            if (!employeeTaskSnapshot.isEmpty) {
                val employeeTaskList = employeeTaskSnapshot.documents.mapNotNull { doc ->
                    doc.toObject(AddTaskDataClass::class.java)
                }
                combinedTaskList.addAll(employeeTaskList)
            }

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
            val taskRef = if (isCommon) {
                // Path for common tasks
                database.collection("Members")
                    .document(adminPhoneNumber)
                    .collection("Tasks")
                    .document(taskId)
            } else {
                // Path for employee-specific tasks
                database.collection("Members")
                    .document(adminPhoneNumber)
                    .collection("Employee")
                    .document(employeePhone)
                    .collection("tasks")
                    .document(taskId)
            }

            // Fetch the current task data
            val snapshot = taskRef.get().await()
            val taskData = snapshot.toObject(AddTaskDataClass::class.java)

            // Append the new remark to the existing list
            val updatedRemarks = taskData?.remarks?.toMutableList() ?: mutableListOf()
            updatedRemarks.add(newRemark)

            // Update Firestore with the new remarks list
            taskRef.update("remarks", updatedRemarks).await()

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
                    .collection("tasks")
                    .document(taskId)
            }
            // Fetch the current task data from Firestore
            val snapshot = taskRef.get().await()
            val taskData = snapshot.toObject(AddTaskDataClass::class.java)

            // Extract remarks from the fetched task data
            val remarks = taskData?.remarks ?: emptyList()
            Log.d("TAG", "fetched successful")
            remarks

        } catch (e: Exception) {
            Log.e("TAG", "Failed to fetch remarks: ${e.message}")
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
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {

        try {
            val updateAdminNumber = if (adminPhoneNumber.startsWith("+91")) {
                adminPhoneNumber
            } else {
                "+91$adminPhoneNumber"
            }

            val yearDocRef = database.collection("Members")
                .document(updateAdminNumber)
                .collection("Employee")
                .document(employeeNumber)
                .collection("Expense")
                .document("$currentYear")

            val monthDocRef = yearDocRef.collection(currentMonth).document(todayDate) // Assuming `todayDate` is unique per day

// Ensure year and month documents exist
            yearDocRef.set(mapOf("created" to true)) // Placeholder field for the year document
                .addOnSuccessListener {
                    Log.d("expense", "Year document $currentYear created successfully.")

                    monthDocRef.set(mapOf("created" to true)) // Placeholder field for the month document
                        .addOnSuccessListener {
                            Log.d("expense", "Month document $currentMonth created successfully.")

                            // Now add the subcollection data
                            val collectionRef = monthDocRef.collection("Entries")
                            collectionRef.document(Timestamp.now().seconds.toString()).set(expense)
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


}