package com.example.fundo.data.networking.users

data class UserDocument(
    val name : String,
    val fields: UserField,
    val createTime: String,
    val updateTime: String
)
