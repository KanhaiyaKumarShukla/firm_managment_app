package com.rach.firmmanagement.repository

import androidx.compose.runtime.mutableStateOf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.rach.firmmanagement.dataClassImp.AddStaffDataClass
import com.rach.firmmanagement.dataClassImp.AddTaskDataClass
import com.rach.firmmanagement.dataClassImp.AddWorkingHourDataClass
import com.rach.firmmanagement.dataClassImp.HolidayAndHoursDataClass
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
            taskList.addAll(commonTaskData.documents.map { document ->
                AddTaskDataClass(
                    date = document.getString("date"),
                    task = document.getString("task")
                )
            })
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

}