package com.rach.firmmanagement.realRoomDatabase

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase


object Graph {

    lateinit var database: CheckingDatabase

    val checkingRepository by lazy {
        CheckingRepository(checkingDao = database.CheckingDao())
    }

    fun provide(context: Context){
        database = Room.databaseBuilder(
            context,
            CheckingDatabase::class.java,
            "checking.db"
        ).build()
    }

}