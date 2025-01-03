package com.rach.firmmanagement.dataClassImp

data class AddStaffDataClass(
    val name:String? = "",
    val phoneNumber:String? = "",
    val role:String? = "",
    val salary :String? = "",
    val registrationDate: String? ="",
    val timeVariation:String? = "",
    val leaveDays:String? = "",
    val workPlace: GeofenceItems = GeofenceItems()
)
