package com.example.a52swoahelper

import com.example.a52swoahelper.Commands.Companion.executeCommand

class Partitions {
    companion object {

        private fun selectStorage() {
            val spaceDialog = InputDialog(MainActivity.context)
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
        }

        private fun showWarning() {
            UniversalDialog(MainActivity.context).showDialog(
                title = "WARNING!",
                text = "Android may not work correctly after doing that\nConsider on making an backup!",
                image = R.drawable.warning,
                buttons = listOf(
                    Pair("Continue") {
                        selectStorage()
                    },
                    Pair("Cancel") {}
                )
            )
        }

        fun partition() {
            showWarning()
        }
    }
}