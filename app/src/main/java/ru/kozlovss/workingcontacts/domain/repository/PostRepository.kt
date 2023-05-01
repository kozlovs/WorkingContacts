package ru.kozlovss.workingcontacts.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.kozlovss.workingcontacts.entity.Post
import ru.kozlovss.workingcontacts.entity.PostRequest

interface PostRepository {
    val posts: Flow<PagingData<Post>>
    suspend fun getById(id: Long): Post
    suspend fun likeById(id: Long): Post
    suspend fun dislikeById(id: Long): Post
    suspend fun removeById(id: Long)
    suspend fun save(post: PostRequest) : Post
    suspend fun switchAudioPlayer(post: Post, audioPlayerState: Boolean)
}