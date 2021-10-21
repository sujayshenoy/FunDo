package com.example.fundo.ui.authentication

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.fundo.R
import com.example.fundo.databinding.SignUpFragmentBinding
import com.example.fundo.models.User
import com.example.fundo.services.Auth
import com.example.fundo.services.Database
import com.example.fundo.utils.Utilities
import com.example.fundo.utils.Validators

class SignUpFragment:Fragment(R.layout.sign_up_fragment) {
    private lateinit var binding : SignUpFragmentBinding
    private lateinit var dialog : Dialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = SignUpFragmentBinding.bind(view)
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.loading_dialog)

        attachListeners()
    }

    private fun attachListeners() {
        binding.regLogChangeRText.setOnClickListener{
            Utilities.fragmentSwitcher(requireActivity().supportFragmentManager,R.id.authFragmentContainer,
                LoginFragment()
            )
        }

        binding.signUpButton.setOnClickListener{
            register()
        }
    }

    private fun register() {
        dialog.show()

        var name = binding.nameTextEdit
        var email = binding.emailTextEdit
        var phone = binding.phoneTextEdit
        var password = binding.passwordTextEdit
        var confirmPassword = binding.confirmPasswordTextEdit

        if(Validators.signUpValidator(name,email,phone,password,confirmPassword)){
            val user = User(name.text.toString(),
                email.text.toString() ,
                phone.text.toString())

            Auth.signUpWithEmailAndPassword(email.text.toString(),password.text.toString()){ firebaseUser ->
                if(firebaseUser == null) {
                    Utilities.displayToast(requireContext(),"Sign Up Failed")
                }
                else{
                    Database.addUserToDB(user)
                }
                dialog.dismiss()
            }
        }
    }
}