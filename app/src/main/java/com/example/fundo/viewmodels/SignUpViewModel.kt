package com.example.fundo.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fundo.models.User
import com.example.fundo.services.AuthService
import com.example.fundo.services.DatabaseService

class SignUpViewModel: ViewModel() {
    private val _emailPassSignUpStatus = MutableLiveData<Boolean>()
    val emailPassSignUpStatus = _emailPassSignUpStatus as LiveData<Boolean>

    fun signUpWithEmailAndPassword(user: User,password: String) {
        AuthService.signUpWithEmailAndPassword(user.email,password) {
            DatabaseService.addUserToDB(user){
                _emailPassSignUpStatus.value = it
            }
        }
    }
}