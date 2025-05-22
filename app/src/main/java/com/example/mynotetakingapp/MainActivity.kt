package com.example.mynotetakingapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import android.widget.EditText

class MainActivity : AppCompatActivity() {

    private lateinit var editTextNote: EditText
    private lateinit var buttonAddNote: Button
    private lateinit var recyclerView: RecyclerView
    private val noteList = mutableListOf<Note>()
    private lateinit var noteAdapter: NoteAdapter
    private var noteIdCounter = 0

    companion object {
        const val EDIT_NOTE_REQUEST = 1
        const val EXTRA_NOTE_ID = "extra_note_id"
        const val EXTRA_NOTE_TEXT = "extra_note_text"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextNote = findViewById(R.id.editTextNote)
        buttonAddNote = findViewById(R.id.buttonAddNote)
        recyclerView = findViewById(R.id.recyclerView)

        noteAdapter = NoteAdapter(noteList,
            onClick = { note -> editNote(note) },
            onLongClick = { note -> deleteNote(note) })

        recyclerView.adapter = noteAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        buttonAddNote.setOnClickListener {
            val noteText = editTextNote.text.toString()
            if (noteText.isNotBlank()) {
                val note = Note(noteIdCounter++, noteText)
                noteList.add(0, note)
                noteAdapter.notifyItemInserted(0)
                editTextNote.text.clear()
                recyclerView.scrollToPosition(0)
            }
        }
    }

    private fun editNote(note: Note) {
        val intent = Intent(this, EditNoteActivity::class.java).apply {
            putExtra(EXTRA_NOTE_ID, note.id)
            putExtra(EXTRA_NOTE_TEXT, note.text)
        }
        startActivityForResult(intent, EDIT_NOTE_REQUEST)
    }

    private fun deleteNote(note: Note) {
        val position = noteList.indexOfFirst { it.id == note.id }
        if (position != -1) {
            noteList.removeAt(position)
            noteAdapter.notifyItemRemoved(position)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == EDIT_NOTE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val id = data.getIntExtra(EXTRA_NOTE_ID, -1)
            val updatedText = data.getStringExtra(EXTRA_NOTE_TEXT) ?: return

            val index = noteList.indexOfFirst { it.id == id }
            if (index != -1) {
                noteList[index].text = updatedText
                noteAdapter.notifyItemChanged(index)
            }
        }
    }
}
