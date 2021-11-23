package com.example.fundo.common

import android.content.Context
import android.content.SharedPreferences

class SharedPrefUtil(private val context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("FundoShaaredPref", Context.MODE_PRIVATE)

    companion object {
        private val instance: SharedPrefUtil? by lazy { null }
        fun getInstance(context: Context) = instance ?: SharedPrefUtil(context)
    }

    fun addString(key: String, value: String) {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun addUserId(userID: Long) {
        val editor = sharedPreferences.edit()
        editor.putLong("userid", userID)
        editor.apply()
    }

    fun getString(key: String): String? = sharedPreferences.getString(key, key)

    fun getUserId() = sharedPreferences.getLong("userid", 0L)

    fun removeString(key: String) {
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