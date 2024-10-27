package com.example.a52swoahelper

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView

class ProgressDialog(private val context: Context) {
    lateinit var dialog: Dialog

    private lateinit var dialogView: View

    fun showDialog(step: String) {
        // Inflate the custom layout
        dialogView = LayoutInflater.from(context).inflate(R.layout.progress_layout, null)

        // Create the AlertDialog
        val dialogBuilder = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(true)

        // Get references to the views
        val stepView: TextView = dialogView.findViewById(R.id.step)

        stepView.text = step

        // Create and show the dialog
        dialog = dialogBuilder.create()
        dialog.show()
    }
}