package com.example.a52swoahelper

class Updates {
    companion object {

        fun checkUpdate(version: String): Boolean {
            if (!Internet.isNetworkConnected()) return false

            val remoteVersion = Commands.executeCommand("su -c /data/local/tmp/busybox wget --no-check-certificate -qO- https://github.com/VendDair/VoA-Helper/releases/download/FILES/latest_version_name", true)

            return !version.contains(remoteVersion)
        }
    }
}