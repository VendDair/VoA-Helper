package com.example.a52swoahelper

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import kotlin.properties.Delegates

class IndexDialog(private val context: Context) {

    private lateinit var dialog: Dialog

    var index by Delegates.notNull<Int>()

    fun showDialog(title: String, indexes: List<String>, textGravity: Int = Gravity.CENTER, onItemClick: () -> Unit) {
        // Inflate the custom layout
        val dialogView: View = LayoutInflater.from(context).inflate(R.layout.index_layout, null)

        // Create the AlertDialog
        val dialogBuilder = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(true)

        val dialogText: TextView = dialogView.findViewById(R.id.text)
        val itemsText: TextView = dialogView.findViewById(R.id.items)
        val editText: EditText = dialogView.findViewById(R.id.index)

        dialogText.text = title
        itemsText.text = indexes.joinToString("\n")
        itemsText.gravity = textGravity


        editText.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                // User pressed Enter
                // Attempt to convert the text to an integer
                val inputText = editText.text.toString()
                try {
                    index = inputText.toInt() // This may throw an exception if inputText is not a valid integer
                    dialog.dismiss()
                } catch (e: NumberFormatException) {
                    // Handle the error, e.g., show a message to the user
                    Toast.makeText(context, "Please enter a valid number", Toast.LENGTH_SHORT).show()
                }
                onItemClick()

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