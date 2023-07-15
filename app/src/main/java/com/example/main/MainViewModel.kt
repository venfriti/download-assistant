package com.example.main

import android.app.Application
import android.app.DownloadManager
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import com.example.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class MainViewModel(application: Application) : AndroidViewModel(application){

    private val appContext = getApplication<Application>()

    fun request(downloadUrl: String): DownloadManager.Request? {
        return DownloadManager.Request(Uri.parse(downloadUrl))
            .setTitle(appContext.getString(R.string.app_name))
            .setDescription(appContext.getString(R.string.app_description))
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "filename.ext"
            )
            .setRequiresCharging(false)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)
    }

    suspend fun isFileDownloadable(urlString: String): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "HEAD"
                connection.connect()

                val responseCode = connection.responseCode
                connection.disconnect()
                responseCode == HttpURLConnection.HTTP_OK
            } catch (e: MalformedURLException) {
                false
            } catch (e: Exception) {
                false
            }
        }
}