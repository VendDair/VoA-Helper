package com.example.a52swoahelper

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.res.AssetManager
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import com.example.a52swoahelper.Commands.Companion.executeCommand
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import kotlin.collections.contains

class Files {

    companion object {
        fun checkIfFileExists(filename: String, context: Context): Boolean {
            val assetManager: AssetManager = context.assets
            return try {
                val files = assetManager.list("") // List all files in the assets folder
                files?.contains(filename) == true // Check if the filename exists in the list
            } catch (e: IOException) {
                e.printStackTrace()
                false
            }
        }

        fun checkIfFolderExists(folderPath: String): Boolean {
            val folder = File(folderPath)
            return folder.exists() && folder.isDirectory
        }

        fun createFolderIfFolderDontExists(folderPath: String, context: Context) {
            if (!checkIfFolderExists(folderPath)) {
                executeCommand("su -c mkdir $folderPath")
                Toast.makeText(context, "No $folderPath was found so I created it", Toast.LENGTH_SHORT).show()
            }
        }

        fun checkIfFileExists(filePath: String): Boolean {
            val file = File(filePath)
            return file.exists() && file.isFile
        }

        fun alertUserIfFileDoesntExist(fileName: String, filePath: String, context: Context): Boolean {
            if (!checkIfFileExists(filePath + fileName)) {
                Toast.makeText(context, "$fileName was not found in $filePath", Toast.LENGTH_SHORT).show()
                return true
            }
            return false
        }


        fun copyAssetToTempFile(context: Context, assetFileName: String): File? {
            val assetManager: AssetManager = context.assets
            return try {
                val inputStream: InputStream = assetManager.open(assetFileName)
                val tempFile = File(context.cacheDir, assetFileName) // Create a temp file in cache directory
                val outputStream = FileOutputStream(tempFile)

                inputStream.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }
                tempFile // Return the temporary file
            } catch (e: IOException) {
                e.printStackTrace()
                null // Return null if there was an error
            }
        }

        fun copyAssetToLocal(context: Context, assetFileName: String) {
            val outputFilePath = "/data/local/tmp/$assetFileName"

            try {
                val tempFile = File(context.cacheDir, assetFileName)
                val inputStream: InputStream = context.assets.open(assetFileName)
                val outputStream = FileOutputStream(tempFile)

                inputStream.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }

                // Now copy the temporary file to /data/local/tmp using root privileges
                val command = "su -c cp ${tempFile.absolutePath} $outputFilePath"
                val process = Runtime.getRuntime().exec(command)
                process.waitFor()

                println("File copied to $outputFilePath")
            } catch (e: IOException) {
                e.printStackTrace()
                println("Error copying file: ${e.message}")
            } catch (e: InterruptedException) {
                e.printStackTrace()
                println("Process was interrupted: ${e.message}")
            }
        }
    }
}