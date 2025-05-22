package com.example.mynotetakingapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText

class EditNoteActivity : AppCompatActivity() {

    private lateinit var editText: EditText
    private lateinit var saveButton: Button
    private var noteId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_note)

        editText = findViewById(R.id.editText)
        saveButton = findViewById(R.id.buttonSave)

        noteId = intent.getIntExtra(MainActivity.EXTRA_NOTE_ID, -1)
        val text = intent.getStringExtra(MainActivity.EXTRA_NOTE_TEXT) ?: ""
        editText.setText(text)

        saveButton.setOnClickListener {
            val updatedText = editText.text.toString()
            val resultIntent = Intent().apply {
                putExtra(MainActivity.EXTRA_NOTE_ID, noteId)
                putExtra(MainActivity.EXTRA_NOTE_TEXT, updatedText)
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
}
