package ru.kozlovss.workingcontacts.data.userdata.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import ru.kozlovss.workingcontacts.data.dto.PhotoModel
import ru.kozlovss.workingcontacts.data.dto.User
import ru.kozlovss.workingcontacts.data.userdata.dto.Token

interface UserRepository {

    val myData: MutableStateFlow<User?>
    val userData: MutableStateFlow<User?>

    suspend fun register(
        login: String,
        password: String,
        name: String,
        photoModel: PhotoModel?
    ): Token

    suspend fun login(login: String, password: String): Token

    suspend fun saveTokenOfUser(id: Long, token: String)

    suspend fun clearTokenOfUser()
}