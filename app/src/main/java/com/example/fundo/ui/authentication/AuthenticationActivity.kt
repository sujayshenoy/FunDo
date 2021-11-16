package com.example.fundo.ui.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.fundo.R
import com.example.fundo.databinding.ActivityAuthenticationBinding
import com.example.fundo.ui.authentication.login.LoginFragment
import com.example.fundo.ui.authentication.resetpassword.ResetPasswordFragment
import com.example.fundo.ui.authentication.signup.SignUpFragment
import com.example.fundo.ui.home.HomeActivity
import com.example.fundo.common.Utilities

class AuthenticationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthenticationBinding
    private lateinit var authenticationSharedViewModel: AuthenticationSharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        authenticationSharedViewModel =
            ViewModelProvider(this@AuthenticationActivity)[AuthenticationSharedViewModel::class.java]
        attachObservers()

        authenticationSharedViewModel.setGoToLoginScreenStatus(true)
    }

    private fun attachObservers() {
        authenticationSharedViewModel.goToLoginScreen.observe(this@AuthenticationActivity, {
            if (it) {
                Utilities.fragmentSwitcher(
                    supportFragmentManager, R.id.authFragmentContainer,
                    LoginFragment()
                )
            }
        })

        authenticationSharedViewModel.goToSignUpScreen.observe(this@AuthenticationActivity, {
            if (it) {
                Utilities.fragmentSwitcher(
                    supportFragmentManager, R.id.authFragmentContainer,
                    SignUpFragment()
                )
            }
        })

        authenticationSharedViewModel.goToResetPassword.observe(this@AuthenticationActivity, {
            if (it) {
                Utilities.fragmentSwitcher(
                    supportFragmentManager, R.id.authFragmentContainer,
                    ResetPasswordFragment()
                )
            }
        })

        authenticationSharedViewModel.goToHome.observe(this@AuthenticationActivity, {
            if (it) {
                val intent = Intent(this@AuthenticationActivity, HomeActivity::class.java)
                finish()
                startActivity(intent)
            }
        })
    }
}