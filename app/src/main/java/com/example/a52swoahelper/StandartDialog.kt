package com.example.a52swoahelper

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

class StandartDialog(private val context: Context) {

    private lateinit var dialog: Dialog

    private lateinit var dialogView: View

    @SuppressLint("InflateParams")
    fun showDialog(imageResId: Int, text: String, onYesClick: () -> Unit, onNoClick: () -> Unit) {
        // Inflate the custom layout
        dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_layout, null)

        // Create the AlertDialog
        val dialogBuilder = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false)

        // Get references to the views
        val dialogImage: ImageView = dialogView.findViewById(R.id.image)
        val dialogText: TextView = dialogView.findViewById(R.id.text)
        val buttonYes: Button = dialogView.findViewById(R.id.button_yes)
        val buttonNo: Button = dialogView.findViewById(R.id.button_no)

        // Set the image and message
        dialogImage.setImageResource(imageResId)
        dialogText.text = text

        // Set button click listeners
        buttonYes.setOnClickListener {
            onYesClick()
            dialog.dismiss() // Close the dialog
        }

        buttonNo.setOnClickListener {
            onNoClick()
            dialog.dismiss() // Close the dialog
        }

        // Create and show the dialog
        dialog = dialogBuilder.create()
        dialog.show()
    }
}