package com.rach.firmmanagement.firmAdminOwner

sealed class ScreenAdmin(val route: String) {

    // Admin Panel Home Screen
    object AdminPanel : ScreenAdmin("AdminPanel")
    object AddWorkHours : ScreenAdmin("add workHour")
    object AddEmployee : ScreenAdmin("add Employee")
    object AddHoliday : ScreenAdmin("Add Holiday")
    object ViewEmployee : ScreenAdmin("View Employee")
    object ViewAllTask : ScreenAdmin("ViewAllTask")
    object AllEmployeeAttendance: ScreenAdmin("All Employee Attendance")
    object EmployeeAttendance:ScreenAdmin("Employee Attended")
    object AddTask :ScreenAdmin("Add task")
    object AllExpenses: ScreenAdmin("All Expenses")
    object AddGeofence: ScreenAdmin("Add Geofence")
    object AddGeofenceByMap : ScreenAdmin("Add Geofence By Map")
    object HolidayTabMenu : ScreenAdmin( "Holiday Tab Menu")
    object WorkHours: ScreenAdmin("Work Hours")
    object ViewOneEmployeeTask: ScreenAdmin("One Employee Task")
    object EmployeeProfile: ScreenAdmin("Employee Profile")
    object EmployeeExpense:ScreenAdmin("Employee Task")
    object AdminChatScreen:ScreenAdmin("Admin Chat Screen")
    object AdminMessage:ScreenAdmin("Admin Message")
    object EmployeeChat:ScreenAdmin("Employee Chat")
    object EmployeeSalary:ScreenAdmin("Employee Salary")
    object Regularization:ScreenAdmin("Regularization")
    object RegularizationScreen : ScreenAdmin("regularization_screen/{categoryName}") {
        fun createRoute(categoryName: String) = "regularization_screen/$categoryName"
    }

}