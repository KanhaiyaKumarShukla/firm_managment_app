package com.rach.firmmanagement.dataClassImp

data class EmployeeSectionData(
    val type:String? ="",
    val startingDate :String? ="",
    val endDate:String? ="",
    val reason:String?= "",
    val status:Boolean? = false,
    val currentDate:String?= "",
    val emlPhoneNumber:String? =""
)

data class AdvanceMoneyData(
    val reason: String? = "",
    val amount:String? ="",
    val date:String? = "",
    val emplPhoneNumber:String?="",
    val status: Boolean? = false,
    val time:String? =""
)

data class PunchInPunchOut(
    val currentTime:String?="",
    val absent:String?= "Present",
    val date:String?="",
    val punchTime:String?="",
    val punchOutTime:String?="",
    val locationPunchTime:String?="",
    val name:String?="",
    val phoneNumberString: String?="",
    val totalMinutes:Int=0
)

data class OutForWork(
    val date:String?="",
    val duration:Int?=0,
    val name:String?="",
)

data class EmployeeHomeScreenData(
    val name:String? ="",
    val role:String? ="",
    val registrationDate: String? =""
)

data class Expense(
    val moneyRaise: String,
    val items: List<Pair<String, String>>,
    val remaining: String,
    val selectedDate: String
)
