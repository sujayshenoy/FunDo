package com.example.fundo.ui.authentication.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fundo.data.wrappers.User
import com.example.fundo.auth.services.FirebaseAuthService
import com.example.fundo.common.Logger
import com.example.fundo.data.services.DatabaseService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignUpViewModel: ViewModel() {
    private val _emailPassSignUpStatus = MutableLiveData<Boolean>()
    val emailPassSignUpStatus = _emailPassSignUpStatus as LiveData<Boolean>

    fun signUpWithEmailAndPassword(user: User, password: String) {
        FirebaseAuthService.signUpWithEmailAndPassword(user.email,password) {
            viewModelScope.launch{
                if(it){
                    DatabaseService.addUserToDB(user)
                }
                _emailPassSignUpStatus.value = it
            }
        }
    }
}