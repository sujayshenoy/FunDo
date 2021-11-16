package com.example.fundo.ui.authentication.signup

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fundo.data.wrappers.User
import com.example.fundo.auth.services.FirebaseAuthService
import com.example.fundo.common.SharedPrefUtil
import com.example.fundo.data.services.DatabaseService
import kotlinx.coroutines.launch

class SignUpViewModel : ViewModel() {
    private val _emailPassSignUpStatus = MutableLiveData<Boolean>()
    val emailPassSignUpStatus = _emailPassSignUpStatus as LiveData<Boolean>

    fun signUpWithEmailAndPassword(context: Context, user: User, password: String) {
        FirebaseAuthService.signUpWithEmailAndPassword(user.email, password) { userAuth ->
            viewModelScope.launch {
                if (userAuth != null) {
                    val newUser =
                        User(user.name, user.email, user.phone, user.id, userAuth.firebaseId)
                    DatabaseService.getInstance(context).addUserToCloudDB(newUser)
                    DatabaseService.getInstance(context).addUserToDB(newUser)?.let {
                        SharedPrefUtil.addUserId(it.id)
                    }
                    _emailPassSignUpStatus.value = true
                } else {
                    _emailPassSignUpStatus.value = false
                }
            }
        }
    }
}