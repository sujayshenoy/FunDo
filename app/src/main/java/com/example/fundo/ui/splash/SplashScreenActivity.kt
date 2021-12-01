package com.example.fundo.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fundo.databinding.ActivitySplashBinding
import com.example.fundo.data.services.DatabaseService
import com.example.fundo.ui.authentication.AuthenticationActivity
import com.example.fundo.ui.home.HomeActivity
import com.example.fundo.common.SharedPrefUtil
import com.example.fundo.common.Utilities
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.*

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val splashScreenIcon = binding.splashScreenIcon
        splashScreenIcon.alpha = 0f
        splashScreenIcon.animate().setDuration(500).alpha(1f).withEndAction {
            if (SharedPrefUtil.getInstance(this@SplashScreenActivity).getUserId() == 0L) {
                val intent = Intent(this@SplashScreenActivity, AuthenticationActivity::class.java)
                finish()
                startActivity(intent)
            } else {
                CoroutineScope(Dispatchers.IO).launch(Dispatchers.IO) {
                    val intent = Intent(this@SplashScreenActivity, HomeActivity::class.java)
                    finish()
                    startActivity(intent)
                }
            }
        }

        Firebase.messaging.subscribeToTopic("general")
            .addOnCompleteListener {
                Utilities.displayToast(this,"Subscribed to general")
            }
    }
}