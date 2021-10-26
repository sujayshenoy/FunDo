package com.example.fundo.ui.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import com.example.fundo.R
import com.example.fundo.databinding.ActivityHomeBinding
import com.example.fundo.services.Auth
import com.example.fundo.ui.authentication.AuthenticationActivity
import com.example.fundo.utils.Utilities
import com.google.android.material.navigation.NavigationView

class HomeActivity : AppCompatActivity() {
    private lateinit var binding:ActivityHomeBinding
    private lateinit var toggle:ActionBarDrawerToggle

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }

        Utilities.displayToast(this,"Item id selected ${item.itemId}")

        when(item.itemId){
            R.id.profileMenu -> Utilities.displayToast(this,"Profile Icon clicked")
        }
        return true
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

        binding.logOutButton.setOnClickListener{
            Auth.signOut()
            var intent = Intent(this@HomeActivity,AuthenticationActivity::class.java)
            finish()
            startActivity(intent)
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
}