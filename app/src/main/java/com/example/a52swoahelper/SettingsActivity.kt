package com.example.a52swoahelper

import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge

class SettingsActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        /*downloadPeButton = findViewById(R.id.download_pe_button)
        downloadDriversButton = findViewById(R.id.download_drivers_button)

        downloadPeButton.setOnClickListener {
            DownloadDialog(this).showDialog("pe.img")
        }

        downloadDriversButton.setOnClickListener {
            DownloadDialog(this).showDialog("Driver.zip")
        }*/

    }

}