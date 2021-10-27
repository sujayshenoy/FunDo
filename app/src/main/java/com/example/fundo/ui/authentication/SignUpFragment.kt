package com.example.fundo.ui.authentication

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.fundo.R
import com.example.fundo.databinding.SignUpFragmentBinding
import com.example.fundo.models.User
import com.example.fundo.utils.Utilities
import com.example.fundo.utils.Validators
import com.example.fundo.viewmodels.AuthenticationViewModel
import com.example.fundo.viewmodels.AuthenticationViewModelFactory
import com.example.fundo.viewmodels.SignUpViewModel
import com.example.fundo.viewmodels.SignUpViewModelFactory

class SignUpFragment:Fragment(R.layout.sign_up_fragment) {
    private lateinit var authenticationViewModel: AuthenticationViewModel
    private lateinit var signUpViewModel: SignUpViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = SignUpFragmentBinding.bind(view)
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.loading_dialog)
        authenticationViewModel = ViewModelProvider(requireActivity(), AuthenticationViewModelFactory())[AuthenticationViewModel::class.java]
        signUpViewModel = ViewModelProvider(this,SignUpViewModelFactory())[SignUpViewModel::class.java]

        attachListeners()
        attachObservers()
    }

    private fun attachObservers() {
        signUpViewModel.emailPassSignUpStatus.observe(viewLifecycleOwner){
            if(it){
                authenticationViewModel.setGoToHome(true)
            }
            else{
                Utilities.displayToast(requireContext(),"Something went wrong!! User not registered")
            }
            dialog.dismiss()
        }
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
        val user = User(name.text.toString(),email.text.toString(),phone.text.toString())

        if(Validators.signUpValidator(name,email,phone,password,confirmPassword)){
            signUpViewModel.signUpWithEmailAndPassword(user,password.text.toString())
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