package com.example.fundo.ui.home.noteslist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fundo.R
import com.example.fundo.data.wrappers.Note

class NotesRecyclerAdapter(private val notesList: List<Note>) :
    RecyclerView.Adapter<NotesRecyclerAdapter.NotesViewHolder>() {
    private lateinit var recyclerListener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_item, parent, false)
        return NotesViewHolder(itemView, recyclerListener)
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        val currentNote = notesList[position]
        holder.titleText.text = currentNote.title
        holder.contentText.text = currentNote.content

        if (currentNote.title.isEmpty()) {
            holder.titleText.visibility = View.GONE
        } else {
            holder.titleText.visibility = View.VISIBLE
        }
        if (currentNote.content.isEmpty()) {
            holder.contentText.visibility = View.GONE
        } else {
            holder.contentText.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return notesList.size
    }

    class NotesViewHolder(itemView: View, listener: OnItemClickListener) :
        RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.titleTextView)
        val contentText: TextView = itemView.findViewById(R.id.contentTextView)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        recyclerListener = listener
    }

}