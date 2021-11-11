package com.example.fundo.ui.authentication.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fundo.data.wrappers.User
import com.example.fundo.auth.services.FirebaseAuthService
import com.example.fundo.data.services.DatabaseService

class SignUpViewModel: ViewModel() {
    private val _emailPassSignUpStatus = MutableLiveData<Boolean>()
    val emailPassSignUpStatus = _emailPassSignUpStatus as LiveData<Boolean>

    fun signUpWithEmailAndPassword(user: User, password: String) {
        FirebaseAuthService.signUpWithEmailAndPassword(user.email,password) {
            DatabaseService.addUserToDB(user){
                _emailPassSignUpStatus.value = it
            }
        }
    }
}