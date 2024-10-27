package com.example.a52swoahelper

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity


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
            //ProgressDialog(context).showDialog(command)
            try {

                val process = Runtime.getRuntime().exec(command)
                process.waitFor()

                process.inputStream.bufferedReader().use { it.readText() }

            } catch (e: Exception) {
                e.printStackTrace()
                "Error: ${e.message}"
            }
            /*            executor.execute {

                        }*/
            /*            GlobalScope.launch(Dispatchers.IO) {
                            try {

                                val process = Runtime.getRuntime().exec(command)
                                process.waitFor()

                                process.inputStream.bufferedReader().use { it.readText() }

                            } catch (e: Exception) {
                                e.printStackTrace()
                                "Error: ${e.message}"
                            }
                        }*/

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
            val isMounted = executeCommand("su -c mount | grep Windows", true)
            return isMounted.isNotEmpty()
        }

        @SuppressLint("SdCardPath")
        fun mountWindows(context: Context): String {
            Files.createFolderIfFolderDontExists("/sdcard/Windows", context)
            if (!Files.checkIfFileExists("/data/local/tmp/mount.ntfs")) {
                Files.copyAssetToLocal(context, "mount.ntfs")
                executeCommand("su -c chmod 777 /data/local/tmp/mount.ntfs")
            }
            val result = executeCommand(
                "su -mm -c /data/local/tmp/mount.ntfs -o rw /dev/block/by-name/win /sdcard/Windows",
                false
            )
            if (result != "0") return result
            return "0"
        }


        fun backupBootImage() {
            executeCommand("su -c dd bs=8M if=/dev/block/by-name/boot of=/sdcard/boot.img")
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
            return executeCommand("su -c /data/local/tmp/wimlib-imagex info $path | grep -E \"Index|Name|Description|Display Name|Display Description\" | grep -v -E \"Boot Index|Product Name\"", true)
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

            if (!Files.checkIfFileExists("/data/local/tmp/mkfs.fat")) {
                Files.copyAssetToLocal(context, "mkfs.fat")
                executeCommand("su -c chmod 777 /data/local/tmp/mkfs.fat")
            }
            if (!Files.checkIfFileExists("/data/local/tmp/mkfs.ntfs")) {
                Files.copyAssetToLocal(context, "mkfs.ntfs")
                executeCommand("su -c chmod 777 /data/local/tmp/mkfs.ntfs")
            }
            if (!Files.checkIfFileExists("/data/local/tmp/wimlib-imagex")) {
                Files.copyAssetToLocal(context, "wimlib-imagex")
                executeCommand("su -c chmod 777 /data/local/tmp/wimlib-imagex")
            }

            showWimFilesDialog(context, method)

        }
    }
}