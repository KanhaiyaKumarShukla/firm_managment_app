package com.rach.firmmanagement.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.rach.firmmanagement.R


class MyNotification(val context: Context, val title: String, val message: String) {

    val channelId = "FCM100"  // unique identifier for the notification channel.
    val channelName = "FCMMessage" // user-visible name for the notification channel, which appears in the device's notification settings.

    val notificationManager =
        context.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager // manages the display of notifications.

    lateinit var notificationChannel: NotificationChannel
    lateinit var notificationBuilder: NotificationCompat.Builder


    fun fireNotification() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            notificationChannel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)

            notificationChannel.enableVibration(true)
            notificationChannel.enableLights(true)
            notificationChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 300, 200, 1000)


        }

        notificationBuilder = NotificationCompat.Builder(context, channelId) //Used to build the actual notification.
        notificationBuilder.setSmallIcon(R.drawable.ic_launcher_background)
        notificationBuilder.setContentTitle(title)
        notificationBuilder.setContentText(message)
        notificationBuilder.setAutoCancel(true)

        notificationManager.notify(100, notificationBuilder.build())
    }
}