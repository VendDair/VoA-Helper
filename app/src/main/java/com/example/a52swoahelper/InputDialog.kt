package com.example.a52swoahelper

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import kotlin.properties.Delegates

class InputDialog(private val context: Context) {
    private lateinit var dialog: Dialog

    var text: String = ""

    fun showDialog(title: String, text: String,
                   textGravity: Int = Gravity.CENTER, callback: () -> Unit, imageId: Int) {
        // Inflate the custom layout
        val dialogView: View = LayoutInflater.from(context).inflate(R.layout.input_dialog, null)

        // Create the AlertDialog
        val dialogBuilder = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(true)

        val dialogText: TextView = dialogView.findViewById(R.id.text)
        val itemsText: TextView = dialogView.findViewById(R.id.items)
        val editText: EditText = dialogView.findViewById(R.id.index)
        val imageView: ImageView = dialogView.findViewById(R.id.image)

        dialogText.text = title
        itemsText.text = text
        itemsText.gravity = textGravity
        imageView.setImageResource(imageId)


        editText.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                // User pressed Enter
                // Attempt to convert the text to an integer
                val inputText = editText.text.toString()

                this.text = inputText

                dialog.dismiss()

                callback()

                dialog.dismiss()
                true // Indicate that the event was handled
            } else {
                false // Indicate that the event was not handled
            }
        }
        // Create and show the dialog
        dialog = dialogBuilder.create()
        dialog.show()
    }
}