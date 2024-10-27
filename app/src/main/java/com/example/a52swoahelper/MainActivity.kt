package com.example.a52swoahelper

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge

class MainActivity : ComponentActivity() {

    private lateinit var bootIntoWindowsButton: LinearLayout
    private lateinit var mountWindowsButton: LinearLayout
    private lateinit var helpButton: Button
    private lateinit var backupBootButton: LinearLayout
    private lateinit var installWindowsButton: LinearLayout

    @SuppressLint("SdCardPath")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        Commands.context = this

        Files.createFolderIfFolderDontExists("/sdcard/WindowsInstall", this)
        Files.createFolderIfFolderDontExists("/sdcard/UEFI", this)

        bootIntoWindowsButton = findViewById(R.id.BootIntoWindowsButton)
        bootIntoWindowsButton.setOnClickListener {
            val standartDialog = StandartDialog(this)
            standartDialog.showDialog(
                imageResId = R.drawable.win11logo,
                text = getString(R.string.boot_to_windows_dialog),
                onYesClick = {
                    Commands.bootIntoWindows(this)
                },
                onNoClick = {}
            )
        }

        mountWindowsButton = findViewById(R.id.MountWindowsButton)
        val mountWindows: TextView = findViewById(R.id.MountWindowsLabel)
        if (Commands.isWindowsMounted()) {
            mountWindows.text = getString(R.string.unmount_windows)
        }
        mountWindowsButton.setOnClickListener {
            val standartDialog = StandartDialog(this)
            standartDialog.showDialog(
                imageResId = R.drawable.folder,
                text = if (!Commands.isWindowsMounted()) getString(R.string.mount_windows_dialog) else getString(
                    R.string.unmount_windows_dialog
                ),
                onYesClick = {
                    if (Commands.isWindowsMounted()) {
                        Commands.executeCommand("su -mm -c umount /sdcard/Windows", true)

                        mountWindows.text = getString(R.string.mount_windows)
                        return@showDialog
                    } else {
                        Commands.mountWindows(this)


                        mountWindows.text = getString(R.string.unmount_windows)
                    }


                },
                onNoClick = {}
            )
        }

        helpButton = findViewById(R.id.HelpButton)
        helpButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/a52sxq_uefi"))
            startActivity(intent)
        }

        backupBootButton = findViewById(R.id.BackupBootButton)
        backupBootButton.setOnClickListener {
            val standartDialog = StandartDialog(this)
            standartDialog.showDialog(
                imageResId = R.drawable.cd,
                text = getString(R.string.backup_boot_image_dialog),
                onYesClick = {
                    Commands.backupBootImage()
                },
                onNoClick = {}
            )
        }

        installWindowsButton = findViewById(R.id.InstallWindowsButton)
        installWindowsButton.setOnClickListener {

            UniversalDialog(this).showDialog(
                title = "Do you want to install Windows?",
                text = "Only if you have win and esp partitions present",
                image = R.drawable.installer,
                buttons = listOf(
                    Pair("YES") {
                        UniversalDialog(this).showDialog(
                            title = "Select method",
                            buttons = listOf(
                                Pair("Pre-made Image\n(fast)") {
                                    Commands.installWindows(1)
                                },
                                Pair("Custom Image\n(slower)") {
                                    Commands.installWindows( 2)
                                }
                            )
                        )
                    },
                    Pair("GUIDE") {

                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://github.com/VendDair/VoA-Helper/blob/main/README.md")
                        )
                        startActivity(intent)

                    },
                    Pair("NO") {

                    },
                )
            )
        }
    }

}