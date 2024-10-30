package com.example.a52swoahelper

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.widget.Switch
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge

@SuppressLint("UseSwitchCompatOrMaterialCode")
class SettingsActivity : ComponentActivity() {

    private lateinit var mntSdcardSwitch: Switch


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        val settingsPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val editor = settingsPreferences.edit()

        mntSdcardSwitch = findViewById(R.id.MountToMnt)


        if (settingsPreferences.getBoolean("mountToMnt", false))
            mntSdcardSwitch.isChecked = true

        mntSdcardSwitch.setOnClickListener {

            editor.putBoolean("mountToMnt", !settingsPreferences.getBoolean("mountToMnt", false))

            editor.apply()
        }

    }

}