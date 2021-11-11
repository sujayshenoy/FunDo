package com.example.fundo.interfaces

import android.graphics.Bitmap

interface CloudStorage {
    fun addUserAvatar(bitmap: Bitmap, callback:(Boolean) -> Unit )
    fun getUserAvatar(callback: (Bitmap?) -> Unit )
}