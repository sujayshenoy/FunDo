package com.example.fundo.data.wrappers

import java.io.Serializable
import java.util.*

data class Label(
    var name: String,
    var firebaseId: String = "",
    var lastModified: Date? = null
) : Serializable
