package ru.kozlovss.workingcontacts.domain.repository

import okhttp3.MultipartBody
import okhttp3.RequestBody
import ru.kozlovss.workingcontacts.data.userdata.dto.User
import ru.kozlovss.workingcontacts.data.userdata.dto.Token

interface UserRepository {
    suspend fun register(
        login: RequestBody,
        password: RequestBody,
        name: RequestBody,
        avatar: MultipartBody.Part?
    ): Token

    suspend fun login(login: String, password: String): Token
    suspend fun saveTokenOfUser(token: Token)
    suspend fun clearTokenOfUser()
    suspend fun getUserById(id: Long): User
    suspend fun getUsers(): List<User>
}