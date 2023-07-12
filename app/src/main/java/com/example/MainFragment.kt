package com.example

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider

class MainFragment : Fragment() {

//    private var downloadID: Long = 0
//
//    private lateinit var notificationManager: NotificationManager
//
//    private var downloadUrl: String = ""
//    var fileName: String = ""
//    private lateinit var addButton: FloatingActionButton
//    private lateinit var urlEditText: EditText
//    private lateinit var enterButton: Button
//    private lateinit var optionFour: RadioButton
//    private lateinit var urlLink: String
//    private lateinit var userInput: String
//    private lateinit var progressBar: ProgressBar
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }
}