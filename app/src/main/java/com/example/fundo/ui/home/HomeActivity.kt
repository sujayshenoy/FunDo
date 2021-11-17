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
import androidx.core.view.get
import androidx.lifecycle.ViewModelProvider
import com.example.fundo.R
import com.example.fundo.common.Logger
import com.example.fundo.databinding.ActivityHomeBinding
import com.example.fundo.ui.authentication.AuthenticationActivity
import com.example.fundo.common.Utilities
import com.example.fundo.config.Constants.PICK_IMAGE_FOR_USERPROFILE_REQUEST_CODE
import com.example.fundo.config.Constants.STORAGE_PERMISSION_REQUEST_CODE
import com.example.fundo.data.wrappers.User
import com.example.fundo.ui.home.noteslist.NotesListFragment
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.*

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var alertDialog: AlertDialog
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var dialog: Dialog
    private lateinit var profileOverlayView: View
    private var menu: Menu? = null
    private var currentUser: User = User(name = "Name", email = "email", phone = "phone")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dialog = Dialog(this@HomeActivity)
        dialog.setContentView(R.layout.dialog_loading)
        homeViewModel = ViewModelProvider(this@HomeActivity)[HomeViewModel::class.java]

        homeViewModel.getUserFromDB(this@HomeActivity)
        createNavigationDrawer()
        initNavigator()
        homeViewModel.goToNotesListFragment()
        createProfileOverlay()
        attachObservers()
        setUserData()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        this.menu = menu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.profileMenu -> showProfile()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_FOR_USERPROFILE_REQUEST_CODE && data != null) {
            handleUserImagePickData(data.data)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty()) {
            if ((grantResults[0] != PackageManager.PERMISSION_GRANTED)) {
                Utilities.displayToast(
                    this@HomeActivity,
                    getString(R.string.storage_denied_permission_error)
                )
            } else {
                Logger.logStorageInfo("Storage Permission: Permission granted")
            }
        }
    }

    private fun initNavigator() {
        homeViewModel.goToNotesList.observe(this@HomeActivity) {
            Utilities.fragmentSwitcher(
                supportFragmentManager,
                R.id.homeActivityFragment,
                NotesListFragment()
            )
        }
    }

    private fun createNavigationDrawer() {
        setSupportActionBar(binding.toolBar)
        toggle = ActionBarDrawerToggle(
            this@HomeActivity,
            binding.drawerLayout,
            binding.toolBar,
            R.string.open,
            R.string.close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.isDrawerIndicatorEnabled = true
        toggle.syncState()

        val notesItemMenu = binding.navigationDrawer.menu[0]
        notesItemMenu.isChecked = true

        binding.navigationDrawer.setNavigationItemSelectedListener {
            notesItemMenu.isChecked = false
            when (it.itemId) {
                R.id.notes -> homeViewModel.goToNotesListFragment()
                R.id.reminders -> Utilities.displayToast(this@HomeActivity, "Reminders Selected")
                R.id.settings -> Utilities.displayToast(this@HomeActivity, "Settings Selected")
                R.id.about -> Utilities.displayToast(this@HomeActivity, "About Selected")
                R.id.logout -> homeViewModel.logout(this@HomeActivity)
            }

            it.isCheckable = true
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
        homeViewModel.goToAuthenticationActivity.observe(this@HomeActivity) {
            var intent = Intent(this@HomeActivity, AuthenticationActivity::class.java)
            dismissProfile()
            finish()
            startActivity(intent)
        }

        homeViewModel.getUserFromDB.observe(this@HomeActivity) {
            currentUser = it
            setUserData()
        }

        homeViewModel.logoutStatus.observe(this@HomeActivity) {
            homeViewModel.goToAuthenticationActivity(true)
        }

        homeViewModel.setUserAvatarStatus.observe(this@HomeActivity) {
            val userProfileIcon: ImageButton =
                profileOverlayView.findViewById(R.id.userAvatarButton)
            userProfileIcon.setImageBitmap(it)

            val headerView = binding.navigationDrawer.getHeaderView(0)
            val navUserAvatar: ImageView = headerView.findViewById(R.id.userAvatar)
            navUserAvatar.setImageBitmap(it)

            val item = menu?.findItem(R.id.profileMenu)
            item?.icon = BitmapDrawable(it)

            dialog.dismiss()
        }
    }

    private fun createProfileOverlay() {
        profileOverlayView =
            LayoutInflater.from(this@HomeActivity).inflate(R.layout.dialog_userprofile, null)
        alertDialog = AlertDialog.Builder(this@HomeActivity)
            .setView(profileOverlayView)
            .create()

        homeViewModel.getUserAvatar()
        val dialogLogoutButton: MaterialButton = profileOverlayView.findViewById(R.id.userLogout)
        dialogLogoutButton.setOnClickListener {
            homeViewModel.logout(this@HomeActivity)
        }

        val closeOverlayButton: ImageView = profileOverlayView.findViewById(R.id.closeOverlayButton)
        closeOverlayButton.setOnClickListener {
            dismissProfile()
        }

        val userProfileIcon: ImageButton = profileOverlayView.findViewById(R.id.userAvatarButton)
        userProfileIcon.setOnClickListener {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                pickImageFromGallery()
            } else {
                Logger.logStorageError("Storage Permission: Permission not granted")
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    STORAGE_PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    private fun setUserData() {
        val modalUserNameText: TextView = profileOverlayView.findViewById(R.id.userNameText)
        val modalUserEmailText: TextView = profileOverlayView.findViewById(R.id.userEmailText)
        val headerView = binding.navigationDrawer.getHeaderView(0)
        val navUserNameText: TextView = headerView.findViewById(R.id.userNameText)

        modalUserEmailText.text = currentUser.email
        modalUserNameText.text = currentUser.name
        navUserNameText.text = currentUser.name
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_FOR_USERPROFILE_REQUEST_CODE)
    }

    private fun handleUserImagePickData(imageUri: Uri?) {
        dialog.show()
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
        homeViewModel.setUserAvatar(bitmap)
    }
}