package ru.kozlovss.workingcontacts.data.postsdata.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.kozlovss.workingcontacts.data.dto.MediaModel
import ru.kozlovss.workingcontacts.data.postsdata.dto.Post
import ru.kozlovss.workingcontacts.data.postsdata.dto.PostRequest

interface PostRepository {
    val posts: Flow<PagingData<Post>>
    suspend fun getById(id: Long): Post?
    suspend fun likeById(id: Long)
    suspend fun removeById(id: Long)
    suspend fun save(post: PostRequest, model: MediaModel?)
    suspend fun switchAudioPlayer(post: Post, audioPlayerState: Boolean)
    suspend fun stopAudioPlayer()
}