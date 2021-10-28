package com.example.fundo.services

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

object StorageService {
    private val storageRef = Firebase.storage.reference
    private val imagesRef = storageRef.child("images")

    fun addUserAvatar(bitmap: Bitmap, callback:(Boolean) -> Unit ){
        val userImagesRef = imagesRef.child("users").child(AuthService.getCurrentUser()?.uid.toString()).child("avatar.webp")
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

    fun getUserAvatar(callback: (Bitmap?) -> Unit ) {
        val userImagesRef = imagesRef.child("users").child(AuthService.getCurrentUser()?.uid.toString()).child("avatar.webp")
        userImagesRef.getBytes(5000000).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeByteArray(it,0,it.size)
            Log.d("Storage","fetched image from storage for ${AuthService.getCurrentUser()?.uid}")
            callback(bitmap)
        }.addOnFailureListener {
            Log.e("Firebase Storage",it.message.toString())
            callback(null)
        }
    }
}