package com.example.fundo.data.models

data class CloudDBNote(
    val title: String,
    val content: String,
    val lastModified: String,
    val archived: Boolean = false
)