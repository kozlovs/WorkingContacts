package ru.kozlovss.workingcontacts.data.repository.userrepository

import ru.kozlovss.workingcontacts.data.dto.PhotoModel
import ru.kozlovss.workingcontacts.data.dto.Token

interface UserRepository {

    suspend fun register(
        login: String,
        password: String,
        name: String,
        photoModel: PhotoModel?
    ): Token

    suspend fun login(login: String, password: String): Token

    fun saveTokenOfUser(id: Long, token: String)

    fun clearTokenOfUser()
}