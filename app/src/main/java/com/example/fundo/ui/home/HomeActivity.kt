package com.example.fundo.ui.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.fundo.R
import com.example.fundo.databinding.ActivityHomeBinding
import com.example.fundo.ui.authentication.AuthenticationActivity
import com.example.fundo.utils.SharedPrefUtil
import com.example.fundo.utils.Utilities
import com.example.fundo.viewmodels.HomeViewModel
import com.example.fundo.viewmodels.HomeViewModelFactory
import com.google.android.material.button.MaterialButton

class HomeActivity : AppCompatActivity() {
    private lateinit var binding:ActivityHomeBinding
    private lateinit var toggle:ActionBarDrawerToggle
    private lateinit var alertDialog: AlertDialog
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.profileMenu -> showProfile()
        }
        return true
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
        homeViewModel = ViewModelProvider(this,HomeViewModelFactory())[HomeViewModel::class.java]

        createProfileOverlay()
        createNavigationDrawer()
        attachObservers()
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
    }

    private fun createProfileOverlay() {
        val profileView = LayoutInflater.from(this@HomeActivity).inflate(R.layout.user_profile,null)
        alertDialog = AlertDialog.Builder(this)
            .setView(profileView)
            .create()

        val dialogLogoutButton:MaterialButton = profileView.findViewById(R.id.userLogout)
        dialogLogoutButton.setOnClickListener{
            homeViewModel.logout()
        }

        val closeOverlayButton:ImageView = profileView.findViewById(R.id.closeOverlayButton)
        closeOverlayButton.setOnClickListener{
            dismissProfile()
        }

        val userNameText:TextView = profileView.findViewById(R.id.userNameText)
        val userEmailText:TextView = profileView.findViewById(R.id.userEmailText)

        userNameText.text = SharedPrefUtil.getUserName()
        userEmailText.text = SharedPrefUtil.getUserEmail()
    }
}