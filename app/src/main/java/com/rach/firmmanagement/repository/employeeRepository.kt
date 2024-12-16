package com.rach.firmmanagement.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.rach.firmmanagement.dataClassImp.AddTaskDataClass
import com.rach.firmmanagement.dataClassImp.AdvanceMoneyData
import com.rach.firmmanagement.dataClassImp.EmployeeSectionData
import com.rach.firmmanagement.dataClassImp.PunchInPunchOut
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
                val commonTaskList = commonTaskData.documents.map { document ->
                    AddTaskDataClass(
                        date = document.getString("date"),
                        task = document.getString("task")
                    )
                }
                combinedTaskList.addAll(commonTaskList)
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




}