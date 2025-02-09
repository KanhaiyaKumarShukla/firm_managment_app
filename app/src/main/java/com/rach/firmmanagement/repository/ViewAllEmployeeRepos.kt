package com.rach.firmmanagement.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.rach.firmmanagement.dataClassImp.AddStaffDataClass
import com.rach.firmmanagement.dataClassImp.GeofenceItems
import com.rach.firmmanagement.dataClassImp.OutForWork
import com.rach.firmmanagement.dataClassImp.PunchInPunchOut
import com.rach.firmmanagement.dataClassImp.ViewAllEmployeeDataClass
import kotlinx.coroutines.tasks.await

class ViewAllEmployeeRepos {

    val database = FirebaseFirestore.getInstance()
    val currentUserPhoneNumber = FirebaseAuth.getInstance().currentUser?.phoneNumber.toString()

    suspend fun viewAllEmployee(
        firmName: String,
        onSuccess: (List<AddStaffDataClass>) -> Unit,
        onFailure: () -> Unit
    ) {

        val tag ="allEmployee"

        try {
            val data = database.collection("Firms")
                .document(firmName)
                .collection("Employee")
                .get()
                .await()

            Log.d(tag, "Data: ${data.documents}")

            if (!data.isEmpty) {
                val employeeDetailsList = mutableListOf<AddStaffDataClass>()
                /*val employeeList = data.documents.map { document ->

                    ViewAllEmployeeDataClass(
                        name = document.getString("name"),
                        phoneNumber = document.getString("phoneNumber"),
                        role = document.getString("role")
                    )

                }*/

                for (document in data.documents) {
                    Log.d(tag, "Document: ${document.data}")
                    val phoneNumber = document.getString("phoneNumber") ?: continue

                    Log.d(tag, "Phone Number: $phoneNumber")

                    val employeeDetails = database.collection("Employee")
                        .document(phoneNumber)
                        .get()
                        .await()

                    Log.d(tag, "Employee Details: ${employeeDetails.data}")

                    if (employeeDetails.exists()) {
                        val details = AddStaffDataClass(
                            name = employeeDetails.getString("name"),
                            phoneNumber = employeeDetails.getString("phoneNumber"),
                            newPhoneNumber = employeeDetails.getString("newPhoneNumber"),
                            role = employeeDetails.getString("role"),
                            salary = employeeDetails.getString("salary"),
                            salaryUnit = employeeDetails.getString("salaryUnit"),
                            registrationDate = employeeDetails.getString("registrationDate"),
                            timeVariation = employeeDetails.getString("timeVariation"),
                            timeVariationUnit = employeeDetails.getString("timeVariationUnit"),
                            leaveDays = employeeDetails.getString("leaveDays"),
                            firmName = employeeDetails.getString("firmName"),
                            adminNumber = employeeDetails.getString("adminNumber"),
                            workPlace = employeeDetails.get("workPlace")?.let { locationData ->
                                if (locationData is Map<*, *>) {
                                    GeofenceItems(
                                        title = locationData["title"] as? String,
                                        latitude = locationData["latitude"] as? String,
                                        longitude = locationData["longitude"] as? String,
                                        radius = locationData["radius"] as? String,
                                        adminNo = locationData["adminNo"] as? String,
                                        firmName = locationData["firmName"] as? String
                                    )
                                } else {
                                    GeofenceItems()
                                }
                            } ?: GeofenceItems()
                        )
                        Log.d(tag, "Details: $details")

                        employeeDetailsList.add(details)
                    }
                }

                Log.d(tag, employeeDetailsList.toString())
                onSuccess(employeeDetailsList)
            } else {
                onFailure()
            }

        } catch (_: Exception) {
            onFailure()
        }


    }


    suspend fun viewAllAdmin(
        firmName: String,
        onSuccess: (List<AddStaffDataClass>) -> Unit,
        onFailure: () -> Unit
    ) {

        val tag="allAdmin"
        try {
            val data = database.collection("Firms")
                .document(firmName)
                .collection("Admin")
                .get()
                .await()

            Log.d(tag, "Data: ${data.documents}")

            if (!data.isEmpty) {
                val employeeDetailsList = mutableListOf<AddStaffDataClass>()

                for (document in data.documents) {
                    Log.d(tag, "Document: ${document.data}")
                    val phoneNumber = document.getString("phoneNumber") ?: continue
                    Log.d(tag, "Phone Number: $phoneNumber")

                    val employeeDetails = database.collection("Employee")
                        .document(phoneNumber)
                        .get()
                        .await()
                    Log.d(tag, "Employee Details: ${employeeDetails.data}")

                    if (employeeDetails.exists()) {
                        val details = AddStaffDataClass(
                            name = employeeDetails.getString("name"),
                            phoneNumber = employeeDetails.getString("phoneNumber"),
                            newPhoneNumber = employeeDetails.getString("newPhoneNumber"),
                            role = employeeDetails.getString("role"),
                            salary = employeeDetails.getString("salary"),
                            salaryUnit = employeeDetails.getString("salaryUnit"),
                            registrationDate = employeeDetails.getString("registrationDate"),
                            timeVariation = employeeDetails.getString("timeVariation"),
                            timeVariationUnit = employeeDetails.getString("timeVariationUnit"),
                            leaveDays = employeeDetails.getString("leaveDays"),
                            firmName = employeeDetails.getString("firmName"),
                            adminNumber = employeeDetails.getString("adminNumber"),
                            workPlace = employeeDetails.get("workPlace")?.let { locationData ->
                                if (locationData is Map<*, *>) {
                                    GeofenceItems(
                                        title = locationData["title"] as? String,
                                        latitude = locationData["latitude"] as? String,
                                        longitude = locationData["longitude"] as? String,
                                        radius = locationData["radius"] as? String,
                                        adminNo = locationData["adminNo"] as? String,
                                        firmName = locationData["firmName"] as? String
                                    )
                                } else {
                                    GeofenceItems()
                                }
                            } ?: GeofenceItems()
                        )

                        employeeDetailsList.add(details)
                    }
                }
                Log.d(tag, "DetailsList: $employeeDetailsList")

                if (employeeDetailsList.isNotEmpty()) {
                    onSuccess(employeeDetailsList)
                } else {
                    onFailure()
                }
            } else {
                onFailure()
            }

        } catch (_: Exception) {
            onFailure()
        }


    }

    suspend fun deleteEmployee(phoneNumber: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        try {
            database.collection("Employee")
                .document(phoneNumber) // employee ko phoneNumber se identify karenge
                .delete()
                .await()

            database.collection("Gender")
                .get()
                .addOnSuccessListener { documents ->
                    val document = documents.documents.firstOrNull()
                    if (document != null) {
                        val genderDocRef = database.collection("Gender").document(document.id)

                        // Use FieldValue.delete() to remove the phone number key
                        genderDocRef.update(mapOf(phoneNumber to FieldValue.delete()))
                            .addOnSuccessListener {
                                onSuccess()
                                Log.d("Firestore", "Phone number $phoneNumber removed successfully")
                            }
                            .addOnFailureListener { e ->
                                Log.e("Firestore", "Failed to remove phone number: ${e.message}")
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Failed to fetch document: ${e.message}")
                }


        } catch (e: Exception) {
            e.printStackTrace()
            // Handle failure
        }

    }
    suspend fun updateEmployeesToAdmin(
        selectedEmployees: List<AddStaffDataClass>,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        try {
            selectedEmployees.forEach { employee ->
                val genderCollection = database.collection("Gender")
                genderCollection.get()
                    .addOnSuccessListener { documents ->
                        val document = documents.documents.firstOrNull()
                        if (document != null) {
                            val genderDocRef = genderCollection.document(document.id)
                            genderDocRef.update(mapOf(employee.phoneNumber to "Admin"))
                        }
                    }

                val employeeRef = database.collection("Employee").document(employee.phoneNumber.toString())
                employeeRef.update("role", "Admin")
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { onFailure() }

                val firmRef = database.collection("Firms").document(employee.firmName.toString())

                if (employee.role != "Admin") {
                    val employeeRef = firmRef
                        .collection(employee.role.toString())
                        .document(employee.phoneNumber.toString())

                    val adminRef = firmRef
                        .collection("Admin")
                        .document(employee.phoneNumber.toString())

                    Log.d("Firestore", "Ref: ${adminRef.path}, ${employeeRef.path}")

                    // Get the employee data before deleting
                    employeeRef.get().addOnSuccessListener { document ->
                        Log.d("Firestore", "Document: $document")
                        if (document.exists()) {
                            val updatedEmployee = employee.copy(role = "Admin") // Update role to Admin

                            Log.d("Firestore", "Updated Employee: $updatedEmployee")

                            // Add employee to Admin collection
                            adminRef.set(updatedEmployee)
                                .addOnSuccessListener {
                                    // Delete from the original role collection after successful addition
                                    employeeRef.delete()
                                        .addOnSuccessListener {
                                            Log.d("Firestore", "Successfully moved to Admin")
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e("Firestore", "Failed to delete from previous role: ${e.message}")
                                        }
                                }
                                .addOnFailureListener { e ->
                                    Log.e("Firestore", "Failed to add employee to Admin: ${e.message}")
                                }
                        }
                    }.addOnFailureListener { e ->
                        Log.e("Firestore", "Failed to fetch employee: ${e.message}")
                    }
                }

            }
        }catch (e:Exception){
            onFailure()
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

