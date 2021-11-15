package com.example.fundo.interfaces

interface LoggerInterface {
    fun logError(message:String)
    fun logInfo(message:String)
    fun logNetworkError(message: String)
    fun logNetworkInfo(message: String)
    fun logDbError(message:String)
    fun logDbInfo(message:String)
    fun logAuthError(message:String)
    fun logAuthInfo(message:String)
    fun logStorageError(message: String)
    fun logStorageInfo(message: String)
}