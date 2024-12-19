package com.rach.firmmanagement.HomeScreen

sealed class ScreensManage(val route:String) {

     object PhoneLogin:ScreensManage("Login")

    object Register:ScreensManage("Register")
    object OtpScreen:ScreensManage("OtpScreen")
    object HomeLogi :ScreensManage("Home")
    object CheckData:ScreensManage("CheckData")
    object RaiseScreen:ScreensManage("Raise")
    object EmployeeHomeScreen:ScreensManage("EmployeeHomeScreen")
    object AdvanceMoney:ScreensManage("Advance Money")
    object RaiseAHoliday:ScreensManage("RaiseAHoliday")
    object RaiseExpense:ScreensManage("RaiseExpense")
    object goToTask:ScreensManage("GoToTask")
    object PunchInOut:ScreensManage("PunchInPunchOut")
    object AttendanceSummary:ScreensManage("Attendence Summary")
    object AllExpense: ScreensManage("All Expense")

}