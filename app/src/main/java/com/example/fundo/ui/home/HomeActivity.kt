package com.example.fundo.ui.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import com.example.fundo.R
import com.example.fundo.databinding.ActivityHomeBinding
import com.example.fundo.services.Auth
import com.example.fundo.ui.authentication.AuthenticationActivity
import com.example.fundo.utils.Utilities
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView

class HomeActivity : AppCompatActivity() {
    private lateinit var binding:ActivityHomeBinding
    private lateinit var toggle:ActionBarDrawerToggle
    private lateinit var alertDialog: AlertDialog

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }

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

        setSupportActionBar(binding.toolBar)
        toggle = ActionBarDrawerToggle(this,binding.drawerLayout,binding.toolBar,R.string.open,R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.isDrawerIndicatorEnabled = true
        toggle.syncState()

        createProfileOverlay()

        binding.logOutButton.setOnClickListener{
            logout()
        }

        binding.navigationDrawer.setNavigationItemSelectedListener(object:NavigationView.OnNavigationItemSelectedListener{
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                TODO("Not yet implemented")
            }

        })

        binding.navigationDrawer.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.notes -> Utilities.displayToast(this,"Notes Selected")
                R.id.reminders -> Utilities.displayToast(this,"Reminders Selected")
                R.id.settings -> Utilities.displayToast(this,"Settings Selected")
                R.id.about -> Utilities.displayToast(this,"About Selected")
                R.id.logout -> Utilities.displayToast(this,"Logout Selected")
            }

            true
        }
    }

    private fun createProfileOverlay() {
        val profileView = LayoutInflater.from(this@HomeActivity).inflate(R.layout.user_profile,null)
        alertDialog = AlertDialog.Builder(this)
            .setView(profileView)
            .create()

        val dialogLogoutButton:MaterialButton = profileView.findViewById(R.id.userLogout)
        dialogLogoutButton.setOnClickListener{
            logout()
        }

        val closeOverlayButton:ImageView = profileView.findViewById(R.id.closeOverlayButton)
        closeOverlayButton.setOnClickListener{
            dismissProfile()
        }
    }

    private fun logout() {
        Auth.signOut()
        var intent = Intent(this@HomeActivity,AuthenticationActivity::class.java)
        finish()
        startActivity(intent)
    }
}