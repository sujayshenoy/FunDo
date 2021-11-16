package com.example.fundo.ui.authentication.login

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.fundo.R
import com.example.fundo.common.Logger
import com.example.fundo.databinding.FragmentLoginBinding
import com.example.fundo.ui.authentication.AuthenticationSharedViewModel
import com.example.fundo.common.Utilities
import com.example.fundo.common.Validators
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult

class LoginFragment : Fragment(R.layout.fragment_login) {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var callbackManager: CallbackManager
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var authenticationSharedViewModel: AuthenticationSharedViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentLoginBinding.bind(view)
        callbackManager = CallbackManager.Factory.create()
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_loading)

        loginViewModel = ViewModelProvider(this@LoginFragment)[LoginViewModel::class.java]
        authenticationSharedViewModel = ViewModelProvider(
            requireActivity()
        )[AuthenticationSharedViewModel::class.java]

        attachListeners()
        attachObservers()
    }

    private fun attachObservers() {
        loginViewModel.emailPassLoginStatus.observe(viewLifecycleOwner) {
            handleLogin(it)
        }

        loginViewModel.facebookLoginStatus.observe(viewLifecycleOwner) {
            handleLogin(it)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun attachListeners() {
        binding.forgotPasswordText.setOnClickListener {
            authenticationSharedViewModel.setGoToResetPassword(true)
        }

        binding.regLogChangeRText.setOnClickListener {
            authenticationSharedViewModel.setGoToSignUpScreenStatus(true)
        }

        binding.loginButton.setOnClickListener {
            login()
        }

        binding.facebookLoginButton.setOnClickListener {
            var facebookLoginButton = binding.facebookLoginButton
            facebookLoginButton.setReadPermissions("email", "public_profile")
            facebookLoginButton.registerCallback(callbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onCancel() {
                        Logger.logAuthInfo("facebook:onCancel")
                    }

                    override fun onError(error: FacebookException) {
                        Logger.logAuthError("facebook:onError")
                        Logger.logAuthError(error.toString())
                    }

                    override fun onSuccess(result: LoginResult) {
                        Logger.logAuthInfo("facebook:onSuccess")
                        loginViewModel.loginWithFacebook(requireContext(), result.accessToken)
                    }
                })
        }
    }

    private fun login() {
        dialog.show()
        val email = binding.usernameTextEdit
        val password = binding.passwordTextEdit

        if (Validators.logInValidator(requireContext(), email, password)) {
            loginViewModel.loginWithEmailAndPassword(
                requireContext(),
                email.text.toString(),
                password.text.toString()
            )
        } else {
            dialog.dismiss()
        }
    }

    private fun handleLogin(status: Boolean) {
        if (status) {
            Utilities.displayToast(requireContext(), getString(R.string.sign_in_success_toast))
            authenticationSharedViewModel.setGoToHome(true)
        } else {
            Utilities.displayToast(requireContext(), getString(R.string.sign_in_failed_toast))
        }
        dialog.dismiss()
    }

    companion object {
        lateinit var dialog: Dialog
    }
}