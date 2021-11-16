package com.example.fundo.data.wrappers

data class User(
    val name: String,
    val email: String,
    val phone: String,
    var id: Long = 0,
    var firebaseId: String = ""
)