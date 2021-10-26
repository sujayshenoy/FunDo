package com.example.fundo.ui.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.example.fundo.R
import com.example.fundo.databinding.AuthBinding
import com.example.fundo.models.DBUser
import com.example.fundo.services.Database
import com.example.fundo.ui.home.HomeActivity
import com.example.fundo.utils.Utilities
import com.example.fundo.viewmodels.AuthenticationViewModel
import com.example.fundo.viewmodels.AuthenticationViewModelFactory

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

        authenticationViewModel.resetPasswordStatus.observe(this@AuthenticationActivity){
            if(it){
                Utilities.displayToast(this@AuthenticationActivity,"Password reset link has been sent to your email")
            }
            else{
                Utilities.displayToast(this@AuthenticationActivity,"User not registered")
            }
            authenticationViewModel.setGoToLoginScreenStatus(true)
            ResetPasswordFragment.dialog.dismiss()
        }

        authenticationViewModel.emailPassSignUpStatus.observe(this@AuthenticationActivity){
            if(it){
                val user = DBUser(
                    SignUpFragment.binding.nameTextEdit.text.toString(),
                    SignUpFragment.binding.emailTextEdit.text.toString() ,
                    SignUpFragment.binding.phoneTextEdit.text.toString())
                Database.addUserToDB(user)

                Utilities.displayToast(this@AuthenticationActivity,"Sign Up Successful")
                authenticationViewModel.setGoToHome(true)
            }
            else{
                Utilities.displayToast(this@AuthenticationActivity,"Something went wrong!! User not registered")
            }
            SignUpFragment.dialog.dismiss()
        }

        authenticationViewModel.emailPassLoginStatus.observe(this@AuthenticationActivity){ user ->
            handleLogin(user.loginStatus)
        }

        authenticationViewModel.facebookLoginStatus.observe(this@AuthenticationActivity){ user ->
            val dbUser = DBUser(user.name,user.email,user.phone)
            Database.addUserToDB(dbUser)
            handleLogin(user.loginStatus)
        }
    }

    private fun handleLogin(status:Boolean){
        if(status) {
            Utilities.displayToast(this,"Sign in success")
            Database.getUserFromDB {
                val user = Utilities.createUserFromHashMap(it)
                Log.i("Auth","User object $user")
            }
            authenticationViewModel.setGoToHome(true)
        }
        else {
            Log.d("Auth","Authentication Failed")
            Utilities.displayToast(this,"Authentication Failed")
        }
        LoginFragment.dialog.dismiss()
    }

    companion object{

    }
}