package com.example

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)



        val radioGroup: RadioGroup = findViewById(R.id.radio_group)
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId){
                R.id.option_one -> {
                    updateUrl(DownloadUrl.GLIDE)
                    Toast.makeText(this, downloadUrl, Toast.LENGTH_SHORT).show()
                }
                R.id.option_two -> {
                    updateUrl(DownloadUrl.UDACITY)
                    Toast.makeText(this, downloadUrl, Toast.LENGTH_SHORT).show()
                }
                R.id.option_three -> {
                    updateUrl(DownloadUrl.RETROFIT)
                    Toast.makeText(this, downloadUrl, Toast.LENGTH_SHORT).show()
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

    private fun download() {
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
                    if (cursor.moveToFirst()) {
                        val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                        if (status == DownloadManager.STATUS_SUCCESSFUL) {
                            // Download completed successfully
                            val uri =
                                cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
                            // Do something with the downloaded file
                        } else if (status == DownloadManager.STATUS_FAILED) {
                            // Download failed
                            val reason =
                                cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON))
                            // Handle the failure
                        } else {
                            // Download is still in progress
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

    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
    }

}
