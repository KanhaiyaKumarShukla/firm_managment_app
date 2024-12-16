package com.rach.firmmanagement.realRoomDatabase

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(
    entities = [CheckingData::class],
    version = 1,
    exportSchema = false
)
abstract class CheckingDatabase:RoomDatabase(){

    abstract fun CheckingDao():CheckingDao

}