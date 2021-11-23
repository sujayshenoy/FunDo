package com.example.fundo.data.models

import java.util.*

data class CloudDBNote(
    val title: String,
    val content: String,
    val lastModified: String,
    val archived: Boolean = false,
    val reminder: String = ""
)