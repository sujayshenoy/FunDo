package com.example.fundo.ui.authentication

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.fundo.R
import com.example.fundo.databinding.LoginFragmentBinding
import com.example.fundo.utils.Utilities
import com.example.fundo.utils.Validators
import com.example.fundo.viewmodels.AuthenticationSharedViewModel
import com.example.fundo.viewmodels.AuthenticationSharedViewModelFactory
import com.example.fundo.viewmodels.LoginViewModel
import com.example.fundo.viewmodels.LoginViewModelFactory
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult

class LoginFragment : Fragment(R.layout.login_fragment) {
    private lateinit var binding:LoginFragmentBinding
    private lateinit var callbackManager:CallbackManager
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var authenticationSharedViewModel:AuthenticationSharedViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = LoginFragmentBinding.bind(view)
        callbackManager = CallbackManager.Factory.create()
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.loading_dialog)

        loginViewModel = ViewModelProvider(this,
            LoginViewModelFactory()
        )[LoginViewModel::class.java]
        authenticationSharedViewModel = ViewModelProvider(requireActivity(),
            AuthenticationSharedViewModelFactory()
        )[AuthenticationSharedViewModel::class.java]

        attachListeners()
        attachObservers()
    }

    private fun attachObservers() {
        loginViewModel.emailPassLoginStatus.observe(viewLifecycleOwner){ user ->
            handleLogin(user.loginStatus)
        }

        loginViewModel.facebookLoginStatus.observe(viewLifecycleOwner){ user ->
            handleLogin(user.loginStatus)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        callbackManager.onActivityResult(requestCode,resultCode,data)
    }

    private fun attachListeners() {
        binding.forgotPasswordText.setOnClickListener{
            authenticationSharedViewModel.setGoToResetPassword(true)
        }

        binding.regLogChangeRText.setOnClickListener{
            authenticationSharedViewModel.setGoToSignUpScreenStatus(true)
        }

        binding.loginButton.setOnClickListener{
            login()
        }

        binding.facebookLoginButton.setOnClickListener{
            var facebookLoginButton = binding.facebookLoginButton
            facebookLoginButton.setReadPermissions("email","public_profile")
            facebookLoginButton.registerCallback(callbackManager,object : FacebookCallback<LoginResult>{
                override fun onCancel() {
                    Log.d("Facebook-OAuth","facebook:onCancel")
                }

                override fun onError(error: FacebookException) {
                    Log.d("Facebook-OAuth","facebook:onError",error)
                }

                override fun onSuccess(result: LoginResult) {
                    Log.d("Facebook-OAuth","facebook:onSuccess:$result")
                    loginViewModel.loginWithFacebook(result.accessToken)
                }
            })
        }
    }

    private fun login() {
        dialog.show()
        val email = binding.usernameTextEdit
        val password = binding.passwordTextEdit

        if(Validators.logInValidator(email,password)){
            loginViewModel.loginWithEmailAndPassword(email.text.toString(),password.text.toString())
        }
        else{
            dialog.dismiss()
        }
    }

    private fun handleLogin(status:Boolean){
        if(status) {
            Utilities.displayToast(requireContext(),"Sign in success")
            authenticationSharedViewModel.setGoToHome(true)
        }
        else {
            Log.d("Auth","Authentication Failed")
            Utilities.displayToast(requireContext(),"Authentication Failed")
        }
        dialog.dismiss()
    }

    companion object{
        lateinit var dialog:Dialog
    }
}