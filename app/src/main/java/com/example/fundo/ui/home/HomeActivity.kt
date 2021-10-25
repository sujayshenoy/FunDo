package com.example.fundo.ui.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fundo.databinding.ActivityHomeBinding
import com.example.fundo.services.Auth
import com.example.fundo.ui.authentication.AuthenticationActivity

class HomeActivity : AppCompatActivity() {
    private lateinit var binding:ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.logOutButton.setOnClickListener{
            Auth.signOut()
            var intent = Intent(this@HomeActivity,AuthenticationActivity::class.java)
            finish()
            startActivity(intent)
        }
    }
}