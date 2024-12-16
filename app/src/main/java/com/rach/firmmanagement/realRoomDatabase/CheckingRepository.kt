package com.rach.firmmanagement.realRoomDatabase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CheckingRepository(private val checkingDao: CheckingDao) {

    suspend fun addData(checkingData: CheckingData){
        withContext(Dispatchers.IO){
            checkingDao.addData(checkingData)
        }
    }

    suspend fun updateData(checkingData: CheckingData){
        withContext(Dispatchers.IO){
            checkingDao.updateData(checkingData)
        }
    }

    suspend fun getData():CheckingData?{
        return withContext(Dispatchers.IO){
            checkingDao.getData()
        }
    }

}