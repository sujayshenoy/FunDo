package com.example.fundo.ui.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.example.fundo.R
import com.example.fundo.databinding.ActivityHomeBinding
import com.example.fundo.services.Auth
import com.example.fundo.ui.authentication.AuthenticationActivity
import com.example.fundo.utils.Utilities

class HomeActivity : AppCompatActivity() {
    private lateinit var binding:ActivityHomeBinding

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
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

        binding.logOutButton.setOnClickListener{
            Auth.signOut()
            var intent = Intent(this@HomeActivity,AuthenticationActivity::class.java)
            finish()
            startActivity(intent)
        }

        binding.toolBar.setNavigationOnClickListener {
            Utilities.displayToast(this,"Navigation drawer clicked")
        }
    }
}