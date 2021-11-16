package com.example.fundo.ui.authentication.resetpassword

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.fundo.R
import com.example.fundo.databinding.FragmentForgotPasswordBinding
import com.example.fundo.ui.authentication.AuthenticationSharedViewModel
import com.example.fundo.common.Utilities
import com.example.fundo.common.Validators

class ResetPasswordFragment : Fragment(R.layout.fragment_forgot_password) {
    private lateinit var binding: FragmentForgotPasswordBinding
    private lateinit var authenticationSharedViewModel: AuthenticationSharedViewModel
    private lateinit var resetPasswordViewModel: ResetPasswordViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentForgotPasswordBinding.bind(view)
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_loading)

        authenticationSharedViewModel = ViewModelProvider(
            requireActivity()
        )[AuthenticationSharedViewModel::class.java]
        resetPasswordViewModel =
            ViewModelProvider(this@ResetPasswordFragment)[ResetPasswordViewModel::class.java]

        attachListeners()
        attachObservers()
    }

    private fun attachListeners() {
        binding.resetPasswordButton.setOnClickListener {
            resetPassword(binding.emailTextEdit.text.toString())
        }
    }

    private fun attachObservers() {
        resetPasswordViewModel.resetPasswordStatus.observe(viewLifecycleOwner) {
            if (it) {
                Utilities.displayToast(
                    requireContext(),
                    getString(R.string.password_reset_link_sent_text)
                )
            } else {
                Utilities.displayToast(requireContext(), getString(R.string.user_not_registered))
            }
            authenticationSharedViewModel.setGoToLoginScreenStatus(true)
            dialog.dismiss()
        }
    }

    private fun resetPassword(email: String) {
        if (Validators.resetPasswordValidator(requireContext(), binding.emailTextEdit)) {
            dialog.show()
            resetPasswordViewModel.resetPassword(email)
        }
    }

    companion object {
        lateinit var dialog: Dialog
    }
}