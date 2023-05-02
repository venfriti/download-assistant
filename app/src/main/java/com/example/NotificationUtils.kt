package com.example

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat


private val NOTIFICATION_ID = 0
private val REQUEST_CODE = 0
private val FLAGS = 0

fun NotificationManager.sendNotifications(messageBody: String, applicationContext: Context){

    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.download_notification_channel_id)
    )

        .setSmallIcon(R.drawable.download_icon)
        .setContentTitle(applicationContext
            .getString(R.string.notification_title))
        .setContentText(messageBody)

    notify(NOTIFICATION_ID, builder.build())

}