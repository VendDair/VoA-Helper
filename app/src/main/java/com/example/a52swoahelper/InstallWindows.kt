package com.example.a52swoahelper

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import com.example.a52swoahelper.Commands.Companion.executeCommand

class InstallWindows {

    companion object {
        @SuppressLint("SdCardPath")
        private fun showWimFilesDialog(method: Int) {
            val wimFiles = fetchWimFiles()
            val wimFilesList = prepareWimFilesList(wimFiles)

            val indexDialog = IndexDialog(MainActivity.context)
            indexDialog.showDialog(
                title = "Select the wim you want to install",
                Commands.adjustIndexList(wimFilesList)
            ) {
                val selectedWimFile = wimFilesList[indexDialog.index - 1]
                val path = "/sdcard/WindowsInstall/$selectedWimFile"

                val indexes = fetchWimFileIndexes(path)
                showIndexDialog(indexes, method, path)
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

        private fun showIndexDialog(indexes: String, method: Int, path: String) {
            val indexDialog = IndexDialog(MainActivity.context)
            indexDialog.showDialog(
                title = "Select index",
                listOf(indexes),
                Gravity.START
            ) {
                val index = indexDialog.index
                handleFlashMethod(method, MainActivity.context)
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

            showWimFilesDialog(method)

        }
    }


}