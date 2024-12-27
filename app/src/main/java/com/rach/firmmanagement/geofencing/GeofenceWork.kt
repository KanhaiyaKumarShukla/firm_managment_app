package com.rach.firmmanagement.geofencing

import android.content.Context
import com.rach.firmmanagement.firmAdminOwner.restoreGeofences
import androidx.work.Worker
import androidx.work.WorkerParameters

class GeofenceWork(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        restoreGeofences(applicationContext)
        return Result.success()
    }
}
