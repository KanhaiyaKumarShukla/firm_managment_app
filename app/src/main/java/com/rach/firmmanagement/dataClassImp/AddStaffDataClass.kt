package com.rach.firmmanagement.dataClassImp

/*
* used 2 phone number because if employee have to change phone number and i have use phone number as document id in firestore, then
* it will be difficult to update phone number with document id by creating a new document with new phone number and restoring all
* collections ans sub-collections and fields of old document to new document and then delete old document.
*/
data class AddStaffDataClass(
    val name:String? = "",
    val phoneNumber:String? = "",
    val newPhoneNumber:String? = "",
    val role:String? = "",
    val salary :String? = "",
    val registrationDate: String? ="",
    val timeVariation:String? = "",
    val leaveDays:String? = "",
    val workPlace: GeofenceItems = GeofenceItems()
)
