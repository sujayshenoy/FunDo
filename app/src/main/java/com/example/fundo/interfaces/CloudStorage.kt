package com.example.fundo.interfaces

import android.graphics.Bitmap

interface CloudStorage {
    suspend fun setUserAvatar(bitmap: Bitmap): Boolean
    suspend fun getUserAvatar(): Bitmap
}