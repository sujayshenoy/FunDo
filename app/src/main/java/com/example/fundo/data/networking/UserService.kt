package com.example.fundo.data.networking

import com.example.fundo.data.models.CloudDBUser
import com.example.fundo.data.networking.users.StringField
import com.example.fundo.data.networking.users.UserAddDocument
import com.example.fundo.data.networking.users.UserField
import com.example.fundo.data.networking.users.UsersApi
import com.example.fundo.data.wrappers.User

object UserService {
    private val retrofit = RetrofitClient.createRetrofit()
    private val usersApi: UsersApi = retrofit.create(UsersApi::class.java)

    suspend fun getUsers(): ArrayList<CloudDBUser> {
        val userResponse = usersApi.getUsers()
        val userList: ArrayList<CloudDBUser> = userResponse.documents.map {
            CloudDBUser(
                it.fields.name.stringValue,
                it.fields.email.stringValue,
                it.fields.phone.stringValue
            )
        } as ArrayList<CloudDBUser>

        return userList
    }

    suspend fun getUser(userId: String): User {
        val userResponse = usersApi.getUser(userId)
        val fields = userResponse.fields

        return User(
            fields.name.stringValue,
            fields.email.stringValue,
            fields.phone.stringValue,
            firebaseId = userId
        )
    }

    suspend fun addUser(user: User): User {
        val userField = UserField(
            name = StringField(user.name),
            email = StringField(user.email),
            phone = StringField(user.phone)
        )
        val userAddDocument = UserAddDocument(userField)

        usersApi.addUser(user.firebaseId, userAddDocument)
        return user
    }
}