package com.example

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.R
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val extras = intent.extras
        if (extras != null) {
            val notificationStatus = extras.getString("notification_status")
            val statusText = findViewById<TextView>(R.id.fileName)
            statusText.text = notificationStatus
        }
    }

}
