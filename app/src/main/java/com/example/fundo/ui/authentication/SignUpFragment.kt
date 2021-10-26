package com.example.fundo.ui.authentication

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.fundo.R
import com.example.fundo.databinding.SignUpFragmentBinding
import com.example.fundo.utils.Validators
import com.example.fundo.viewmodels.AuthenticationViewModel
import com.example.fundo.viewmodels.AuthenticationViewModelFactory

class SignUpFragment:Fragment(R.layout.sign_up_fragment) {
    private lateinit var authenticationViewModel: AuthenticationViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = SignUpFragmentBinding.bind(view)
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.loading_dialog)
        authenticationViewModel = ViewModelProvider(requireActivity(), AuthenticationViewModelFactory())[AuthenticationViewModel::class.java]

        attachListeners()
    }

    private fun attachListeners() {
        binding.regLogChangeRText.setOnClickListener{
            authenticationViewModel.setGoToLoginScreenStatus(true)
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
            authenticationViewModel.signUpWithEmailAndPassword(email.text.toString(),password.text.toString())
        }
        else{
            dialog.dismiss()
        }
    }

    companion object{
        lateinit var binding : SignUpFragmentBinding
        lateinit var dialog : Dialog
    }
}