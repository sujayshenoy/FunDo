package com.example.fundo.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.example.fundo.models.DBUser

object SharedPrefUtil {
    private lateinit var sharedPreferences: SharedPreferences

    fun initSharedPref(context: Context) {
       sharedPreferences = context.getSharedPreferences("FundoShaaredPref",Context.MODE_PRIVATE)
    }

    fun addString(key:String,value:String){
        val editor = sharedPreferences.edit()
        editor.putString(key,value)
        editor.apply()
    }

    fun getString(key:String):String? = sharedPreferences.getString(key, key)

    fun clearAll() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    fun removeString(key:String){
        val editor = sharedPreferences.edit()
        editor.remove(key)
        editor.apply()
    }
}