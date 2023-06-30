package com.example

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val extras = intent.extras
        if (extras != null) {
            val notificationStatus = extras.getString("notification_status")
            val fileName = extras.getString("file_name")
            val fileText = findViewById<TextView>(R.id.fileName)
            val statusText = findViewById<TextView>(R.id.statusName)

            fileText.text = fileName
            statusText.text = notificationStatus
        }

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


    }

}
