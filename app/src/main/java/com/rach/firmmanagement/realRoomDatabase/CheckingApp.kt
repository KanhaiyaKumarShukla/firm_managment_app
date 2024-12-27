package com.rach.firmmanagement.realRoomDatabase

import android.app.Application
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.rach.firmmanagement.geofencing.GeofenceWork
import java.util.concurrent.TimeUnit

class CheckingApp:Application() {
    override fun onCreate() {
        super.onCreate()
        Graph.provide(this)
        // Schedule the periodic geofence work
        val request = PeriodicWorkRequestBuilder<GeofenceWork>(15, TimeUnit.MINUTES).build()
        WorkManager.getInstance(this).enqueue(request)
    }
}