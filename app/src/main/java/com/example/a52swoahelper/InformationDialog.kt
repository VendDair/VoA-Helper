package com.example.a52swoahelper

import android.annotation.SuppressLint
import android.content.SharedPreferences
import com.example.a52swoahelper.Commands.Companion.context

class InformationDialog(val settingsPreferences: SharedPreferences) {

    @SuppressLint("SdCardPath")
    fun showDialog(text: String) {
        UniversalDialog(context).showDialog(
            image = R.drawable.info,
            title = "Information",
            text = text
        )
    }
}