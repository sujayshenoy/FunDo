package com.example.fundo.data.services

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.fundo.auth.services.FirebaseAuthService
import com.example.fundo.common.Logger
import com.example.fundo.interfaces.CloudStorage
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

object StorageService : CloudStorage {
    private val storageRef = Firebase.storage.reference
    private val imagesRef = storageRef.child("images")

    override fun addUserAvatar(bitmap: Bitmap, callback:(Boolean) -> Unit ){
        val userImagesRef = imagesRef.child("users")
            .child(FirebaseAuthService.getCurrentUser()?.uid.toString()).child("avatar.webp")
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.WEBP,80,baos)
        val data = baos.toByteArray()

        val uploadTask = userImagesRef.putBytes(data)
        uploadTask.addOnCompleteListener{
            if(it.isSuccessful){
                callback(true)
            }
            else{
                callback(false)
            }
        }
    }

    override fun getUserAvatar(callback: (Bitmap?) -> Unit ) {
        val userImagesRef = imagesRef.child("users")
            .child(FirebaseAuthService.getCurrentUser()?.uid.toString()).child("avatar.webp")
        userImagesRef.getBytes(5000000).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeByteArray(it,0,it.size)
            Logger.logStorageInfo("fetched image from storage for ${FirebaseAuthService.getCurrentUser()?.uid}")
            callback(bitmap)
        }.addOnFailureListener {
            Logger.logStorageError(it.message.toString())
            callback(null)
        }
    }
}