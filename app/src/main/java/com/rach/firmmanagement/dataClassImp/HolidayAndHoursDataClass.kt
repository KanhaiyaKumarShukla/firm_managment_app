package com.rach.firmmanagement.dataClassImp

data class HolidayAndHoursDataClass(
    val holidayName:String?="",
    val holidayDate:String?=""
)

data class AddWorkingHourDataClass(
    val date:String="",
    val startingTime:String = "",
    val endTime:String = "",
    val currentTime:String = ""
)

data class AddTaskDataClass(
    val date:String?="",
    val task:String?=""
)

data class ViewAllEmployeeDataClass(
    val name:String? = "",
    val phoneNumber:String? = "",
    val role:String? = "",
    val isSelected: Boolean = false
)