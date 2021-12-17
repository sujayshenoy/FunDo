package com.example.fundo.data.networking.users

import retrofit2.http.*

interface UsersApi {

    @GET("projects/fundo-ac81f/databases/(default)/documents/users")
    suspend fun getUsers(): FirebaseUserResponse

    @GET("projects/fundo-ac81f/databases/(default)/documents/users/{userid}")
    suspend fun getUser(@Path("userid") userId: String) : UserDocument

    @POST("projects/fundo-ac81f/databases/(default)/documents/users")
    suspend fun addUser(@Query("documentId") userId: String, @Body userDocument: UserAddDocument)
}