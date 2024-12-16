package com.rach.firmmanagement.dataClassImp

data class NoAdminDataClass(
    val firmName:String? = "",
    val phoneNumber:String? = "",
    val ownerName:String? = "",
    val address:String? = "",
    val pinCode:String? = "",
    val status:String? = "pending",
    val fcmToken:String? = ""
)
