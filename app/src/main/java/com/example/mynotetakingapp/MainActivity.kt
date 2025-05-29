package com.example.mynotetakingapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import androidx.core.content.edit
import androidx.appcompat.widget.SearchView
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {
    private lateinit var notes: MutableList<Note>
    private lateinit var filteredNotes: MutableList<Note>
    private lateinit var adapter: NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPreferences = getSharedPreferences("MyNotesPrefs", Context.MODE_PRIVATE)
        notes = loadNotes(sharedPreferences)
        filteredNotes = notes.toMutableList()

        val editTextTitle = findViewById<EditText>(R.id.editTextTitle)
        val editTextNote = findViewById<EditText>(R.id.editTextNote)
        val buttonAddNote = findViewById<Button>(R.id.buttonAddNote)
        val searchView = findViewById<SearchView>(R.id.searchView)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        adapter = NoteAdapter(filteredNotes,
            onItemClick = { note ->
                // Show a dialog to edit the note
                val editDialogView = layoutInflater.inflate(R.layout.dialog_edit_note, null)
                val editTitle = editDialogView.findViewById<EditText>(R.id.editDialogTitle)
                val editText = editDialogView.findViewById<EditText>(R.id.editDialogText)
                editTitle.setText(note.title)
                editText.setText(note.text)
                AlertDialog.Builder(this)
                    .setTitle("Edit Note")
                    .setView(editDialogView)
                    .setPositiveButton("Save") { _, _ ->
                        val newTitle = editTitle.text.toString().trim()
                        val newText = editText.text.toString().trim()
                        if (newTitle.isNotBlank() && newText.isNotBlank()) {
                            note.title = newTitle
                            note.text = newText
                            // note.timestamp = System.currentTimeMillis() // Will fix in Note data class
                            saveNotes(sharedPreferences, notes)
                            adapter.notifyDataSetChanged()
                        } else {
                            Toast.makeText(this, "Both fields are required.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            },
            onItemDelete = { note ->
                val index = filteredNotes.indexOf(note)
                if (index != -1) {
                    showDeleteConfirmation(note, index)
                }
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        buttonAddNote.setOnClickListener {
            addNote(editTextTitle, editTextNote, sharedPreferences)
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                filterNotes(newText)
                return true
            }
        })
    }

    private fun addNote(editTextTitle: EditText, editTextNote: EditText, sharedPreferences: android.content.SharedPreferences) {
        val title = editTextTitle.text.toString().trim()
        val text = editTextNote.text.toString().trim()
        if (title.isNotBlank() && text.isNotBlank()) {
            val note = Note(notes.size, title, text, System.currentTimeMillis())
            notes.add(note)
            filterNotes("")
            saveNotes(sharedPreferences, notes)
            adapter.notifyItemInserted(filteredNotes.size - 1)
            editTextTitle.text.clear()
            editTextNote.text.clear()
        } else {
            Toast.makeText(this, "Please enter both title and note.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun filterNotes(query: String?) {
        filteredNotes.clear()
        if (query.isNullOrBlank()) {
            filteredNotes.addAll(notes)
        } else {
            val lower = query.lowercase()
            filteredNotes.addAll(notes.filter { it.title.lowercase().contains(lower) || it.text.lowercase().contains(lower) })
        }
        adapter.notifyDataSetChanged()
    }

    private fun showDeleteConfirmation(note: Note, index: Int) {
        AlertDialog.Builder(this)
            .setTitle("Delete Note")
            .setMessage("Are you sure you want to delete this note?")
            .setPositiveButton("Delete") { _, _ ->
                val removedNote = filteredNotes.removeAt(index)
                notes.remove(removedNote)
                adapter.notifyItemRemoved(index)
                saveNotes(getSharedPreferences("MyNotesPrefs", Context.MODE_PRIVATE), notes)
                Snackbar.make(findViewById(R.id.recyclerView), "Note deleted", Snackbar.LENGTH_LONG)
                    .setAction("Undo") {
                        notes.add(index, removedNote)
                        filterNotes("")
                        saveNotes(getSharedPreferences("MyNotesPrefs", Context.MODE_PRIVATE), notes)
                    }.show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun loadNotes(sharedPreferences: android.content.SharedPreferences): MutableList<Note> {
        val notesJson = sharedPreferences.getString("notes", null)
        val notesList = mutableListOf<Note>()
        if (notesJson != null) {
            val jsonArray = org.json.JSONArray(notesJson)
            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.get(i)
                if (item is org.json.JSONObject) {
                    val obj = item
                    val id = obj.optInt("id", i)
                    val title = obj.optString("title", "Untitled")
                    val text = obj.optString("text", "")
                    val timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                    notesList.add(Note(id, title, text, timestamp))
                } else if (item is String) {
                    // Legacy format: just the note text
                    notesList.add(Note(i, "Untitled", item, System.currentTimeMillis()))
                }
            }
        }
        return notesList
    }

    private fun saveNotes(sharedPreferences: android.content.SharedPreferences, notes: List<Note>) {
        val jsonArray = org.json.JSONArray()
        for (note in notes) {
            val obj = org.json.JSONObject()
            obj.put("id", note.id)
            obj.put("title", note.title)
            obj.put("text", note.text)
            obj.put("timestamp", note.timestamp)
            jsonArray.put(obj)
        }
        sharedPreferences.edit().putString("notes", jsonArray.toString()).apply()
    }
}
