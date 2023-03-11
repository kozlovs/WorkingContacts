package ru.kozlovss.workingcontacts.data.walldata.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import ru.kozlovss.workingcontacts.data.postsdata.dto.Post

interface UserWallRepository {
    var userId: Long?
    val posts: Flow<PagingData<Post>>
    suspend fun getById(id: Long): Post?
    suspend fun likeById(id: Long)
    fun clearData()
}