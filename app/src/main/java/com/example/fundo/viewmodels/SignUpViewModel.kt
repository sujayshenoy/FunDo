package com.example.fundo.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fundo.models.User
import com.example.fundo.services.Auth
import com.example.fundo.services.Database

class SignUpViewModel: ViewModel() {
    private val _emailPassSignUpStatus = MutableLiveData<Boolean>()
    val emailPassSignUpStatus = _emailPassSignUpStatus as LiveData<Boolean>

    fun signUpWithEmailAndPassword(user: User,password: String) {
        Auth.signUpWithEmailAndPassword(user.email,password) {
            Database.addUserToDB(user){
                _emailPassSignUpStatus.value = it
            }
        }
    }
}