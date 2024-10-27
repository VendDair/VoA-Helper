package com.example.a52swoahelper

import java.io.File

class Root {
    fun isDeviceRooted(): Boolean {
        val paths = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/bin/su"
        )

        return paths.any { File(it).exists() }
    }

    fun requestRootAccess(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec("su")
            process.waitFor()
            true
        } catch (e: Exception) {
            false
        }
    }
}