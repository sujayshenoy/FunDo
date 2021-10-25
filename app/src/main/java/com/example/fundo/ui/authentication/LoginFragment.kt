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
import com.example.fundo.models.User
import com.example.fundo.services.Auth
import com.example.fundo.services.Database
import com.example.fundo.ui.home.HomeActivity
import com.example.fundo.utils.Utilities
import com.example.fundo.utils.Validators
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider

class LoginFragment : Fragment(R.layout.login_fragment) {
    private lateinit var binding:LoginFragmentBinding
    private lateinit var dialog:Dialog
    private lateinit var callbackManager:CallbackManager
    private lateinit var authenticationViewModel: AuthenticationViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = LoginFragmentBinding.bind(view)
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.loading_dialog)
        authenticationViewModel = ViewModelProvider(requireActivity(),AuthenticationViewModelFactory())[AuthenticationViewModel::class.java]
        callbackManager = CallbackManager.Factory.create()

        attachListeners()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        callbackManager.onActivityResult(requestCode,resultCode,data)
    }

    private fun attachListeners() {
        binding.forgotPasswordText.setOnClickListener{
            authenticationViewModel.setGoToResetPassword(true)
        }

        binding.regLogChangeRText.setOnClickListener{
            authenticationViewModel.setGoToSignUpScreenStatus(true)
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
                    Auth.handleFacebookLogin(result.accessToken){ firebaseUser ->
                        if(firebaseUser == null){
                            Utilities.displayToast(requireContext(),"Authentication Failed")
                        }
                        else{
                            Database.getUserFromDB {
                                var user = Utilities.createUserFromHashMap(it)
                                goToHomePage()
                            }
                        }
                    }
                }

            })
        }
    }

    private fun goToHomePage() {
        authenticationViewModel.setGoToHome(true)
    }

    private fun login() {
        dialog.show()
        val email = binding.usernameTextEdit
        val password = binding.passwordTextEdit

        if(Validators.logInValidator(email,password)){
            Auth.signInWithEmailAndPassword(email.text.toString(),password.text.toString()){ firebaseUser ->
                if(firebaseUser == null) {
                    Utilities.displayToast(requireContext(),"Sign In Failed")
                }
                else{
                    Utilities.displayToast(requireContext(),"Sign in success")
                    Database.getUserFromDB {
                        val user = Utilities.createUserFromHashMap(it)
                        Log.i("Auth","User object $user")
                    }
                    goToHomePage()
                }
                dialog.dismiss()
            }
        }
        else{
            dialog.dismiss()
        }
    }
}