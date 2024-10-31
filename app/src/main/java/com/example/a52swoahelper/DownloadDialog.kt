package com.example.a52swoahelper

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

class DownloadDialog(private val context: Context) {
    private lateinit var dialog: Dialog
    private lateinit var terminalTextView: TextView
    private lateinit var coroutineScope: CoroutineScope
    private lateinit var stopButton: Button
    private lateinit var process: Process
    private lateinit var fileName: String

    fun cancelDownloading() {
        process.destroy()
        coroutineScope.cancel()
        Commands.executeCommand("su -c rm /sdcard/WindowsInstall/$fileName")
        dialog.dismiss()
    }

    fun showDialog(fileName: String, @SuppressLint("SdCardPath") outputPath: String = "/sdcard/WindowsInstall/") {
        this.fileName = fileName
        // Inflate the custom layout
        val dialogView: View = LayoutInflater.from(context).inflate(R.layout.download_dialog, null)

        // Create the AlertDialog
        val dialogBuilder = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(true)

        stopButton = dialogView.findViewById(R.id.stopButton)

        stopButton.isEnabled = false

        stopButton.setOnClickListener {
            cancelDownloading()
        }

        terminalTextView = dialogView.findViewById(R.id.terminal) // Assuming you have a TextView in your layout
        val downloadButton: Button = dialogView.findViewById(R.id.downloadButton)
        downloadButton.setOnClickListener {
            dialog.setCancelable(false)
            stopButton.isEnabled = !stopButton.isEnabled
            Files.createFolderIfFolderDontExists(outputPath, context)
//            executeCommand("su -c /data/local/tmp/curl -L -o /sdcard/WindowsInstall/$fileName https://github.com/VendDair/VoA-Helper/releases/download/FILES/$fileName")
            executeCommand("su -c /data/local/tmp/busybox wget https://github.com/VendDair/VoA-Helper/releases/download/FILES/$fileName -O $outputPath$fileName")
//            executeCommand("su -c \"/data/local/tmp/busybox wget -P /sdcard/WindowsInstall/ -O /sdcard/WindowsInstall/pe.img https://github.com/VendDair/VoA-Helper/releases/download/FILES/pe.img\"\n")
        }

//        dialogBuilder.setOnCancelListener {
//            cancelDownloading()
//        }

        // Create and show the dialog
        dialog = dialogBuilder.create()
        dialog.show()
    }

    private fun executeCommand(command: String) {
        // Clear previous output
        terminalTextView.text = ""

        // Launch a coroutine to run the command in the background
        coroutineScope = CoroutineScope(Dispatchers.IO)
        coroutineScope.launch {
            try {
                // Start the process
                process = ProcessBuilder(command.split(" "))
                    .redirectErrorStream(true) // Redirect error stream to output stream
                    .start()


                // Read the output in real-time
                val reader = BufferedReader(InputStreamReader(process.inputStream))
                var line: String?

                while (reader.readLine().also { line = it } != null) {
                    // Update the TextView on the main thread
                    withContext(Dispatchers.Main) {
                        terminalTextView.append("$line\n")
                    }
                }

                // Wait for the process to finish
                process.waitFor()

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    terminalTextView.append("Error: ${e.message}\n")
                }
            } finally {
                // Dismiss the dialog after the command is finished
                withContext(Dispatchers.IO) {
                    Thread.sleep(1000)
                    dialog.dismiss()
                }
            }
        }
    }
}
