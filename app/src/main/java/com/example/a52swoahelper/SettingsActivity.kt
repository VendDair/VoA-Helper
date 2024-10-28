package com.example.a52swoahelper

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ProgressBar
import androidx.activity.ComponentActivity

class SettingsActivity : ComponentActivity() {

    private lateinit var downloadPeButton: Button
    private lateinit var downloadDriversButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        downloadPeButton = findViewById(R.id.download_pe_button)
        downloadDriversButton = findViewById(R.id.download_drivers_button)

        downloadPeButton.setOnClickListener {
            DownloadDialog(this).showDialog("pe.img")
        }

        downloadDriversButton.setOnClickListener {
            DownloadDialog(this).showDialog("Driver.zip")
        }

    }

}