package ru.kozlovss.workingcontacts.data.userdata.repository

import ru.kozlovss.workingcontacts.data.dto.PhotoModel
import ru.kozlovss.workingcontacts.data.dto.User
import ru.kozlovss.workingcontacts.data.userdata.dto.Token

interface UserRepository {
    suspend fun register(
        login: String,
        password: String,
        name: String,
        photoModel: PhotoModel?
    ): Token

    suspend fun login(login: String, password: String): Token

    suspend fun saveTokenOfUser(token: Token)

    suspend fun getMyData(id: Long): User

    suspend fun clearTokenOfUser()

    suspend fun getUserInfoById(id: Long): User
}