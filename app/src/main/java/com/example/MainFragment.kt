package com.example

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RadioButton
import androidx.databinding.DataBindingUtil
import com.example.databinding.FragmentMainBinding
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.button.ButtonState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL


enum class DownloadUrl(val value: Int) {
    GLIDE(R.string.glide),
    UDACITY(R.string.udacity),
    RETROFIT(R.string.retrofit),
    CUSTOM(R.string.custom_download)
}

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private val viewModel: MainViewModel by activityViewModels()

    private lateinit var notificationManager: NotificationManager
    private var downloadID: Long = 0

    private var downloadUrl: String = ""
    var fileName: String = ""

    private lateinit var addButton: FloatingActionButton
    private lateinit var urlEditText: EditText
    private lateinit var enterButton: Button
    private lateinit var optionFour: RadioButton
    private lateinit var urlLink: String
    private lateinit var userInput: String
    private lateinit var progressBar: ProgressBar


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_main, container, false
        )
        val activity = requireActivity()
        if (activity is AppCompatActivity) {
            activity.setSupportActionBar(binding.toolbar)
        }

        binding.lifecycleOwner = this


        addButton = binding.addReminderFAB
        urlEditText = binding.customUrl
        enterButton = binding.enterButton
        optionFour = binding.optionFour
        progressBar = binding.statusLoadingWheel


        addButton.setOnClickListener {
            openEditText()
        }

        enterButton.setOnClickListener {
            urlLink = userInput
            progressBar.visibility = View.VISIBLE
            hideSoftKeyboard()
            validateFileDownload(urlLink)

        }

        urlEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                userInput = s.toString()
                optionFour.text = userInput
            }
        })

        notificationManager = ContextCompat.getSystemService(
            requireContext().applicationContext,
            NotificationManager::class.java
        ) as NotificationManager

        createChannel(
            getString(R.string.download_notification_channel_id),
            getString(R.string.download_notification_channel_name),
            requireContext()
        )

        val radioGroup: RadioGroup = binding.radioGroup
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
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
                    fileName =
                        getString(R.string.retrofit_type_safe_http_client_for_android_and_java_by_square_inc)
                }

                R.id.option_four -> {
                    updateUrl(DownloadUrl.CUSTOM)
                    fileName =
                        "Custom Download"
                }
            }
        }

        binding.customButton.setOnClickListener {
            download()
        }


        return binding.root
    }

        private fun validateFileDownload(urlString: String) {
        lifecycleScope.launch {
            if (viewModel.isFileDownloadable(urlString)) {
                urlEditText.visibility = View.GONE
                enterButton.visibility = View.GONE
                optionFour.visibility = View.VISIBLE
            } else {
                Toast.makeText(requireContext(), "Enter a valid download link", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        progressBar.visibility = View.GONE
    }

    private fun hideSoftKeyboard() {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(urlEditText.windowToken, 0)
    }


//    private suspend fun isFileDownloadable(urlString: String): Boolean =
//        withContext(Dispatchers.IO) {
//            try {
//                val url = URL(urlString)
//                val connection = url.openConnection() as HttpURLConnection
//                connection.requestMethod = "HEAD"
//                connection.connect()
//
//                val responseCode = connection.responseCode
//                connection.disconnect()
//                responseCode == HttpURLConnection.HTTP_OK
//            } catch (e: MalformedURLException) {
//                false
//            } catch (e: Exception) {
//                false
//            }
//        }

    private fun openEditText() {
        urlEditText.visibility = View.VISIBLE
        enterButton.visibility = View.VISIBLE
        optionFour.visibility = View.GONE
    }

    private fun setButtonState(buttonState: ButtonState) {
        binding.customButton.setNewButtonState(buttonState)
    }

    private fun updateUrl(url: DownloadUrl) {
        downloadUrl = when (url) {
            DownloadUrl.GLIDE -> {
                getString(R.string.glide_url)
            }

            DownloadUrl.UDACITY -> {
                getString(R.string.udacity_url)
            }

            DownloadUrl.RETROFIT -> {
                getString(R.string.retrofit_url)
            }
            DownloadUrl.CUSTOM -> {
                urlLink.trim()
            }
        }
    }

//    private fun createChannel(channelId: String, channelName: String) {
//        //Create channel
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val notificationChannel = NotificationChannel(
//                channelId,
//                channelName,
//                NotificationManager.IMPORTANCE_DEFAULT
//            )
//
//                .apply {
//                    setShowBadge(false)
//                }
//
//            notificationChannel.enableLights(true)
//            notificationChannel.lightColor = Color.RED
//            notificationChannel.enableVibration(true)
//            notificationChannel.description = "download completed"
//
//            notificationManager = requireActivity().getSystemService(
//                NotificationManager::class.java
//            )
//            notificationManager.createNotificationChannel(notificationChannel)
//        }
//    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun download() {
        val applicationContext = requireContext().applicationContext
        if (downloadUrl == "") {
            Toast.makeText(requireContext(), "Choose a file to download", Toast.LENGTH_SHORT).show()
        } else {
            setButtonState(ButtonState.Loading)
            val request =
                DownloadManager.Request(Uri.parse(downloadUrl))
                    .setTitle(getString(R.string.app_name))
                    .setDescription(getString(R.string.app_description))
                    .setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_DOWNLOADS,
                        "filename.ext"
                    )
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)

            val downloadManager = requireActivity().getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID =
                downloadManager.enqueue(request)// enqueue puts the download request in the queue.

            val receiver = object : BroadcastReceiver() {
                @SuppressLint("Range")
                override fun onReceive(context: Context?, intent: Intent?) {
                    val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                    if (id == downloadID) {
                        val query = DownloadManager.Query()
                        query.setFilterById(id)
                        val cursor = downloadManager.query(query)
                        if (cursor.moveToFirst())
                            when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                                DownloadManager.STATUS_SUCCESSFUL -> {
                                    val uri =
                                        cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
                                    notificationManager.sendNotifications(
                                        applicationContext.getString(R.string.notification_successful),
                                        applicationContext, fileName, "Success", uri
                                    )
                                    setButtonState(ButtonState.Completed)
                                }

                                DownloadManager.STATUS_FAILED -> {
                                    val uri = ""
                                    notificationManager.sendNotifications(
                                        applicationContext.getString(R.string.notification_failed),
                                        applicationContext, fileName, "Failed", uri
                                    )
                                    setButtonState(ButtonState.Completed)
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
            requireActivity().registerReceiver(receiver, intent)
        }
    }
}
