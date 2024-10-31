package com.example.a52swoahelper

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge

class DownloadActivity: ComponentActivity() {

    private lateinit var winpeButton: LinearLayout
    private lateinit var driversButton: LinearLayout
    private lateinit var uefiButton: LinearLayout

    @SuppressLint("SdCardPath")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_download)

        winpeButton = findViewById(R.id.winpeButton)
        driversButton = findViewById(R.id.driversButton)
        uefiButton = findViewById(R.id.uefiButton)

        winpeButton.setOnClickListener {
            DownloadDialog(this).showDialog("pe.img")
        }
        driversButton.setOnClickListener {
            DownloadDialog(this).showDialog("Driver.zip")
        }
        uefiButton.setOnClickListener {
            DownloadDialog(this).showDialog("uefi.img", "/sdcard/UEFI/")
        }
    }
}