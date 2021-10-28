package com.example.fundo.ui.authentication

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.fundo.R
import com.example.fundo.databinding.ForgotPasswordBinding
import com.example.fundo.utils.Utilities
import com.example.fundo.utils.Validators
import com.example.fundo.viewmodels.AuthenticationSharedViewModel
import com.example.fundo.viewmodels.AuthenticationSharedViewModelFactory
import com.example.fundo.viewmodels.ResetPasswordViewModel
import com.example.fundo.viewmodels.ResetPasswordViewModelFactory

class ResetPasswordFragment:Fragment(R.layout.forgot_password) {
    private lateinit var binding:ForgotPasswordBinding
    private lateinit var authenticationSharedViewModel: AuthenticationSharedViewModel
    private lateinit var resetPasswordViewModel: ResetPasswordViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = ForgotPasswordBinding.bind(view)
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.loading_dialog)

        authenticationSharedViewModel = ViewModelProvider(requireActivity(),
            AuthenticationSharedViewModelFactory()
        )[AuthenticationSharedViewModel::class.java]
        resetPasswordViewModel = ViewModelProvider(this,
            ResetPasswordViewModelFactory()
        )[ResetPasswordViewModel::class.java]

        attachListeners()
        attachObservers()
    }

    private fun attachListeners() {
        binding.resetPasswordButton.setOnClickListener{
            resetPassword(binding.emailTextEdit.text.toString())
        }
    }

    private fun attachObservers() {
        resetPasswordViewModel.resetPasswordStatus.observe(viewLifecycleOwner){
            if(it){
                Utilities.displayToast(requireContext(),"Password reset link has been sent to your email")
            }
            else{
                Utilities.displayToast(requireContext(),"User not registered")
            }
            authenticationSharedViewModel.setGoToLoginScreenStatus(true)
            dialog.dismiss()
        }
    }

    private fun resetPassword(email:String) {
        if(Validators.resetPasswordValidator(binding.emailTextEdit)){
            dialog.show()
            resetPasswordViewModel.resetPassword(email)
        }
    }

    companion object {
        lateinit var dialog: Dialog
    }
}