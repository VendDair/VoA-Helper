package com.example.a52swoahelper

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.view.Gravity
import android.widget.Toast


class Commands {
    companion object {

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

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
        fun bootIntoWindows(context: Context) {
            Files.createFolderIfFolderDontExists("/sdcard/UEFI", context)
            if (Files.alertUserIfFileDoesntExist("uefi.img", "/sdcard/UEFI/", context)) return
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

        fun partition() {

            UniversalDialog(context).showDialog(
                title = "WARNING!",
                text = "Android may not work correctly after doing that\nConsider on making an backup!",
                image = R.drawable.warning,
                buttons = listOf(
                    Pair("Continue") {
                        val spaceDialog = InputDialog(context)
                        spaceDialog.showDialog(
                            title = "Storage",
                            text = "How much space you want to give to windows\nIn GB",
                            imageId = R.drawable.storage,
                            callback = {
                                val androidSpace = 127 - spaceDialog.text.toInt() - 0.5
                                executeCommand("su -c /data/local/tmp/parted /dev/block/sda rm 36")
                                executeCommand("su -c /data/local/tmp/parted /dev/block/sda rm 35")
                                executeCommand("su -c /data/local/tmp/parted /dev/block/sda rm 34")
                                executeCommand("su -c /data/local/tmp/parted /dev/block/sda mkpart userdata ext4 13.2GB ${androidSpace}GB")
                                executeCommand("su -c /data/local/tmp/parted /dev/block/sda mkpart esp fat32 ${androidSpace}GB ${androidSpace+0.5}GB")
                                executeCommand("su -c /data/local/tmp/parted /dev/block/sda mkpart win ntfs ${androidSpace+0.5}GB 100%")
                            }


                        )
                    },
                    Pair("Cancel") {}
                )
            )
        }


        // voa installers code

    }
}