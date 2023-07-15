package com.example

import android.app.Application
import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class MainViewModel(application: Application) : AndroidViewModel(application){

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