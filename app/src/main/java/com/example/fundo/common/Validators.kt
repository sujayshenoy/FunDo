package com.example.fundo.common

import android.content.Context
import android.util.Patterns
import com.example.fundo.R
import com.google.android.material.textfield.TextInputEditText

object Validators {
    fun signUpValidator(
        context: Context,
        name: TextInputEditText,
        email: TextInputEditText,
        phone: TextInputEditText,
        password: TextInputEditText,
        confirmPassword: TextInputEditText
    ): Boolean {
        var flag = true
        if (!nameValidator(name.text.toString())) {
            name.error = context.getString(R.string.name_validation_error)
            flag = false
        }
        if (!emailVaidator(email.text.toString())) {
            email.error = context.getString(R.string.email_validation_error)
            flag = false
        }
        if (!phoneValidator(phone.text.toString())) {
            phone.error = context.getString(R.string.phone_validation_error)
            flag = false
        }

        val passwordString = password.text.toString()
        if (passwordString != confirmPassword.text.toString()) {
            password.error = context.getString(R.string.passwords_mismatch_error)
            confirmPassword.error = context.getString(R.string.passwords_mismatch_error)
            flag = false
        } else {
            if (!passwordValidator(passwordString)) {
                password.error = context.getString(R.string.password_min_chars_validation_error)
                flag = false
            }
        }

        return flag
    }

    fun logInValidator(
        context: Context,
        email: TextInputEditText,
        password: TextInputEditText
    ): Boolean {
        var flag = true
        if (!emailVaidator(email.text.toString())) {
            email.error = context.getString(R.string.email_validation_error)
            flag = false
        }
        if (password.text.toString().isEmpty()) {
            password.error = context.getString(R.string.password_validation_error)
            flag = false
        }

        return flag
    }

    fun resetPasswordValidator(context: Context, email: TextInputEditText): Boolean {
        var flag = true
        if (!emailVaidator(email.text.toString())) {
            email.error = context.getString(R.string.email_validation_error)
            flag = false
        }

        return flag
    }

    private fun nameValidator(name: String) = name.isNotEmpty()

    private fun emailVaidator(email: String) = Patterns.EMAIL_ADDRESS.matcher(email).matches()

    private fun phoneValidator(phone: String) = phone.length == 10

    private fun passwordValidator(password: String) = password.length >= 8

}