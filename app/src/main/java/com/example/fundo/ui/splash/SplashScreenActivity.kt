package com.example.fundo.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fundo.databinding.SplashScreenBinding
import com.example.fundo.services.AuthService
import com.example.fundo.services.DatabaseService
import com.example.fundo.ui.authentication.AuthenticationActivity
import com.example.fundo.viewmodels.AuthenticationSharedViewModel
import com.example.fundo.ui.home.HomeActivity
import com.example.fundo.utils.SharedPrefUtil

class SplashScreenActivity: AppCompatActivity() {
    private lateinit var binding: SplashScreenBinding
    private lateinit var authenticationSharedViewModel: AuthenticationSharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = SplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        SharedPrefUtil.initSharedPref(this)
        val splashScreenIcon = binding.splashScreenIcon
        splashScreenIcon.alpha = 0f
        splashScreenIcon.animate().setDuration(10).alpha(1f).withEndAction{
            if(AuthService.getCurrentUser() == null){
                val intent = Intent(this, AuthenticationActivity::class.java)
                finish()
                startActivity(intent)
            }
            else{
                DatabaseService.getUserFromDB {
                    val intent = Intent(this, HomeActivity::class.java)
                    finish()
                    startActivity(intent)
                }
            }

        }
    }
}