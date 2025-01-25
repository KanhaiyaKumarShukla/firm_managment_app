package com.rach.firmmanagement.dataClassImp

data class MessageDataClass(
    val id: String = "",
    val senderName: String = "",
    val receiverName: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val seen: Boolean =false
)
