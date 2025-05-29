package com.example.mynotetakingapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView



class NoteAdapter(
    private var notes: MutableList<Note>,
    private val onItemClick: (Note) -> Unit,
    private val onItemDelete: (Note) -> Unit
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleView: TextView = itemView.findViewById(R.id.textViewTitle)
        val textView: TextView = itemView.findViewById(R.id.textViewNote)
        val timestampView: TextView = itemView.findViewById(R.id.textViewTimestamp)
        init {
            itemView.setOnClickListener {
                onItemClick(notes[adapterPosition])
            }
            itemView.setOnLongClickListener {
                onItemDelete(notes[adapterPosition])
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.titleView.text = note.title
        holder.textView.text = note.text
        holder.timestampView.text = java.text.SimpleDateFormat("MMM dd, yyyy hh:mm a").format(java.util.Date(note.timestamp))
        val animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.fade_in)
        holder.itemView.startAnimation(animation)
    }

    override fun getItemCount(): Int = notes.size

    fun updateNotes(newNotes: List<Note>) {
        notes.clear()
        notes.addAll(newNotes)
        notifyDataSetChanged()
    }

    fun removeNote(note: Note) {
        val index = notes.indexOf(note)
        if (index != -1) {
            notes.removeAt(index)
            notifyItemRemoved(index)
        }
    }
}
