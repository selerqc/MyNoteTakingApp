package com.example.mynotetakingapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray

class MainActivity : AppCompatActivity() {
    private lateinit var notes: MutableList<String>
    private lateinit var adapter: NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPreferences = getSharedPreferences("MyNotesPrefs", Context.MODE_PRIVATE)
        notes = loadNotes(sharedPreferences)

        val editTextNote = findViewById<EditText>(R.id.editTextNote)
        val buttonAddNote = findViewById<Button>(R.id.buttonAddNote)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        adapter = NoteAdapter(notes) { position ->
            val intent = Intent(this, EditNoteActivity::class.java)
            intent.putExtra("note", notes[position])
            intent.putExtra("position", position)
            startActivityForResult(intent, 1)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        buttonAddNote.setOnClickListener {
            val note = editTextNote.text.toString()
            if (note.isNotBlank()) {
                notes.add(note)
                saveNotes(sharedPreferences, notes)
                adapter.notifyItemInserted(notes.size - 1)
                editTextNote.text.clear()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            val updatedNote = data?.getStringExtra("updatedNote")
            val position = data?.getIntExtra("position", -1) ?: -1
            if (updatedNote != null && position != -1) {
                notes[position] = updatedNote
                saveNotes(getSharedPreferences("MyNotesPrefs", Context.MODE_PRIVATE), notes)
                adapter.notifyItemChanged(position)
            }
        }
    }

    private fun saveNotes(sharedPreferences: android.content.SharedPreferences, notes: List<String>) {
        val json = JSONArray(notes).toString()
        sharedPreferences.edit().putString("notes", json).apply()
    }

    private fun loadNotes(sharedPreferences: android.content.SharedPreferences): MutableList<String> {
        val json = sharedPreferences.getString("notes", "[]")
        val jsonArray = JSONArray(json)
        val list = mutableListOf<String>()
        for (i in 0 until jsonArray.length()) {
            list.add(jsonArray.getString(i))
        }
        return list
    }
}
