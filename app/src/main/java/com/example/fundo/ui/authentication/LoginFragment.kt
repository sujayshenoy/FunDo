package com.example.fundo.ui.authentication

import android.app.Dialog
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
import com.google.firebase.database.ktx.getValue

class LoginFragment : Fragment(R.layout.login_fragment) {
    private lateinit var binding:LoginFragmentBinding
    private lateinit var dialog:Dialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(Auth.getCurrentUser() != null){
//            Auth.signOut()
            Database.getUserFromDB {
                val user = Utilities.createUserFromHashMap(it)
                Log.i("Auth","User object $user")
            }
            //TODO(GO to home page)
        }

        binding = LoginFragmentBinding.bind(view)
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.loading_dialog)

        attachListeners()
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