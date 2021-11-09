package com.example.fundo.ui.home

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.fundo.R
import com.example.fundo.databinding.ActivityHomeBinding
import com.example.fundo.ui.authentication.AuthenticationActivity
import com.example.fundo.ui.note.NoteActivity
import com.example.fundo.utils.NotesRecyclerAdapter
import com.example.fundo.utils.SharedPrefUtil
import com.example.fundo.utils.Utilities
import com.example.fundo.viewmodels.HomeViewModel
import com.example.fundo.viewmodels.HomeViewModelFactory
import com.example.fundo.wrapper.Note
import com.google.android.material.button.MaterialButton

class HomeActivity : AppCompatActivity() {
    private lateinit var binding:ActivityHomeBinding
    private lateinit var toggle:ActionBarDrawerToggle
    private lateinit var alertDialog: AlertDialog
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var dialog:Dialog
    private lateinit var profileOverlayView: View
    private var menu: Menu? = null
    private lateinit var notesAdapter:NotesRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dialog = Dialog(this)
        dialog.setContentView(R.layout.loading_dialog)
        homeViewModel = ViewModelProvider(this,HomeViewModelFactory())[HomeViewModel::class.java]

        homeViewModel.getNotesFromDB()
        createProfileOverlay()
        createNavigationDrawer()
        attachObservers()
        attachListeners()
        initNotesRecyclerView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        this.menu = menu
        menuInflater.inflate(R.menu.toolbar_menu,menu)
        val item = menu?.findItem(R.id.actionSearch)
        val searchView = item?.actionView as SearchView
        
        initActionSearch(searchView)
        return true
    }

    private fun initActionSearch(searchView: SearchView) {
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                tempList.clear()
                val queryText = newText!!.lowercase()
                noteList.forEach {
                    if(it.title.contains(queryText) || it.content.contains(queryText)){
                        tempList.add(it)
                    }
                }
                notesAdapter.notifyDataSetChanged()

                return false
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.profileMenu -> showProfile()
            R.id.layoutChangeButton -> changeLayout(item)
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == PICK_IMAGE_FOR_USERPROFILE_REQUESTCODE && data != null){
            handleUserImagePickData(data.data)
        }

        if(requestCode == ADD_NEW_NOTE_REQUESTCODE && data != null){
            handleNewNoteData(data.extras)
        }

        if(requestCode == UPDATE_NOTE_REQUESTCODE && data != null){
            if(resultCode == NoteActivity.DELETE_NOTE){
                handleDeleteNote(data.extras)
            }
            else{
                handleNoteUpdate(data.extras)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == STORAGE_PERMISSION_REQUESTCODE && grantResults.isNotEmpty()){
            if((grantResults[0] != PackageManager.PERMISSION_GRANTED)){
                Utilities.displayToast(this@HomeActivity,getString(R.string.storage_denied_permission_error))
            }
        }
    }

    private fun initNotesRecyclerView() {
        notesAdapter = NotesRecyclerAdapter(tempList)
        val notesRecyclerView = binding.notesRecyclerView
        notesRecyclerView.layoutManager = StaggeredGridLayoutManager(2,1)
        notesRecyclerView.setHasFixedSize(true)
        notesAdapter.setOnItemClickListener(object : NotesRecyclerAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {
                val note = tempList[position]
                val intent = Intent(this@HomeActivity,NoteActivity::class.java)
                intent.putExtra("id",note.id)
                intent.putExtra("title", note.title)
                intent.putExtra("content",note.content)
                startActivityForResult(intent, UPDATE_NOTE_REQUESTCODE)
            }
        })

        notesRecyclerView.adapter = notesAdapter
    }

    private fun attachListeners() {
        binding.addNewNoteFab.setOnClickListener{
            val intent = Intent(this@HomeActivity,NoteActivity::class.java)
            startActivityForResult(intent, ADD_NEW_NOTE_REQUESTCODE)
        }
    }

    private fun createNavigationDrawer() {
        setSupportActionBar(binding.toolBar)
        toggle = ActionBarDrawerToggle(this,binding.drawerLayout,binding.toolBar,R.string.open,R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.isDrawerIndicatorEnabled = true
        toggle.syncState()

        val headerView = binding.navigationDrawer.getHeaderView(0)
        val userNameTextView: TextView = headerView.findViewById(R.id.userNameText)
        userNameTextView.text = SharedPrefUtil.getUserName()

        binding.navigationDrawer.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.notes -> Utilities.displayToast(this,"Notes Selected")
                R.id.reminders -> Utilities.displayToast(this,"Reminders Selected")
                R.id.settings -> Utilities.displayToast(this,"Settings Selected")
                R.id.about -> Utilities.displayToast(this,"About Selected")
                R.id.logout -> homeViewModel.logout()
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)

            true
        }
    }

    private fun showProfile() {
        alertDialog.show()
    }

    private fun dismissProfile() {
        alertDialog.dismiss()
    }

    private fun attachObservers() {
        homeViewModel.goToAuthenticationActivity.observe(this){
            var intent = Intent(this@HomeActivity,AuthenticationActivity::class.java)
            dismissProfile()
            finish()
            startActivity(intent)
        }

        homeViewModel.logoutStatus.observe(this){
            homeViewModel.setGoToAuthenticationActivity(true)
        }

        homeViewModel.setUserAvatarStatus.observe(this){
            val userProfileIcon:ImageButton = profileOverlayView.findViewById(R.id.userAvatarButton)
            userProfileIcon.setImageBitmap(it)

            val headerView = binding.navigationDrawer.getHeaderView(0)
            val navUserAvatar:ImageView = headerView.findViewById(R.id.userAvatar)
            navUserAvatar.setImageBitmap(it)

            val item = menu?.findItem(R.id.profileMenu)
            item?.icon = BitmapDrawable(it)

            dialog.dismiss()
        }

        homeViewModel.getNotesFromDB.observe(this){
            noteList.clear()
            noteList.addAll(it)
            syncLists()
            notesAdapter.notifyDataSetChanged()
        }

        homeViewModel.addNoteToDB.observe(this@HomeActivity){
            noteList.add(it)
            syncLists()
            notesAdapter.notifyDataSetChanged()
            Log.d("RES","id = ${it.id}")
        }

        homeViewModel.updateNoteInDB.observe(this@HomeActivity){
            noteList.map { note ->
                if(note.id == it.id){
                    note.title = it.title
                    note.content = it.content
                }
            }
            syncLists()
            notesAdapter.notifyDataSetChanged()
        }

        homeViewModel.deleteNoteFromDB.observe(this@HomeActivity){
            noteList.removeIf { note -> note.id == it.id }
            syncLists()
            notesAdapter.notifyDataSetChanged()
        }
    }

    private fun changeLayout(item:MenuItem) {
        if(layoutFlag){
            item.setIcon(R.drawable.linear_layout_icon)
            binding.notesRecyclerView.layoutManager = LinearLayoutManager(this@HomeActivity)
        }
        else{
            item.setIcon(R.drawable.grid_layout_icon)
            binding.notesRecyclerView.layoutManager = StaggeredGridLayoutManager(2,1)
        }
        layoutFlag = !layoutFlag
    }

    private fun createProfileOverlay() {
        profileOverlayView = LayoutInflater.from(this@HomeActivity).inflate(R.layout.user_profile,null)
        alertDialog = AlertDialog.Builder(this)
            .setView(profileOverlayView)
            .create()

        homeViewModel.getUserAvatar()
        val dialogLogoutButton:MaterialButton = profileOverlayView.findViewById(R.id.userLogout)
        dialogLogoutButton.setOnClickListener{
            homeViewModel.logout()
        }

        val closeOverlayButton:ImageView = profileOverlayView.findViewById(R.id.closeOverlayButton)
        closeOverlayButton.setOnClickListener{
            dismissProfile()
        }

        val userNameText:TextView = profileOverlayView.findViewById(R.id.userNameText)
        val userEmailText:TextView = profileOverlayView.findViewById(R.id.userEmailText)

        userNameText.text = SharedPrefUtil.getUserName()
        userEmailText.text = SharedPrefUtil.getUserEmail()

        val userProfileIcon:ImageButton = profileOverlayView.findViewById(R.id.userAvatarButton)
        userProfileIcon.setOnClickListener{
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ){
                pickImageFromGallery()
            }
            else{
                Log.d("Permission","Permission not granted")
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    STORAGE_PERMISSION_REQUESTCODE)
            }
        }
    }

    private fun pickImageFromGallery(){
        val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_FOR_USERPROFILE_REQUESTCODE)
    }

    private fun handleUserImagePickData(imageUri: Uri?) {
        dialog.show()
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,imageUri)
        homeViewModel.setUserAvatar(bitmap)
    }

    private fun handleNewNoteData(noteBundle: Bundle?) {
        val title = noteBundle?.getString("title").toString()
        val content = noteBundle?.getString("content").toString()
        if(title.isNotEmpty() || content.isNotEmpty()){
            val newNote = Note(title,content,"")
            homeViewModel.addNoteToDB(newNote)
        }
        else{
            Utilities.displayToast(this@HomeActivity,getString(R.string.empty_note_discarded))
        }
    }

    private fun handleNoteUpdate(noteBundle: Bundle?) {
        val id = noteBundle?.getString("id").toString()
        val title = noteBundle?.getString("title").toString()
        val content = noteBundle?.getString("content").toString()

        if(title.isNotEmpty() || content.isNotEmpty()){
            val updateNote = noteList.find { it.id == id}!!
            val note = Note(title,content,updateNote.id)
            homeViewModel.updateNoteInDB(note)
        }
        else{
            handleDeleteNote(noteBundle)
        }
    }

    private fun handleDeleteNote(noteBundle: Bundle?) {
        val id = noteBundle?.getString("id").toString()
        val deleteNote = noteList.find { it.id == id}!!
        homeViewModel.deleteNoteFromDB(deleteNote)
    }

    private fun syncLists(){
        tempList.clear()
        tempList.addAll(noteList)
    }

    companion object{
        private val STORAGE_PERMISSION_REQUESTCODE = 0
        private val PICK_IMAGE_FOR_USERPROFILE_REQUESTCODE = 1
        private val ADD_NEW_NOTE_REQUESTCODE = 2
        private val UPDATE_NOTE_REQUESTCODE = 3
        private val noteList = mutableListOf<Note>()
        private val tempList = mutableListOf<Note>()
        private var layoutFlag = true
    }
}