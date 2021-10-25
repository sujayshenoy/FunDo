package com.example.fundo.ui.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.fundo.R
import com.example.fundo.databinding.AuthBinding
import com.example.fundo.ui.home.HomeActivity
import com.example.fundo.utils.Utilities

class AuthenticationActivity : AppCompatActivity() {
    private lateinit var binding: AuthBinding
    private lateinit var authenticationViewModel: AuthenticationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = AuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        authenticationViewModel = ViewModelProvider(this@AuthenticationActivity, AuthenticationViewModelFactory())[AuthenticationViewModel::class.java]

        authenticationViewModel.setGoToLoginScreenStatus(true)

        attachObservers()
    }

    private fun attachObservers() {
        authenticationViewModel.goToLoginScreen.observe(this,{
            if(it){
                Utilities.fragmentSwitcher(supportFragmentManager,R.id.authFragmentContainer,LoginFragment())
            }
        })

        authenticationViewModel.goToSignUpScreen.observe(this,{
            if(it){
                Utilities.fragmentSwitcher(supportFragmentManager,R.id.authFragmentContainer,SignUpFragment())
            }
        })

        authenticationViewModel.goToResetPassword.observe(this,{
            if(it){
                Utilities.fragmentSwitcher(supportFragmentManager,R.id.authFragmentContainer,ResetPasswordFragment())
            }
        })

        authenticationViewModel.goToHome.observe(this,{
            if(it){
                val intent = Intent(this,HomeActivity::class.java)
                finish()
                startActivity(intent)
            }
        })
    }
}