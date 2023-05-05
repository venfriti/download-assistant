package com.example

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*



enum class DownloadUrl(val value: Int) {
    GLIDE(R.string.glide),
    UDACITY(R.string.udacity),
    RETROFIT(R.string.retrofit)
}

class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    private var downloadUrl: String = ""
    var fileName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager

        createChannel(
            getString(R.string.download_notification_channel_id),
            getString(R.string.download_notification_channel_name)
        )

        val radioGroup: RadioGroup = findViewById(R.id.radio_group)
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId){
                R.id.option_one -> {
                    updateUrl(DownloadUrl.GLIDE)
                    fileName = getString(R.string.glide_image_loading_library_by_bumptech)
                }
                R.id.option_two -> {
                    updateUrl(DownloadUrl.UDACITY)
                    fileName = getString(R.string.loadapp_current_repository_by_udacity)
                }
                R.id.option_three -> {
                    updateUrl(DownloadUrl.RETROFIT)
                    fileName = getString(R.string.retrofit_type_safe_http_client_for_android_and_java_by_square_inc)
                }
            }
        }

        custom_button.setOnClickListener {
            download()
        }
    }

    private fun updateUrl(url: DownloadUrl){
        downloadUrl = when (url){
            DownloadUrl.GLIDE -> {
                getString(R.string.glide_url)
            }

            DownloadUrl.UDACITY -> {
                getString(R.string.udacity_url)
            }

            DownloadUrl.RETROFIT -> {
                getString(R.string.retrofit_url)
            }
        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        //Create channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
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

            notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun download() {
        if (downloadUrl == ""){
            Toast.makeText(this, "Choose a file to download", Toast.LENGTH_SHORT).show()
        } else{
            buttonState = ButtonState.Loading
            val request =
                DownloadManager.Request(Uri.parse(downloadUrl))
                    .setTitle(getString(R.string.app_name))
                    .setDescription(getString(R.string.app_description))
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "filename.ext")
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID =
                downloadManager.enqueue(request)// enqueue puts the download request in the queue.

            val receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                    if (id == downloadID) {
                        val query = DownloadManager.Query()
                        query.setFilterById(id)
                        val cursor = downloadManager.query(query)
                        if (cursor.moveToFirst())
                            when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                                DownloadManager.STATUS_SUCCESSFUL -> {
                                    notificationManager.sendNotifications(
                                        applicationContext.getString(R.string.notification_successful),
                                        applicationContext, fileName, "Success")
                                    buttonState = ButtonState.Completed
                                }
                                DownloadManager.STATUS_FAILED -> {
                                    notificationManager.sendNotifications(
                                        applicationContext.getString(R.string.notification_failed),
                                        applicationContext, fileName, "Failed")
                                    buttonState = ButtonState.Completed
                                }
                                else -> {
                                    val bytesDownloaded =
                                        cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                                    val bytesTotal =
                                        cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                                    val percent = bytesDownloaded * 100 / bytesTotal
                                    // Update the download progress
                                }
                            }
                    }
                }
            }
            val intent = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
            registerReceiver(receiver, intent)
        }
    }

    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
    }

}
