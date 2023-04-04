package ru.kozlovss.workingcontacts.data.userdata.repository

import ru.kozlovss.workingcontacts.data.dto.MediaModel
import ru.kozlovss.workingcontacts.data.dto.User
import ru.kozlovss.workingcontacts.data.userdata.dto.Token

interface UserRepository {
    suspend fun register(
        login: String,
        password: String,
        name: String,
        mediaModel: MediaModel?
    ): Token

    suspend fun login(login: String, password: String): Token

    suspend fun saveTokenOfUser(token: Token)

    suspend fun getMyData(id: Long): User

    suspend fun clearTokenOfUser()

    suspend fun getUserInfoById(id: Long): User
    suspend fun getUsers(): List<User>
}