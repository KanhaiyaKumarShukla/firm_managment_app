package com.rach.firmmanagement.realRoomDatabase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
abstract class CheckingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun addData(checkingDataEntity: CheckingData)

    @Update
    abstract fun updateData(checkingDataEntity: CheckingData)

    @Query("SELECT * from OwnerData LIMIT 1")
    abstract fun getData():CheckingData?

}