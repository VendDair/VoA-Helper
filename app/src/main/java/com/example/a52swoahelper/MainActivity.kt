package com.example.a52swoahelper

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.example.a52swoahelper.Commands.Companion.context
import com.example.a52swoahelper.Commands.Companion.executeCommand

class MainActivity : ComponentActivity() {

    private lateinit var bootIntoWindowsButton: LinearLayout
    private lateinit var mountWindowsButton: LinearLayout
    private lateinit var helpButton: ImageView
    private lateinit var backupBootButton: LinearLayout
    private lateinit var installWindowsButton: LinearLayout
    private lateinit var downloadButton: LinearLayout

    private lateinit var versionView: TextView
    private lateinit var settingsButton: ImageView

    fun copyBinaries() {
        val binaries = listOf(
            "mkfs.fat",
            "mkfs.ntfs",
            "mount.ntfs",
            "wimlib-imagex",
            "curl",
        )

        binaries.forEach { binary ->
            if (!Files.checkIfFileExists("/data/local/tmp/$binary")) {
                Files.copyAssetToLocal(context, binary)
                executeCommand("su -c chmod 777 /data/local/tmp/$binary")
            }
        }
    }

    @SuppressLint("SdCardPath")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

//        val metrics: WindowMetrics = windowManager.currentWindowMetrics
//
//        val screenWidth = metrics.bounds.width()
//        val screenHeight = metrics.bounds.height()
//
//        versionView = findViewById(R.id.version)
//        versionView.textSize = screenHeight * 0.0052f

        Commands.context = this

        Files.createFolderIfFolderDontExists("/sdcard/WindowsInstall", this)
        Files.createFolderIfFolderDontExists("/sdcard/UEFI", this)
        Files.createFolderIfFolderDontExists("/sdcard/Windows", this)

        copyBinaries()

        val settingsPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)

        settingsButton = findViewById(R.id.settings)
        settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }



        downloadButton = findViewById(R.id.DownloadButton)
        downloadButton.setOnClickListener {
            val intent = Intent(this, DownloadActivity::class.java)
            startActivity(intent)
        }

        bootIntoWindowsButton = findViewById(R.id.BootIntoWindowsButton)
        bootIntoWindowsButton.setOnClickListener {
            UniversalDialog(this).showDialog(
                image = R.drawable.win11logo,
                title = getString(R.string.boot_to_windows_dialog),
                buttons = listOf(
                    Pair("YES") {
                        Commands.bootIntoWindows(this)
                    },
                    Pair("NO") {}
                )
            )
        }

        mountWindowsButton = findViewById(R.id.MountWindowsButton)
        val mountWindows: TextView = findViewById(R.id.MountWindowsLabel)
        if (Commands.isWindowsMounted()) {
            mountWindows.text = getString(R.string.unmount_windows)
        }
        mountWindowsButton.setOnClickListener {
            UniversalDialog(this).showDialog(
                title = "Mount/Unmount Windows",
                text = if (!Commands.isWindowsMounted()) getString(R.string.mount_windows_dialog) else getString(
                    R.string.unmount_windows_dialog
                ),
                image = R.drawable.folder,
                buttons = listOf(
                    Pair("YES") {
                        if (Commands.mountWindows(settingsPreferences)) {
                            mountWindows.text = getString(R.string.unmount_windows)
                            return@Pair
                        }
                        mountWindows.text = getString(R.string.mount_windows)
                    },
                    Pair("NO") {},
                )
            )
        }

        helpButton = findViewById(R.id.HelpButton)
        helpButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/a52sxq_uefi"))
            startActivity(intent)
        }

        backupBootButton = findViewById(R.id.BackupBootButton)
        backupBootButton.setOnClickListener {
            UniversalDialog(this).showDialog(
                title = "Backup boot partition",
                text = getString(R.string.backup_boot_image_dialog),
                image = R.drawable.cd,
                buttons = listOf(
                    Pair("YES") {
                        Commands.backupBootImage()
                    },
                    Pair("NO") {}
                ),
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