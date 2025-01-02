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
    val id: String = "",                    // Task ID
    val task: String = "",                  // Task description
    val date: String = "",                  // Task creation date
    val isCommon: Boolean = true,          // Flag for common task
    val remarks: List<Remark> = emptyList() // List of remarks
)

data class Remark(
    val person: String = "",   // Person who added the remark
    val message: String = "",  // Remark message
    val date: String = ""      // Date of the remark
)


data class ViewAllEmployeeDataClass(
    val name:String? = "",
    val phoneNumber:String? = "",
    val role:String? = "",
    val isSelected: Boolean = false
)

data class Festival(
    val name: String = "",
    val date: String = "",
    val year: String = "",
    val month: String="",
    val selected: Boolean = false
)

data class RegularHolidayItems(
    val weekOff:List<String> =emptyList(),
    val monthOff:List<Int> =emptyList(),
    val additionalOff:List<String> =emptyList()
)