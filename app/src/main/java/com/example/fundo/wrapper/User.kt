package com.example.fundo.wrapper

import com.example.fundo.services.AuthService

data class User(val name:String, val email:String, val phone:String,val loginStatus:Boolean = AuthService.getCurrentUser()!=null)