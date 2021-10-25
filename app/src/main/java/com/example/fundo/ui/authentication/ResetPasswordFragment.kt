package com.example.fundo.ui.authentication

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.fundo.R
import com.example.fundo.databinding.ForgotPasswordBinding
import com.example.fundo.services.Auth
import com.example.fundo.utils.Utilities
import com.example.fundo.utils.Validators

class ResetPasswordFragment:Fragment(R.layout.forgot_password) {
    private lateinit var binding:ForgotPasswordBinding
    private lateinit var dialog: Dialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = ForgotPasswordBinding.bind(view)
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.loading_dialog)

        binding.resetPasswordButton.setOnClickListener{
            resetPassword(binding.emailTextEdit.text.toString())
        }

    }

    private fun resetPassword(email:String) {
        if(Validators.resetPasswordValidator(binding.emailTextEdit)){
            dialog.show()
            Auth.resetPassword(email){
                Utilities.displayToast(requireContext(),it)
                Utilities.fragmentSwitcher(requireActivity().supportFragmentManager,R.id.authFragmentContainer,LoginFragment())
                dialog.dismiss()
            }
        }
    }
}