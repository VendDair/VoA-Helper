package com.example.a52swoahelper

import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.ComponentActivity

class DownloadActivity: ComponentActivity() {

    private lateinit var winpeButton: LinearLayout
    private lateinit var driversButton: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download)

        winpeButton = findViewById(R.id.winpeButton)
        driversButton = findViewById(R.id.driversButton)

        winpeButton.setOnClickListener {
            DownloadDialog(this).showDialog("pe.img")
        }
        driversButton.setOnClickListener {
            DownloadDialog(this).showDialog("Driver.zip")
        }
    }
}