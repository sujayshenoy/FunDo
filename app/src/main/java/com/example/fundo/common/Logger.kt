package com.example.fundo.common

import android.util.Log
import com.example.fundo.interfaces.LoggerInterface

object Logger : LoggerInterface {
    private const val DB_LOG_TAG = "Database"
    private const val AUTH_LOG_TAG = "Authentication"
    private const val LOG_TAG = "Custom Log"
    private const val STORAGE_LOG_TAG = "Storage"
    private const val NETWORK_LOG_TAG = "Network"

    override fun logDbError(message: String) {
        Log.e(DB_LOG_TAG,message)
    }

    override fun logDbInfo(message: String) {
        Log.i(DB_LOG_TAG,message)
    }

    override fun logAuthError(message: String) {
        Log.e(AUTH_LOG_TAG,message)
    }

    override fun logAuthInfo(message: String) {
        Log.i(AUTH_LOG_TAG,message)
    }

    override fun logStorageError(message: String) {
        Log.e(STORAGE_LOG_TAG,message)
    }

    override fun logStorageInfo(message: String) {
        Log.i(STORAGE_LOG_TAG,message)
    }

    override fun logError(message: String) {
        Log.e(LOG_TAG,message)
    }

    override fun logInfo(message: String) {
        Log.i(LOG_TAG,message)
    }

    override fun logNetworkError(message: String) {
        Log.e(NETWORK_LOG_TAG,message)
    }

    override fun logNetworkInfo(message: String) {
        Log.i(NETWORK_LOG_TAG,message)
    }

}