package com.rach.firmmanagement.realRoomDatabase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("OwnerData")
data class CheckingData(
    @PrimaryKey
    @ColumnInfo("id")
    val id: String,

    @ColumnInfo("whoIs")
    val whoIs: String,

    @ColumnInfo("ownerPhoneNumber")
    val ownerPhoneNumber: String
)
