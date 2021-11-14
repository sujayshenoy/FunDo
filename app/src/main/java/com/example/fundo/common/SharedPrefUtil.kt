package com.example.fundo.common

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

    fun addUserId(userID:Long){
        val editor = sharedPreferences.edit()
        editor.putLong("userid",userID)
        editor.apply()
    }

    fun getString(key:String):String? = sharedPreferences.getString(key, key)

    fun getUserId() = sharedPreferences.getLong("userid", 0L)

    fun removeString(key:String){
        val editor = sharedPreferences.edit()
        editor.remove(key)
        editor.apply()
    }

    fun clearAll() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}