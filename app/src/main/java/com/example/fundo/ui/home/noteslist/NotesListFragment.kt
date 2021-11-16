package com.example.fundo.ui.home.noteslist

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.fundo.R
import com.example.fundo.common.Utilities
import com.example.fundo.config.Constants
import com.example.fundo.data.wrappers.Note
import com.example.fundo.data.wrappers.User
import com.example.fundo.databinding.FragmentNoteListBinding
import com.example.fundo.ui.home.HomeViewModel
import com.example.fundo.ui.note.NoteActivity

class NotesListFragment : Fragment(R.layout.fragment_note_list) {
    private lateinit var binding: FragmentNoteListBinding
    private lateinit var notesListViewModel: NotesListViewModel
    private lateinit var homeViewModel: HomeViewModel
    private var currentUser: User = User(name = "Name", email = "email", phone = "phone")
    private val noteList = mutableListOf<Note>()
    private val tempList = mutableListOf<Note>()
    private var layoutFlag = true
    private lateinit var notesAdapter: NotesRecyclerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        binding = FragmentNoteListBinding.bind(view)
        notesListViewModel = ViewModelProvider(requireActivity())[NotesListViewModel::class.java]
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]

        homeViewModel.getUserFromDB(requireContext())
        attachListeners()
        attachObservers()
        initNotesRecyclerView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_noteslist, menu)
        val item = menu.findItem(R.id.actionSearch)
        val searchView = item?.actionView as SearchView

        initActionSearch(searchView)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.layoutChangeButton -> changeLayout(item)
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.ADD_NEW_NOTE_REQUEST_CODE && data != null) {
            handleNewNoteData(data.extras)
        }

        if (requestCode == Constants.UPDATE_NOTE_REQUEST_CODE && data != null) {
            if (resultCode == NoteActivity.DELETE_NOTE) {
                handleDeleteNote(data.extras)
            } else {
                handleNoteUpdate(data.extras)
            }
        }
    }

    private fun attachObservers() {
        notesListViewModel.getNotesFromDB.observe(viewLifecycleOwner) {
            noteList.clear()
            noteList.addAll(it)
            syncLists()
            notesAdapter.notifyDataSetChanged()
        }

        notesListViewModel.addNoteToDB.observe(viewLifecycleOwner) {
            noteList.add(it)
            syncLists()
            notesAdapter.notifyItemInserted(noteList.size)
        }

        notesListViewModel.updateNoteInDB.observe(viewLifecycleOwner) {
            var pos: Int = -1
            noteList.map { note ->
                if (note.id == it.id) {
                    note.title = it.title
                    note.content = it.content
                    pos = noteList.indexOf(note)
                }
            }
            syncLists()
            notesAdapter.notifyItemChanged(pos)
        }

        notesListViewModel.deleteNoteFromDB.observe(viewLifecycleOwner) {
            val notePos = noteList.indexOf(it)
            noteList.remove(it)
            syncLists()
            notesAdapter.notifyItemRemoved(notePos)
        }

        notesListViewModel.syncDBStatus.observe(viewLifecycleOwner) {
            notesListViewModel.getNotesFromDB(requireContext(), currentUser)
            binding.swipeRefreshView.isRefreshing = false
        }

        homeViewModel.getUserFromDB.observe(viewLifecycleOwner) {
            currentUser = it
            notesListViewModel.getNotesFromDB(requireContext(), currentUser)
        }
    }

    private fun attachListeners() {
        binding.addNewNoteFab.setOnClickListener {
            val intent = Intent(requireContext(), NoteActivity::class.java)
            startActivityForResult(intent, Constants.ADD_NEW_NOTE_REQUEST_CODE)
        }

        binding.swipeRefreshView.setOnRefreshListener {
            notesListViewModel.syncDB(requireContext(), currentUser)
        }
    }

    private fun syncLists() {
        tempList.clear()
        tempList.addAll(noteList)
    }

    private fun initNotesRecyclerView() {
        notesAdapter = NotesRecyclerAdapter(tempList)
        val notesRecyclerView = binding.notesRecyclerView
        notesRecyclerView.layoutManager = StaggeredGridLayoutManager(2, 1)
        notesRecyclerView.setHasFixedSize(true)
        notesAdapter.setOnItemClickListener(object : NotesRecyclerAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val note = tempList[position]
                val intent = Intent(requireContext(), NoteActivity::class.java)
                intent.putExtra("id", note.id)
                intent.putExtra("title", note.title)
                intent.putExtra("content", note.content)
                startActivityForResult(intent, Constants.UPDATE_NOTE_REQUEST_CODE)
            }
        })

        notesRecyclerView.adapter = notesAdapter
    }

    private fun initActionSearch(searchView: SearchView) {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                tempList.clear()
                val queryText = newText!!.lowercase()
                noteList.forEach {
                    if (it.title.contains(queryText) || it.content.contains(queryText)) {
                        tempList.add(it)
                    }
                }
                notesAdapter.notifyDataSetChanged()

                return false
            }
        })
    }

    private fun changeLayout(item: MenuItem) {
        if (layoutFlag) {
            item.setIcon(R.drawable.button_linear_switch)
            binding.notesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        } else {
            item.setIcon(R.drawable.button_grid_switch)
            binding.notesRecyclerView.layoutManager = StaggeredGridLayoutManager(2, 1)
        }
        layoutFlag = !layoutFlag
    }

    private fun handleNewNoteData(noteBundle: Bundle?) {
        val title = noteBundle?.getString("title").toString()
        val content = noteBundle?.getString("content").toString()
        if (title.isNotEmpty() || content.isNotEmpty()) {
            val newNote = Note(title, content)
            notesListViewModel.addNoteToDB(requireContext(), newNote, currentUser)
        } else {
            Utilities.displayToast(requireContext(), getString(R.string.empty_note_discarded))
        }
    }

    private fun handleNoteUpdate(noteBundle: Bundle?) {
        val id = noteBundle?.getLong("id")
        val title = noteBundle?.getString("title").toString()
        val content = noteBundle?.getString("content").toString()

        if (title.isNotEmpty() || content.isNotEmpty()) {
            val updateNote = noteList.find { it.id == id }!!
            val note = Note(title, content, updateNote.id, updateNote.firebaseId)
            notesListViewModel.updateNoteInDB(requireContext(), note, currentUser)
        } else {
            handleDeleteNote(noteBundle)
        }
    }

    private fun handleDeleteNote(noteBundle: Bundle?) {
        val id = noteBundle?.getLong("id")
        val deleteNote = noteList.find { it.id == id }!!
        notesListViewModel.deleteNoteFromDB(requireContext(), deleteNote, currentUser)
    }
}