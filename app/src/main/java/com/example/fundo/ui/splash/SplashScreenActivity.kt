package com.example.fundo.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fundo.databinding.ActivitySplashBinding
import com.example.fundo.auth.services.FirebaseAuthService
import com.example.fundo.data.services.DatabaseService
import com.example.fundo.ui.authentication.AuthenticationActivity
import com.example.fundo.ui.home.HomeActivity
import com.example.fundo.common.SharedPrefUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class SplashScreenActivity: AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        SharedPrefUtil.initSharedPref(this@SplashScreenActivity)
        val splashScreenIcon = binding.splashScreenIcon
        splashScreenIcon.alpha = 0f
        splashScreenIcon.animate().setDuration(10).alpha(1f).withEndAction{
            if(FirebaseAuthService.getCurrentUser() == null){
                val intent = Intent(this@SplashScreenActivity, AuthenticationActivity::class.java)
                finish()
                startActivity(intent)
            }
            else{
                CoroutineScope(Dispatchers.IO).launch(Dispatchers.IO) {
                    DatabaseService.getUserFromDB()
                    val intent = Intent(this@SplashScreenActivity, HomeActivity::class.java)
                    finish()
                    startActivity(intent)
                }
            }
        }
    }
}