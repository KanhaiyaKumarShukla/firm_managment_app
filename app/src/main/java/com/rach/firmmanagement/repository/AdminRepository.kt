package com.rach.firmmanagement.repository

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.rach.firmmanagement.dataClassImp.AddStaffDataClass
import com.rach.firmmanagement.dataClassImp.AddTaskDataClass
import com.rach.firmmanagement.dataClassImp.AddWorkingHourDataClass
import com.rach.firmmanagement.dataClassImp.Expense
import com.rach.firmmanagement.dataClassImp.ExpenseItem
import com.rach.firmmanagement.dataClassImp.HolidayAndHoursDataClass
import com.rach.firmmanagement.dataClassImp.Remark
import com.rach.firmmanagement.dataClassImp.ViewAllEmployeeDataClass
import kotlinx.coroutines.tasks.await
import okhttp3.internal.wait

class AdminRepository {

    val database = FirebaseFirestore.getInstance()
    val currentUserPhoneNumber = FirebaseAuth.getInstance().currentUser?.phoneNumber.toString()

    suspend fun addStaff(
        addStaffDataClass: AddStaffDataClass,
        employeePhoneNumber: String,
        adminPhoneNumber: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {

        try {
            val data = database.collection("Members").document(adminPhoneNumber)

            data.collection("Employee")
                .document(employeePhoneNumber)
                .set(addStaffDataClass)
                .await()

            onSuccess()


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

        try {
            val formattedDate = date.replace("/", "-")

            val data = database.collection("Members")
                .document(adminPhoneNumber)

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
        try {
            // Reference to the document with a generated ID
            val taskRef = database.collection("Members")
                .document(adminPhoneNumber)
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

    suspend fun addWorkingHour(
        addWorkingHourDataClass: AddWorkingHourDataClass,
        date: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {

        try {

            val formattedDate = date.replace("/", "-")

            val data = database.collection("Members")
                .document(currentUserPhoneNumber)

            data.collection("WorkingHour")
                .document(formattedDate)
                .set(addWorkingHourDataClass)
                .await()

            onSuccess()

        } catch (_: Exception) {

            onFailure()

        }

    }

    suspend fun loadTasks(adminPhoneNumber: String): List<AddTaskDataClass> {
        val updateAdminNumber = if (adminPhoneNumber.startsWith("+91")) {
            adminPhoneNumber
        } else {
            "+91$adminPhoneNumber"
        }

        val taskList = mutableListOf<AddTaskDataClass>()
        val commonTaskData = database.collection("Members")
            .document(updateAdminNumber)
            .collection("Tasks")
            .get()
            .await()

        if (!commonTaskData.isEmpty) {
            val commonTasks = commonTaskData.documents.mapNotNull { document ->
                document.toObject(AddTaskDataClass::class.java)?.copy(id = document.id)
            }
            taskList.addAll(commonTasks)
        }
        return taskList
    }

    suspend fun deleteTask(adminPhoneNumber: String, task: AddTaskDataClass) {
        val updateAdminNumber = if (adminPhoneNumber.startsWith("+91")) {
            adminPhoneNumber
        } else {
            "+91$adminPhoneNumber"
        }

        val documentsToDelete = database.collection("Members")
            .document(updateAdminNumber)
            .collection("Tasks")
            .whereEqualTo("date", task.date)
            .whereEqualTo("task", task.task)
            .get()
            .await()

        for (document in documentsToDelete.documents) {
            document.reference.delete().await()
        }
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
                    .collection("tasks")
                    .document(taskId)
            }

            Log.d("TAG","$taskId, $isCommon, $employeePhone, $taskRef, $adminPhoneNumber")

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

}