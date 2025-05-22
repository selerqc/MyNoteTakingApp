package com.example.mynotetakingapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class EditNoteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_note)

        val editText = findViewById<EditText>(R.id.editTextNote)
        val buttonSave = findViewById<Button>(R.id.buttonSaveNote)

        val note = intent.getStringExtra("note")
        val position = intent.getIntExtra("position", -1)

        editText.setText(note)

        buttonSave.setOnClickListener {
            val updatedNote = editText.text.toString()
            val resultIntent = Intent()
            resultIntent.putExtra("updatedNote", updatedNote)
            resultIntent.putExtra("position", position)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
}
