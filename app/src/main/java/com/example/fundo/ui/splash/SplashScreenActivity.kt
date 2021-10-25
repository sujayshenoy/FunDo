package com.example.fundo.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fundo.databinding.SplashScreenBinding
import com.example.fundo.services.Auth
import com.example.fundo.services.Database
import com.example.fundo.ui.authentication.Authentication
import com.example.fundo.ui.home.HomeActivity
import com.example.fundo.utils.Utilities

class SplashScreenActivity: AppCompatActivity() {
    private lateinit var binding: SplashScreenBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = SplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val splashScreenIcon = binding.splashScreenIcon
        splashScreenIcon.alpha = 0f
        splashScreenIcon.animate().setDuration(1500).alpha(1f).withEndAction{
            if(Auth.getCurrentUser() == null){
                val intent = Intent(this, Authentication::class.java)
                finish()
                startActivity(intent)
            }
            else{
                Database.getUserFromDB {
                    val intent = Intent(this, HomeActivity::class.java)
                    finish()
                    startActivity(intent)
                }
            }

        }
    }
}