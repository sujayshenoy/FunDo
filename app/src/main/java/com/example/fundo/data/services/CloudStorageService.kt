package com.example.fundo.data.services

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.fundo.auth.services.FirebaseAuthService
import com.example.fundo.common.Logger
import com.example.fundo.interfaces.CloudStorage
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import kotlin.coroutines.suspendCoroutine

object CloudStorageService : CloudStorage {
    private val storageRef = Firebase.storage.reference
    private val imagesRef = storageRef.child("images")

    override suspend fun setUserAvatar(bitmap: Bitmap): Boolean {
        val userImagesRef = imagesRef.child("users")
            .child(FirebaseAuthService.getCurrentUser()?.uid.toString()).child("avatar.webp")
        val baos = ByteArrayOutputStream()

        return suspendCoroutine {
            bitmap.compress(Bitmap.CompressFormat.WEBP, 80, baos)
            val data = baos.toByteArray()

            val uploadTask = userImagesRef.putBytes(data)
            uploadTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    it.resumeWith(Result.success(true))
                } else {
                    Logger.logStorageError("FirebaseStorage: Add user avatar failed")
                    it.resumeWith(Result.failure(task.exception!!))
                }
            }
        }
    }

    override suspend fun getUserAvatar(): Bitmap {
        val userImagesRef = imagesRef.child("users")
            .child(FirebaseAuthService.getCurrentUser()?.uid.toString()).child("avatar.webp")

        return suspendCoroutine {
            userImagesRef.getBytes(5000000).addOnSuccessListener { byteArray ->
                val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                Logger.logStorageInfo("fetched image from storage for ${FirebaseAuthService.getCurrentUser()?.uid}")
                it.resumeWith(Result.success(bitmap))
            }.addOnFailureListener { exception ->
                Logger.logStorageError("FirebaseStorage: Add user avatar failed")
                it.resumeWith(Result.failure(exception))
            }
        }
    }
}