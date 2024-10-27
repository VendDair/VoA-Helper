package com.example.a52swoahelper

import android.content.Context
import java.util.concurrent.ExecutorService


class Commands {
    companion object {

        lateinit var context: Context

        fun adjustIndexList(strings: List<String>): List<String> {
            return strings.mapIndexed { index, value -> "${index + 1}. $value" }
        }

        fun cleanIndexList(strings: List<String>): List<String> {
            return strings.mapIndexed { index, value -> value.replace("\n", "") }
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
            if (result != "0") return result.toString()
            return "0"
        }


        fun backupBootImage() {
            executeCommand("su -c dd bs=8M if=/dev/block/by-name/boot of=/sdcard/boot.img")
        }

        fun get_wim_esd_file(): List<String> {
            var path: String = "none"
            var index: String = "none"
            val wimFiles = executeCommand("su -c ls /sdcard/WindowsInstall/ | grep .wim", true)
            var wimFilesList: List<String> = wimFiles.split(" ")
            wimFilesList = cleanIndexList(wimFilesList)
            val wimFilesListRender = adjustIndexList(wimFilesList)
            val indexDialog = IndexDialog(context)
            indexDialog.showDialog(
                title = "Select the wim you want to install",
                wimFilesListRender
            ) {
                path = "/sdcard/WindowsInstall/" + wimFilesList[indexDialog.index - 1]
                index = indexDialog.index.toString()

                /*                installWindows(
                                    "/sdcard/WindowsInstall/" + wimFilesList[indexDialog.index - 1],
                                    indexDialog.index
                                )*/
            }
            return listOf(path, index)
        }

        fun installWindows(path: String, index: Int, method: Int) {
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
            /*if (!Files.checkIfFileExists("/data/local/tmp/openrecoveryscript")) {
                Files.copyAssetToLocal(context, "openrecoveryscript")
            }*/

            if (method == 1) {
                Files.copyAssetToLocal(context, "flash.zip")
                executeCommand("su -c echo install /data/local/tmp/flash.zip > /cache/recovery/openrecoveryscript")
            } else if (method == 2) {
                Files.copyAssetToLocal(context, "flash1.zip")
                executeCommand("su -c echo install /data/local/tmp/flash1.zip > /cache/recovery/openrecoveryscript")
            }
            //executeCommand("su -c cp /data/local/tmp/openrecoveryscript /cache/recovery/")

            executeCommand("su -c echo $path > /data/local/tmp/path")
            executeCommand("su -c echo $index > /data/local/tmp/index")

            executeCommand("su -c reboot recovery")


            /*                executeCommand("su -c /data/local/tmp/mkfs.fat -F32 -s1 /dev/block/bootdevice/by-name/esp -n ESPA52SXQ")
                            executeCommand("su -c /data/local/tmp/mkfs.ntfs -f /dev/block/bootdevice/by-name/win -L WINA52SXQ")
                            GlobalScope.launch(Dispatchers.IO) {
                                executeCommand("su -mm -c /data/local/tmp/wimlib-imagex apply $path /dev/block/by-name/win")
                                mountWindows(context)
                                executeCommand("su -c mkdir /sdcard/Windows/installer")
                                *//*            executeCommand("su -c cp /sdcard/WindowsInstall/install.bat /sdcard/Windows/installer")*//*

                    executeCommand("su -mm -c dd if=/sdcard/WindowsInstall/pe.img of=/dev/block/by-name/esp bs=4M")
                    executeCommand("su -c sync")

                    backupBootImage()
                    executeCommand("su -c cp /sdcard/boot.img /sdcard/Windows")

                    executeCommand("su -mm -c umount /sdcard/Windows")
                }*/


        }
    }
}