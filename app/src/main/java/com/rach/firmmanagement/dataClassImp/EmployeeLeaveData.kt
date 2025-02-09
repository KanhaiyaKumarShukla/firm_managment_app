package com.rach.firmmanagement.dataClassImp

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class EmployeeLeaveData(
    val id:String? ="",
    val type:String? ="",
    val startingDate :String? ="",
    val endDate:String? ="",
    val reason:String?= "",
    val status:Int? = 0,
    val currentDate:String?= "",
    val emlPhoneNumber:String? =""
)

data class AdvanceMoneyData(
    val id:String? ="",
    val reason: String? = "",
    val amount:String? ="",
    val date:String? = "",
    val emplPhoneNumber:String?="",
    val status: Int? = 0,
    val time:String? =""
)

data class PunchInPunchOut(
    val currentTime:String?="",
    val absent:String?= "Present",
    val date:String?="",
    val punchTime:String?="",
    val punchOutTime:String?="",
    val locationPunchTime: GeofenceItems?= GeofenceItems(),
    val name:String?="",
    val phoneNumberString: String?="",
    val totalMinutes:Int=0
)

data class OutForWork(
    val id:String?="",
    val date:String?="",
    val duration:Int?=0,
    val name:String?="",
    val firmName:String?="",
    val adminPhoneNumber:String?="",
    val phoneNumber:String?="",
)

data class EmployeeHomeScreenData(
    val name:String? ="",
    val role:String? ="",
    val registrationDate: String? =""
)

@IgnoreExtraProperties
data class Expense(
    val id:String="",
    val moneyRaise: String = "",
    val items: List<ExpenseItem> = emptyList(),
    val remaining: String = "",
    var selectedDate: String = "",
    val employeeNumber: String = "",
    val status: Boolean = false
){
    // No-argument constructor for Firestore
    constructor() : this("", "", emptyList(), "", "", "")
}

@IgnoreExtraProperties
data class ExpenseItem(
    val name:String,
    val value:String
){
    constructor() : this("", "")
}
