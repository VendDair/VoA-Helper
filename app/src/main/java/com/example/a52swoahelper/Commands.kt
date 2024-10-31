package com.example.a52swoahelper

import android.annotation.SuppressLint
import android.content.SharedPreferences


class Commands {
    companion object {

        fun adjustIndexList(strings: List<String>): List<String> {
            return strings.mapIndexed { index, value -> "${index + 1}. $value" }
        }

        fun cleanIndexList(strings: List<String>): List<String> {
            return strings.mapIndexed { _, value -> value.replace("\n", "") }
        }

        fun executeCommand(command: String) {
            try {

                val process = Runtime.getRuntime().exec(command)
                process.waitFor()

                process.inputStream.bufferedReader().use { it.readText() }

            } catch (e: Exception) {
                e.printStackTrace()
                "Error: ${e.message}"
            }

        }

        fun executeCommand(command: String, returnOutput: Boolean = false): String {
            return try {

                val process = Runtime.getRuntime().exec(command)
                process.waitFor()
                val output = process.inputStream.bufferedReader().use { it.readText() }


                if (returnOutput) {
                    output
                } else {
                    "0"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                "Error: ${e.message}"
            }
        }


        @SuppressLint("SdCardPath")
        fun bootIntoWindows() {
            Files.createFolderIfFolderDontExists("/sdcard/UEFI", MainActivity.context)
            if (Files.alertUserIfFileDoesntExist("uefi.img", "/sdcard/UEFI/", MainActivity.context)) return
            val command = "su -c dd if=/sdcard/UEFI/uefi.img of=/dev/block/by-name/boot bs=8M"
            executeCommand(command)
            executeCommand("su -c reboot")
        }


        fun isWindowsMounted(): Boolean {
            val winPartition = executeCommand("su -c realpath /dev/block/by-name/win", true)
            val isMounted = executeCommand("su -c mount | grep $winPartition", true)
            return isMounted.isNotEmpty()
        }

        @SuppressLint("SdCardPath")
        fun mountWindows(settingsPreferences: SharedPreferences): Boolean {
            val winPartition = executeCommand("su -c realpath /dev/block/by-name/win", true)
            val mountFolder = if (settingsPreferences.getBoolean("mountToMnt", false)) "/mnt/sdcard/Windows" else "/sdcard/Windows"
            settingsPreferences.edit().putString("mountFolder", mountFolder).apply()
            if (!isWindowsMounted()) {
                executeCommand("mkdir $mountFolder")
                executeCommand("su -mm -c /data/local/tmp/mount.ntfs -o rw $winPartition $mountFolder")
                InformationDialog(settingsPreferences).showDialog("Windows was mounted to ${settingsPreferences.getString("mountFolder", "/sdcard/Windows")}")
                return true
            } else {
                executeCommand("su -mm -c umount $winPartition")
                executeCommand("su -c rm $mountFolder -rf")
            }
            return false
        }


        fun backupBootImage() {
            executeCommand("su -c dd bs=8M if=/dev/block/by-name/boot of=/sdcard/boot.img")
        }

    }
}