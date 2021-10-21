package com.example.fundo.utils

import android.util.Patterns
import com.google.android.material.textfield.TextInputEditText

object Validators {
    fun signUpValidator(
        name: TextInputEditText,
        email: TextInputEditText,
        phone: TextInputEditText,
        password: TextInputEditText,
        confirmPassword: TextInputEditText
    ) : Boolean {
        var flag = true
        if(!nameValidator(name.text.toString())){
            name.setError("Please enter your name")
            flag = false
        }
        if(!emailVaidator(email.text.toString())){
            email.setError("Please enter a valid email address")
            flag = false
        }
        if(!phoneValidator(phone.text.toString())){
            phone.setError("Please enter a valid phone")
            flag = false
        }

        val passwordString = password.text.toString()
        if(passwordString != confirmPassword.text.toString()){
            password.setError("Passwords don't match")
            confirmPassword.setError("Passwords don't match")
            flag = false
        }
        else{
            if(!passwordValidator(passwordString)){
                password.setError("Password must be of minimum 8 characters")
                flag = false
            }
        }

        return flag
    }

    fun logInValidator(email:TextInputEditText,password:TextInputEditText) : Boolean {
        var flag = true
        if(!emailVaidator(email.text.toString())){
            email.setError("Please enter a valid email address")
            flag = false
        }
        if(!password.text.toString().isNotEmpty()){
            password.setError("Please enter the password")
            flag = false
        }

        return flag
    }

    private fun nameValidator(name:String)  = name.isNotEmpty()

    private fun emailVaidator(email: String) = Patterns.EMAIL_ADDRESS.matcher(email).matches()

    private fun phoneValidator(phone:String) = phone.length == 10

    private fun passwordValidator(password:String) = password.length >= 8

}