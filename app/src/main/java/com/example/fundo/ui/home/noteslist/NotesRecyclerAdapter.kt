package com.example.fundo.ui.home.noteslist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.fundo.R
import com.example.fundo.common.Logger
import com.example.fundo.data.wrappers.Note
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class NotesRecyclerAdapter(private val notesList: ArrayList<Note>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {
    private lateinit var recyclerListener: OnItemClickListener
    private var tempList = notesList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.item_recyclernotes, parent, false)
        return NotesViewHolder(itemView, recyclerListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val notesViewHolder = holder as NotesViewHolder
        val currentNote = tempList[position]
        notesViewHolder.titleText.text = currentNote.title
        notesViewHolder.contentText.text = currentNote.content

        if (currentNote.reminder != null) {
            val formatter = SimpleDateFormat("dd MMM, hh:mm aa", Locale.getDefault())
            val date = formatter.format(currentNote.reminder)
            notesViewHolder.reminderLayout.visibility = View.VISIBLE
            notesViewHolder.reminderText.text = date
        } else {
            notesViewHolder.reminderLayout.visibility = View.GONE
        }

        if (currentNote.title.isEmpty()) {
            notesViewHolder.titleText.visibility = View.GONE
        } else {
            notesViewHolder.titleText.visibility = View.VISIBLE
        }
        if (currentNote.content.isEmpty()) {
            notesViewHolder.contentText.visibility = View.GONE
        } else {
            notesViewHolder.contentText.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return tempList.size
    }

    class NotesViewHolder(itemView: View, listener: OnItemClickListener) :
        RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.titleTextView)
        val contentText: TextView = itemView.findViewById(R.id.contentTextView)
        val reminderText: TextView = itemView.findViewById(R.id.reminderTextView)
        val reminderLayout: RelativeLayout = itemView.findViewById(R.id.reminderLayout)

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

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults {
                tempList = if (p0?.isNullOrEmpty() == true) {
                    notesList
                } else {
                    val filteredList = arrayListOf<Note>()
                    for (i in tempList) {
                        if (i.title.contains(p0, true) || i.content.contains(p0, true)) {
                            filteredList.add(i)
                        }
                    }
                    filteredList
                }

                val filteredResult = FilterResults()
                filteredResult.values = tempList
                return filteredResult
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                tempList = p1?.values as ArrayList<Note>
                notifyDataSetChanged()
            }
        }
    }
}