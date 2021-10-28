package com.example.fundo.ui.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.fundo.R
import com.example.fundo.databinding.AuthBinding
import com.example.fundo.ui.home.HomeActivity
import com.example.fundo.utils.Utilities
import com.example.fundo.viewmodels.AuthenticationSharedViewModel
import com.example.fundo.viewmodels.AuthenticationSharedViewModelFactory

class AuthenticationActivity : AppCompatActivity() {
    private lateinit var binding: AuthBinding
    private lateinit var authenticationSharedViewModel: AuthenticationSharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = AuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        authenticationSharedViewModel = ViewModelProvider(this@AuthenticationActivity, AuthenticationSharedViewModelFactory())[AuthenticationSharedViewModel::class.java]
        attachObservers()

        authenticationSharedViewModel.setGoToLoginScreenStatus(true)
    }

    private fun attachObservers() {
        authenticationSharedViewModel.goToLoginScreen.observe(this,{
            if(it){
                Utilities.fragmentSwitcher(supportFragmentManager,R.id.authFragmentContainer,LoginFragment())
            }
        })

        authenticationSharedViewModel.goToSignUpScreen.observe(this,{
            if(it){
                Utilities.fragmentSwitcher(supportFragmentManager,R.id.authFragmentContainer,SignUpFragment())
            }
        })

        authenticationSharedViewModel.goToResetPassword.observe(this,{
            if(it){
                Utilities.fragmentSwitcher(supportFragmentManager,R.id.authFragmentContainer,ResetPasswordFragment())
            }
        })

        authenticationSharedViewModel.goToHome.observe(this,{
            if(it){
                val intent = Intent(this,HomeActivity::class.java)
                finish()
                startActivity(intent)
            }
        })
    }
}