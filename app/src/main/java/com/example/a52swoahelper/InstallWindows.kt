package com.example.a52swoahelper

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.widget.Toast
import com.example.a52swoahelper.Commands.Companion.executeCommand

class InstallWindows {

    companion object {
        @SuppressLint("SdCardPath")
        private fun showWimFilesDialog(onPathSelected: (String?) -> Unit) {
            val wimFiles = fetchWimFiles()
            val wimFilesList = prepareWimFilesList(wimFiles)

            val inputDialog = InputDialog(MainActivity.context)
            inputDialog.showDialog(
                title = "Select the wim/esd you want to install",
                text = Commands.adjustIndexList(wimFilesList).joinToString("\n")
            ) {
                // This is the callback that gets called when the dialog is confirmed
                val selectedIndex = inputDialog.text.toIntOrNull() // Safely convert to Int
                if (selectedIndex != null && selectedIndex in 1..wimFilesList.size) {
                    val selectedWimFile = wimFilesList[selectedIndex - 1]
                    val path = "/sdcard/WindowsInstall/$selectedWimFile"
                    onPathSelected(path) // Pass the path to the callback
                } else {
                    onPathSelected(null) // Handle invalid input
                }
            }
        }

        private fun fetchWimFiles(): String {
            return executeCommand("su -c ls /sdcard/WindowsInstall/ | grep -E \".wim|.esd\"", true)
        }

        private fun prepareWimFilesList(wimFiles: String): List<String> {
            return wimFiles.split("\n").dropLast(1).let { Commands.cleanIndexList(it) }
        }

        private fun fetchWimFileIndexes(path: String): String {
            return executeCommand(
                "su -c /data/local/tmp/wimlib-imagex info $path | grep -E \"Index|Name|Description|Display Name|Display Description\" | grep -v -E \"Boot Index|Product Name\"",
                true
            )
        }

        private fun showIndexDialog(indexes: String, onIndexSelected: (Int?) -> Unit) {
            val inputDialog = InputDialog(MainActivity.context)
            inputDialog.showDialog(
                title = "Select index",
                text = indexes,
                textGravity = Gravity.START
            ) { text ->
                // Safely convert the text to Int and pass it to the callback
                val index = text.toIntOrNull()
                onIndexSelected(index) // Call the callback with the selected index
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
        fun install(method: Int) {
            if (Files.alertUserIfFileDoesntExist(
                    "pe.img",
                    "/sdcard/WindowsInstall/",
                    MainActivity.context
                )
            ) return
            if (Files.alertUserIfFileDoesntExist(
                    "Driver.zip",
                    "/sdcard/WindowsInstall/",
                    MainActivity.context
                )
            ) return

            showWimFilesDialog { path ->
                if (path == null) return@showWimFilesDialog

                val indexes = fetchWimFileIndexes(path)
                showIndexDialog(indexes) { index ->
                    if (index == null) return@showIndexDialog

                    handleFlashMethod(method, MainActivity.context)
                    savePathAndIndex(path, index)
                    rebootRecovery()
                }

            }

        }
    }


}