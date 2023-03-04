package ru.kozlovss.workingcontacts.data.userdata.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.kozlovss.workingcontacts.data.dto.PhotoModel
import ru.kozlovss.workingcontacts.data.dto.User
import ru.kozlovss.workingcontacts.data.postsdata.dto.Post
import ru.kozlovss.workingcontacts.data.userdata.dto.Token

interface UserRepository {

//    val me: Flow<User>

    suspend fun register(
        login: String,
        password: String,
        name: String,
        photoModel: PhotoModel?
    ): Token

    suspend fun login(login: String, password: String): Token

    fun saveTokenOfUser(id: Long, token: String)

    fun clearTokenOfUser()

//    fun getMyData(token: Token)
}