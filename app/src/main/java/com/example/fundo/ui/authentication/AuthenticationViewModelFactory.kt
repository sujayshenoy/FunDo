package com.example.fundo.ui.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AuthenticationViewModelFactory:ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AuthenticationViewModel() as T
    }
}