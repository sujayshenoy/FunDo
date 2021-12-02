package com.example.fundo.ui.home.noteslist

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.work.*
import com.example.fundo.R
import com.example.fundo.common.NotifyWorker
import com.example.fundo.common.NotifyWorker.Companion.CHANNEL_ID
import com.example.fundo.common.Utilities
import com.example.fundo.config.Constants
import com.example.fundo.data.room.DateTypeConverters
import com.example.fundo.data.wrappers.Note
import com.example.fundo.data.wrappers.User
import com.example.fundo.databinding.FragmentNoteListBinding
import com.example.fundo.ui.home.HomeViewModel
import com.example.fundo.ui.note.NoteActivity
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class NotesListFragment() :
    Fragment(R.layout.fragment_note_list) {
    private lateinit var binding: FragmentNoteListBinding
    private lateinit var notesListViewModel: NotesListViewModel
    private lateinit var homeViewModel: HomeViewModel
    private val noteList = mutableListOf<Note>()
    private var layoutFlag = true
    private lateinit var notesAdapter: NotesRecyclerAdapter
    private lateinit var currentUser: User
    private var archive: Boolean = false
    private var reminder: Boolean = false
    private lateinit var dialog: Dialog
    private var totalNotes: Int = 0
    private var isLoading = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_loading)
        dialog.show()
        binding = FragmentNoteListBinding.bind(view)
        notesListViewModel = ViewModelProvider(requireActivity())[NotesListViewModel::class.java]
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]

        homeViewModel.getUserFromDB(requireContext())
        homeViewModel.getUserFromDB.observe(viewLifecycleOwner) {
            currentUser = it
            initVars()
        }

        createNotificationChannel()
    }

    private fun scheduleNotification(note: Note,cancel: Boolean = false) {
        val inputData = Data.Builder()
            .putString("title",note.title)
            .putString("content",note.content)
            .putLong("id",note.id)
            .putBoolean("archived", note.archived)
            .putString("reminder", DateTypeConverters().fromDateTime(note.reminder))
            .build()

        if(!cancel) {
            val notificationWork = OneTimeWorkRequestBuilder<NotifyWorker>()
                .setInitialDelay(note.reminder!!.time - System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .addTag(note.title)
                .build()
            WorkManager.getInstance(requireContext()).enqueueUniqueWork(note.firebaseId,ExistingWorkPolicy.REPLACE,notificationWork)
            Utilities.displayToast(requireContext(), "Notification set at ${note.reminder}")
        }
        else {
            WorkManager.getInstance(requireContext()).cancelUniqueWork(note.firebaseId)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val name = "Fundo Notify Channel"
        val desc = "Fundo Notification"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance)
        val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun initVars() {
        when (arguments?.getString("type").toString()) {
            "archive" -> {
                notesListViewModel.getArchivedNotes(requireContext(), currentUser)
                archive = true
                binding.addNewNoteFab.visibility = View.GONE
            }
            "reminder" -> {
                notesListViewModel.getReminderNotes(requireContext(), currentUser)
                reminder = true
                binding.addNewNoteFab.visibility = View.GONE
            }
            else -> notesListViewModel.getNotesFromDB(requireContext(), currentUser)
        }

        initNotesRecyclerView()
        fetchNotes()
    }

    private fun fetchNotes() {
        notesListViewModel.getNotesCount(requireContext())
        notesListViewModel.getNotesFromDB.observe(viewLifecycleOwner) {
            noteList.clear()
            noteList.addAll(it)
            notesListViewModel.getNotesCount(requireContext())
            notesAdapter.notifyDataSetChanged()
        }

        notesListViewModel.getNotesCount.observe(viewLifecycleOwner) {
            totalNotes = it
        }

        notesListViewModel.getArchivedNotesFromDB.observe(viewLifecycleOwner) {
            noteList.clear()
            noteList.addAll(it)
            notesAdapter.notifyDataSetChanged()
        }

        notesListViewModel.getReminderNotesFromDB.observe(viewLifecycleOwner) {
            noteList.clear()
            noteList.addAll(it)
            notesAdapter.notifyDataSetChanged()
        }

        attachListeners()
        attachObservers()
        dialog.dismiss()
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
        notesListViewModel.addNoteToDB.observe(viewLifecycleOwner) {
            notesListViewModel.getNotesCount(requireContext())
            when {
                archive -> {
                    notesListViewModel.getArchivedNotes(requireContext(), currentUser)
                }
                reminder -> {
                    notesListViewModel.getReminderNotes(requireContext(), currentUser)
                }
                else -> {
                    notesListViewModel.getNotesFromDB(requireContext(), currentUser)
                }
            }
        }

        notesListViewModel.getNotesPaged.observe(viewLifecycleOwner) {
            isLoading = false
            noteList.addAll(it)
            notesAdapter.notifyDataSetChanged()
            resetListLoader()
        }

        notesListViewModel.getArchivesPaged.observe(viewLifecycleOwner) {
            isLoading = false
            noteList.addAll(it)
            notesAdapter.notifyDataSetChanged()
            resetListLoader()
        }

        notesListViewModel.getRemindersPaged.observe(viewLifecycleOwner) {
            isLoading = false
            noteList.addAll(it)
            notesAdapter.notifyDataSetChanged()
            resetListLoader()
        }

        notesListViewModel.updateNoteInDB.observe(viewLifecycleOwner) {
            when {
                archive -> {
                    notesListViewModel.getArchivedNotes(requireContext(), currentUser)
                }
                reminder -> {
                    notesListViewModel.getReminderNotes(requireContext(), currentUser)
                }
                else -> {
                    notesListViewModel.getNotesFromDB(requireContext(), currentUser)
                }
            }
        }

        notesListViewModel.deleteNoteFromDB.observe(viewLifecycleOwner) {
            when {
                archive -> {
                    notesListViewModel.getArchivedNotes(requireContext(), currentUser)
                }
                reminder -> {
                    notesListViewModel.getReminderNotes(requireContext(), currentUser)
                }
                else -> {
                    notesListViewModel.getNotesFromDB(requireContext(), currentUser)
                }
            }
        }

        notesListViewModel.syncDBStatus.observe(viewLifecycleOwner) {
            notesListViewModel.getNotesFromDB(requireContext(), currentUser)
            binding.swipeRefreshView.isRefreshing = false
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

    private fun initNotesRecyclerView() {
        notesAdapter = NotesRecyclerAdapter(noteList as ArrayList<Note>)
        val notesRecyclerView = binding.notesRecyclerView

        notesRecyclerView.layoutManager = StaggeredGridLayoutManager(2, 1)
        notesRecyclerView.setHasFixedSize(true)
        notesAdapter.setOnItemClickListener(object : NotesRecyclerAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val note = noteList[position]
                val intent = Intent(requireContext(), NoteActivity::class.java)
                intent.putExtra("id", note.id)
                intent.putExtra("title", note.title)
                intent.putExtra("content", note.content)
                intent.putExtra("archived", note.archived)
                intent.putExtra("reminder", note.reminder)
                intent.putExtra("firebaseID", note.firebaseId)
                intent.putExtra("currentUser", currentUser)
                startActivityForResult(intent, Constants.UPDATE_NOTE_REQUEST_CODE)
            }
        })

        notesRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (notesRecyclerView.layoutManager is LinearLayoutManager) {
                    val layoutManager = notesRecyclerView.layoutManager as LinearLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()

                    if (((visibleItemCount + firstVisibleItem) >= totalItemCount) && (totalItemCount < totalNotes)) {
                        isLoading = true
                        when {
                            archive -> {
                                notesListViewModel.getArchivesPaged(
                                    requireContext(),
                                    10,
                                    totalItemCount
                                )
                            }
                            reminder -> {
                                notesListViewModel.getRemindersPaged(
                                    requireContext(),
                                    10,
                                    totalItemCount
                                )
                            }
                            else -> {
                                notesListViewModel.getNotesPaged(
                                    requireContext(),
                                    10,
                                    totalItemCount
                                )
                            }
                        }
                    }
                } else {
                    val layoutManager =
                        notesRecyclerView.layoutManager as StaggeredGridLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItem = layoutManager.findFirstVisibleItemPositions(null)

                    if (((visibleItemCount + firstVisibleItem[0]) >= totalItemCount) && (totalItemCount < totalNotes)) {
                        isLoading = true
                        when {
                            archive -> {
                                notesListViewModel.getArchivesPaged(
                                    requireContext(),
                                    10,
                                    totalItemCount
                                )
                            }
                            reminder -> {
                                notesListViewModel.getRemindersPaged(
                                    requireContext(),
                                    10,
                                    totalItemCount
                                )
                            }
                            else -> {
                                notesListViewModel.getNotesPaged(
                                    requireContext(),
                                    10,
                                    totalItemCount
                                )
                            }
                        }
                    }
                }
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
                notesAdapter.filter.filter(newText)
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
        val archived = noteBundle?.getBoolean("archived") ?: false
        val reminder = noteBundle?.getSerializable("reminder") as? Date

        if (title.isNotEmpty() || content.isNotEmpty()) {
            val updateNote = noteList.find { it.id == id }!!
            val note =
                Note(
                    title,
                    content,
                    updateNote.id,
                    updateNote.firebaseId,
                    archived = archived,
                    reminder = reminder
                )
            notesListViewModel.updateNoteInDB(requireContext(), note, currentUser)

            if(reminder != null) {
                scheduleNotification(note)
            } else {
                scheduleNotification(note,true)
            }
        } else {
            handleDeleteNote(noteBundle)
        }
    }

    private fun handleDeleteNote(noteBundle: Bundle?) {
        val id = noteBundle?.getLong("id")
        val deleteNote = noteList.find { it.id == id }!!
        notesListViewModel.deleteNoteFromDB(requireContext(), deleteNote, currentUser)
    }

    private fun resetListLoader() {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}