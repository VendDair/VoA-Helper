package com.example.a52swoahelper

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import kotlin.properties.Delegates

class TwoOptionDialog(private val context: Context) {
    private lateinit var dialog: Dialog

    fun showDialog(title: String, btn1_text: String, btn2_text: String, onBtn1Click: () -> Unit, onBtn2Click: () -> Unit) {
        // Inflate the custom layout
        val dialogView: View = LayoutInflater.from(context).inflate(R.layout.twooptions_layout, null)

        // Create the AlertDialog
        val dialogBuilder = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(true)

        val dialogText: TextView = dialogView.findViewById(R.id.text)

        val btn1: Button = dialogView.findViewById(R.id.btn1)
        val btn2: Button = dialogView.findViewById(R.id.btn2)

        dialogText.text = title
        btn1.text = btn1_text
        btn2.text = btn2_text

        btn1.setOnClickListener{
            onBtn1Click()
            dialog.dismiss()
        }
        btn2.setOnClickListener{
            onBtn2Click()
            dialog.dismiss()
        }


        dialog = dialogBuilder.create()
        dialog.show()
    }
}