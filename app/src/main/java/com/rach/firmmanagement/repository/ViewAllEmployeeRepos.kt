package com.rach.firmmanagement.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.rach.firmmanagement.dataClassImp.OutForWork
import com.rach.firmmanagement.dataClassImp.PunchInPunchOut
import com.rach.firmmanagement.dataClassImp.ViewAllEmployeeDataClass
import kotlinx.coroutines.tasks.await

class ViewAllEmployeeRepos {

    val database = FirebaseFirestore.getInstance()
    val currentUserPhoneNumber = FirebaseAuth.getInstance().currentUser?.phoneNumber.toString()

    suspend fun viewAllEmployee(
        viewAllEmployeeDataClass: ViewAllEmployeeDataClass,
        onSuccess: (List<ViewAllEmployeeDataClass>) -> Unit,
        onFailure: () -> Unit
    ) {

        try {
            val data = database.collection("Members")
                .document(currentUserPhoneNumber)
                .collection("Employee")
                .get()
                .await()


            if (!data.isEmpty) {
                val employeeList = data.documents.map { document ->

                    ViewAllEmployeeDataClass(
                        name = document.getString("name"),
                        phoneNumber = document.getString("phoneNumber"),
                        role = document.getString("role")
                    )

                }
                Log.d("Task", employeeList.toString())
                onSuccess(employeeList)
            } else {
                onFailure()
            }

        } catch (_: Exception) {
            onFailure()
        }


    }

    suspend fun deleteEmployee(phoneNumber: String) {
        try {
            database.collection("Members")
                .document(currentUserPhoneNumber)
                .collection("Employee")
                .document(phoneNumber) // employee ko phoneNumber se identify karenge
                .delete()
                .await()

        } catch (e: Exception) {
            e.printStackTrace()
            // Handle failure
        }

    }

    suspend fun getPunchInOutAttendanceForAllEmployees(
        targetYear: String,
        targetMonth: String,
        targetDate: String,
        onSuccess: (List<PunchInPunchOut>) -> Unit,
        onFailure: () -> Unit
    ) {
        try {
            // Step 1: Get all employees under the admin
            val employeesSnapshot = database.collection("Members")
                .document(currentUserPhoneNumber)
                .collection("Employee")
                .get()
                .await()

            // Check if there are any employees
            if (employeesSnapshot.isEmpty) {
                Log.d("Attendance", "No employees found.")
                onFailure()
                return
            }
            Log.d("TAG", "CurrentAdminPno. : $currentUserPhoneNumber")
            val attendanceData = mutableListOf<PunchInPunchOut>()

            // Step 2: Loop through each employee to fetch their attendance
            for (employeeDoc in employeesSnapshot.documents) {
                val employeePhoneNumber = employeeDoc.id // Use document ID as EmployeePhoneNumber

                Log.d("TAG", "Employee Phone Number Punch InOut: $employeePhoneNumber")
                // Path to the target date's attendance entries
                val attendanceSnapshot = database.collection("Members")
                    .document(currentUserPhoneNumber)
                    .collection("Employee")
                    .document(employeePhoneNumber)
                    .collection("Attendance")
                    .document(targetYear)
                    .collection(targetMonth)
                    .document(targetDate)
                    .collection("Entries")
                    .get()
                    .await()

                Log.d("TAG", "PunchInOut Date: $targetYear/ $targetMonth/ $targetDate")

                if (!attendanceSnapshot.isEmpty) {
                    attendanceSnapshot.documents.forEach { entry ->
                        val punchInData = entry.toObject(PunchInPunchOut::class.java)
                        punchInData?.let {
                            attendanceData.add(it)
                        }
                        Log.d("TAG", "Punch In Data: $punchInData")
                    }
                }
            }

            // Return collected attendance data
            onSuccess(attendanceData)

        } catch (e: Exception) {
            Log.e("TAG", "Error fetching attendance: ${e.message}")
            onFailure()
        }
    }

    suspend fun getOutOfWorkAttendanceForAllEmployees(
        targetYear: String,
        targetMonth: String,
        targetDate: String,
        onSuccess: (List<OutForWork>) -> Unit,
        onFailure: () -> Unit
    ) {


        try {
            // Mutable list to collect OutOfWork objects for all employees
            val outOfWorkList = mutableListOf<OutForWork>()

            // Step 1: Fetch all employees under the admin
            val employeesSnapshot = database.collection("Members")
                .document(currentUserPhoneNumber)
                .collection("Employee")
                .get()
                .await()

            if (employeesSnapshot.isEmpty) {
                Log.d("OutOfWork", "No employees found.")
                onFailure()
                return
            }

            // Step 2: Iterate through each employee to fetch their "OutOfWork" data
            for (employeeDoc in employeesSnapshot.documents) {
                val employeePhoneNumber = employeeDoc.id // Employee document ID is the phone number

                Log.d("TAG", "Employee Phone Number: $employeePhoneNumber")

                // Reference to the specific date's collection
                val dayRef = database.collection("Members")
                    .document(currentUserPhoneNumber)
                    .collection("Employee")
                    .document(employeePhoneNumber)
                    .collection("Attendance")
                    .document(targetYear)
                    .collection(targetMonth)
                    .document(targetDate)

                // Fetch the document for the specific date
                val daySnapshot = dayRef.get().await()

                // Map the data to the OutOfWork object
                val outOfWorkData = daySnapshot.toObject(OutForWork::class.java)
                if (outOfWorkData != null && !outOfWorkData.date.isNullOrEmpty()) {
                    outOfWorkList.add(outOfWorkData)
                }
                Log.d("TAG", "OutOfWork Data: $outOfWorkData")
            }

            // Return the collected OutOfWork data
            Log.d("TAG", outOfWorkList.toString())
            onSuccess(outOfWorkList)

        } catch (e: Exception) {
            // Handle errors
            Log.e("TAG", "Error fetching OutOfWork data", e)
            onFailure()
        }
    }

}

