package ru.kozlovss.workingcontacts.data.postsdata.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.kozlovss.workingcontacts.data.dto.PhotoModel
import ru.kozlovss.workingcontacts.data.postsdata.dto.Post

interface PostRepository {
    val posts: Flow<PagingData<Post>>
    suspend fun getById(id: Long): Post?
    suspend fun likeById(id: Long)
    suspend fun removeById(id: Long)
    suspend fun save(post: Post)
    suspend fun saveWithAttachment(post: Post, photo: PhotoModel)
    suspend fun switchAudioPlayer(post: Post, audioPlayerState: Boolean)
    suspend fun stopAudioPlayer()
}