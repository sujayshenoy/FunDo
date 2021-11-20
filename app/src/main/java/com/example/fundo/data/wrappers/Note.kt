package com.example.fundo.data.wrappers

import java.util.*

data class Note(
    var title: String,
    var content: String,
    var id: Long = 0,
    var firebaseId: String = "",
    var lastModified: Date? = null,
    var archived: Boolean = false
)