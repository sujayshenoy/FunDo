package com.example.fundo.ui.authentication

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.example.fundo.R
import com.example.fundo.databinding.LoginFragmentBinding
import com.example.fundo.models.User
import com.example.fundo.services.Auth
import com.example.fundo.services.Database
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(Auth.getCurrentUser() != null){
//            Auth.signOut()   //TODO(Remove this later)
            Utilities.displayToast(requireContext(),"User already logged in")
            Database.getUserFromDB {
                val user = Utilities.createUserFromHashMap(it)
            }
            //TODO(GO to home page)
        }

        binding = LoginFragmentBinding.bind(view)
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.loading_dialog)
        callbackManager = CallbackManager.Factory.create()

        attachListeners()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        callbackManager.onActivityResult(requestCode,resultCode,data)
    }

    private fun attachListeners() {
        binding.regLogChangeRText.setOnClickListener{
            Utilities.fragmentSwitcher(requireActivity().supportFragmentManager,R.id.authFragmentContainer,
                SignUpFragment()
            )
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
                                //TODO(GO TO HOMEPAGE)
                            }
                        }
                    }
                }

            })
        }
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
                    //TODO(GO to home page)
                }
                dialog.dismiss()
            }
        }
        else{
            dialog.dismiss()
        }
    }
}