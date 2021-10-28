package com.example.fundo.utils

import android.content.Context
import android.content.SharedPreferences

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

    fun getUserName():String? = sharedPreferences.getString("userName","Name")

    fun getUserEmail():String? = sharedPreferences.getString("userEmail","Email")

    fun getUserPhone():String? = sharedPreferences.getString("userPhone","Phone")

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