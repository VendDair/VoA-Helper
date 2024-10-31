package com.example.a52swoahelper

import android.annotation.SuppressLint

class InformationDialog() {

    @SuppressLint("SdCardPath")
    fun showDialog(text: String) {
        UniversalDialog(MainActivity.context).showDialog(
            image = R.drawable.info,
            title = "Information",
            text = text
        )
    }
}