package com.example.fundo.ui.home.noteslist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fundo.R
import com.example.fundo.data.wrappers.Note
import java.text.SimpleDateFormat

class NotesRecyclerAdapter(private val notesList: ArrayList<Note>) :
    RecyclerView.Adapter<NotesRecyclerAdapter.NotesViewHolder>(), Filterable {
    private lateinit var recyclerListener: OnItemClickListener
    private var tempList = notesList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_recyclernotes, parent, false)
        return NotesViewHolder(itemView, recyclerListener)
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        val currentNote = tempList[position]
        holder.titleText.text = currentNote.title
        holder.contentText.text = currentNote.content

        if (currentNote.reminder != null) {
            val formatter = SimpleDateFormat("dd MMM, hh:mm aa")
            val date = formatter.format(currentNote.reminder)
            holder.reminderLayout.visibility = View.VISIBLE
            holder.reminderText.text = date
        } else {
            holder.reminderLayout.visibility = View.GONE
        }

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