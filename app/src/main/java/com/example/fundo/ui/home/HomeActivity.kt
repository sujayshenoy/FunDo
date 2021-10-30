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
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.fundo.R
import com.example.fundo.databinding.ActivityHomeBinding
import com.example.fundo.ui.authentication.AuthenticationActivity
import com.example.fundo.ui.newnote.NewNoteActivity
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        this.menu = menu
        menuInflater.inflate(R.menu.toolbar_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.profileMenu -> showProfile()
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == PICK_IMAGE_FOR_USERPROFILE_REQUESTCODE && data!=null){
            handleUserImagePickData(data.data)
        }

        if(requestCode == ADD_NEW_NOTE_REQUESTCODE && data!=null){
            handleNewNoteData(data.extras)
        }
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
            val newNote = Note(title.toString(),content.toString())
            noteList.add(newNote)
        }
        else{
            Utilities.displayToast(this@HomeActivity,"Empty Note Discarded")
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
                Utilities.displayToast(this@HomeActivity,"Storage Access required to upload picture")
            }
        }
    }

    private fun showProfile() {
        alertDialog.show()
    }

    private fun dismissProfile() {
        alertDialog.dismiss()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dialog = Dialog(this)
        dialog.setContentView(R.layout.loading_dialog)
        homeViewModel = ViewModelProvider(this,HomeViewModelFactory())[HomeViewModel::class.java]

        createProfileOverlay()
        createNavigationDrawer()
        attachObservers()
        attachListeners()
    }

    private fun attachListeners() {
        binding.addNewNoteFab.setOnClickListener{
            val intent = Intent(this@HomeActivity,NewNoteActivity::class.java)
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

    fun pickImageFromGallery(){
        val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_FOR_USERPROFILE_REQUESTCODE)
    }

    companion object{
        private val STORAGE_PERMISSION_REQUESTCODE = 0
        private val PICK_IMAGE_FOR_USERPROFILE_REQUESTCODE = 1
        private val ADD_NEW_NOTE_REQUESTCODE = 2
        private val noteList = mutableListOf<Note>()
    }
}