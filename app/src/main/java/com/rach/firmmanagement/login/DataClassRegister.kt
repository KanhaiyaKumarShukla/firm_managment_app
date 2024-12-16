package com.rach.firmmanagement.login

data class DataClassRegister(
    val name:String? ="",
    val mobileNumber:String?= "",
    val email:String? ="",
    val firmName:String? = "",
    val address:String?="",
    val city:String?="",
    val pinCode:String? =""
)
