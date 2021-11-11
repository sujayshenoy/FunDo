package com.example.fundo.data.wrappers

import com.example.fundo.auth.services.FirebaseAuthService

data class User(val name:String, val email:String, val phone:String,
                val loginStatus:Boolean = FirebaseAuthService.getCurrentUser()!=null)