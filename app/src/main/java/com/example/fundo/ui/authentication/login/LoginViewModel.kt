package com.example.fundo.ui.authentication.login

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fundo.auth.services.FirebaseAuthService
import com.example.fundo.common.SharedPrefUtil
import com.example.fundo.data.services.DatabaseService
import com.facebook.AccessToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val _emailPassLoginStatus = MutableLiveData<Boolean>()
    val emailPassLoginStatus = _emailPassLoginStatus as LiveData<Boolean>

    private val _facebookLoginStatus = MutableLiveData<Boolean>()
    val facebookLoginStatus = _facebookLoginStatus as LiveData<Boolean>

    fun loginWithEmailAndPassword(context: Context, email: String, password: String) {
        FirebaseAuthService.signInWithEmailAndPassword(email, password) { user ->
            viewModelScope.launch(Dispatchers.IO) {
                if (user != null) {
                    DatabaseService.getInstance(context).addUserToDB(user)?.let {
                        SharedPrefUtil.getInstance(context).addUserId(it.id)
                        DatabaseService.getInstance(context).addCloudDataToLocalDB(it)
                    }
                    _emailPassLoginStatus.postValue(true)
                } else {
                    _emailPassLoginStatus.postValue(false)
                }
            }
        }
    }

    fun loginWithFacebook(context: Context, accessToken: AccessToken) {
        FirebaseAuthService.handleFacebookLogin(accessToken) { user ->
            viewModelScope.launch(Dispatchers.IO) {
                if (user != null) {
                    if (!DatabaseService.getInstance(context).checkUserInCloudDB(user.firebaseId)) {
                        DatabaseService.getInstance(context).addUserToCloudDB(user)
                    }
                    DatabaseService.getInstance(context).addUserToDB(user)?.let {
                        SharedPrefUtil.getInstance(context).addUserId(it.id)
                        DatabaseService.getInstance(context).addCloudDataToLocalDB(it)
                    }
                    _facebookLoginStatus.postValue(true)
                } else {
                    _facebookLoginStatus.postValue(false)
                }
            }
        }
    }
}