package com.example.fundo.models

import com.example.fundo.services.Auth

data class User(val name:String, val email:String, val phone:String,val loginStatus:Boolean = Auth.getCurrentUser()!=null)