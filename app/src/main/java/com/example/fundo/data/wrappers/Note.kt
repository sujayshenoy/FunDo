package com.example.fundo.data.wrappers

import java.util.*
import kotlin.collections.ArrayList

data class Note(
    var title: String,
    var content: String,
    var id: Long = 0,
    var firebaseId: String = "",
    var lastModified: Date? = null,
    var archived: Boolean = false,
    var reminder: Date? = null,
)