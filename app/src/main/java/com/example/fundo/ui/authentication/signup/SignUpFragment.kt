package com.example.fundo.ui.authentication.signup

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.fundo.R
import com.example.fundo.databinding.FragmentSignupBinding
import com.example.fundo.ui.authentication.AuthenticationSharedViewModel
import com.example.fundo.data.wrappers.User
import com.example.fundo.common.Utilities
import com.example.fundo.common.Validators

class SignUpFragment : Fragment(R.layout.fragment_signup) {
    private lateinit var authenticationSharedViewModel: AuthenticationSharedViewModel
    private lateinit var signUpViewModel: SignUpViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSignupBinding.bind(view)
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_loading)

        authenticationSharedViewModel =
            ViewModelProvider(requireActivity())[AuthenticationSharedViewModel::class.java]
        signUpViewModel = ViewModelProvider(this@SignUpFragment)[SignUpViewModel::class.java]

        attachListeners()
        attachObservers()
    }

    private fun attachObservers() {
        signUpViewModel.emailPassSignUpStatus.observe(viewLifecycleOwner) {
            if (it) {
                authenticationSharedViewModel.setGoToHome(true)
            } else {
                Utilities.displayToast(requireContext(), getString(R.string.user_not_registered))
            }
            dialog.dismiss()
        }
    }

    private fun attachListeners() {
        binding.regLogChangeRText.setOnClickListener {
            authenticationSharedViewModel.setGoToLoginScreenStatus(true)
        }

        binding.signUpButton.setOnClickListener {
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
        val user = User(name.text.toString(), email.text.toString(), phone.text.toString())

        if (Validators.signUpValidator(
                requireContext(),
                name,
                email,
                phone,
                password,
                confirmPassword
            )
        ) {
            signUpViewModel.signUpWithEmailAndPassword(
                requireContext(),
                user,
                password.text.toString()
            )
        } else {
            dialog.dismiss()
        }
    }

    companion object {
        lateinit var binding: FragmentSignupBinding
        lateinit var dialog: Dialog
    }
}