package com.rach.firmmanagement.firmAdminOwner

sealed class ScreenAdmin(val route: String) {

    // Admin Panel Home Screen
    object AdminPanel : ScreenAdmin("AdminPanel")
    object AddWorkHours : ScreenAdmin("add workHour")
    object AddEmployee : ScreenAdmin("add Employee")
    object AddHoliday : ScreenAdmin("Add Holiday")
    object ViewEmployee : ScreenAdmin("View Employee")
    object ViewAllTask : ScreenAdmin("ViewAllTask")
    object EmployeeAttendance:ScreenAdmin("Employee Attended")
    object AddTask :ScreenAdmin("Add task")
    object AllExpenses: ScreenAdmin("All Expenses")

}