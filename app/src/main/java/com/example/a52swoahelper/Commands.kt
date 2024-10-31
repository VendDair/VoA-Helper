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

        private fun adjustIndexList(strings: List<String>): List<String> {
            return strings.mapIndexed { index, value -> "${index + 1}. $value" }
        }

        private fun cleanIndexList(strings: List<String>): List<String> {
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
        @SuppressLint("SdCardPath")
        fun showWimFilesDialog(context: Context, method: Int) {
            val wimFiles = fetchWimFiles()
            val wimFilesList = prepareWimFilesList(wimFiles)

            val indexDialog = IndexDialog(context)
            indexDialog.showDialog(
                title = "Select the wim you want to install",
                adjustIndexList(wimFilesList)
            ) {
                val selectedWimFile = wimFilesList[indexDialog.index - 1]
                val path = "/sdcard/WindowsInstall/$selectedWimFile"

                val indexes = fetchWimFileIndexes(path)
                showIndexDialog(context, indexes, method, path)
            }
        }

        private fun fetchWimFiles(): String {
            return executeCommand("su -c ls /sdcard/WindowsInstall/ | grep -E \".wim|.esd\"", true)
        }

        private fun prepareWimFilesList(wimFiles: String): List<String> {
            return wimFiles.split("\n").dropLast(1).let { cleanIndexList(it) }
        }

        private fun fetchWimFileIndexes(path: String): String {
            return executeCommand(
                "su -c /data/local/tmp/wimlib-imagex info $path | grep -E \"Index|Name|Description|Display Name|Display Description\" | grep -v -E \"Boot Index|Product Name\"",
                true
            )
        }

        private fun showIndexDialog(context: Context, indexes: String, method: Int, path: String) {
            val indexDialog = IndexDialog(context)
            indexDialog.showDialog(
                title = "Select index",
                listOf(indexes),
                Gravity.START
            ) {
                val index = indexDialog.index
                handleFlashMethod(method, context)
                savePathAndIndex(path, index)
                rebootRecovery()
            }
        }

        private fun handleFlashMethod(method: Int, context: Context) {
            val assetFileName = if (method == 1) "flash.zip" else "flash1.zip"
            Files.copyAssetToLocal(context, assetFileName)
            executeCommand("su -c echo install /data/local/tmp/$assetFileName > /cache/recovery/openrecoveryscript")
        }

        private fun savePathAndIndex(path: String, index: Int) {
            executeCommand("su -c echo $path > /data/local/tmp/path")
            executeCommand("su -c echo $index > /data/local/tmp/index")
        }

        private fun rebootRecovery() {
            executeCommand("su -c reboot recovery")
        }


        @SuppressLint("SdCardPath")
        fun installWindows(method: Int) {
            if (Files.alertUserIfFileDoesntExist(
                    "pe.img",
                    "/sdcard/WindowsInstall/",
                    context
                )
            ) return
            if (Files.alertUserIfFileDoesntExist(
                    "Driver.zip",
                    "/sdcard/WindowsInstall/",
                    context
                )
            ) return

            showWimFilesDialog(context, method)

        }
    }
}