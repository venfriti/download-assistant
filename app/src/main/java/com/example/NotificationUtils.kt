package com.example

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat


private val NOTIFICATION_ID = 0


fun NotificationManager.sendNotifications(
    messageBody: String,
    applicationContext: Context,
    fileName: String,
    notificationStatus: String,
    uri: String
){

    //Create content intent for the notification, which launches detail activity

    val contentIntent = Intent(applicationContext, DetailActivity::class.java)
    contentIntent.putExtra("file_name", fileName)
    contentIntent.putExtra("notification_status", notificationStatus)
    contentIntent.putExtra("file_uri", uri)

    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.download_notification_channel_id)
    )

        .setSmallIcon(R.drawable.download_icon)
        .setContentTitle(fileName)
        .setContentText(messageBody)
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)

    notify(NOTIFICATION_ID, builder.build())

}

fun createChannel(channelId: String, channelName: String, context: Context) {
    //Create channel
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        )

            .apply {
                setShowBadge(false)
            }

        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.RED
        notificationChannel.enableVibration(true)
        notificationChannel.description = "download completed"

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(notificationChannel)
    }
}

