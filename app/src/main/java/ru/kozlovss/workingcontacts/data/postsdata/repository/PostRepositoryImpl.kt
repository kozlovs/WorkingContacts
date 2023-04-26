package ru.kozlovss.workingcontacts.data.postsdata.repository

import androidx.paging.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.kozlovss.workingcontacts.data.postsdata.api.PostApiService
import ru.kozlovss.workingcontacts.data.postsdata.dao.PostDao
import ru.kozlovss.workingcontacts.data.postsdata.db.PostDb
import ru.kozlovss.workingcontacts.data.postsdata.dto.Post
import ru.kozlovss.workingcontacts.data.postsdata.dao.PostRemoteKeyDao
import ru.kozlovss.workingcontacts.data.postsdata.dto.PostRequest
import ru.kozlovss.workingcontacts.data.postsdata.entity.PostEntity
import ru.kozlovss.workingcontacts.domain.util.ResponseChecker
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val dao: PostDao,
    private val apiService: PostApiService,
    remoteKeyDao: PostRemoteKeyDao,
    db: PostDb
) : PostRepository {

    @OptIn(ExperimentalPagingApi::class)
    override val posts: Flow<PagingData<Post>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        pagingSourceFactory = dao::pagingSource,
        remoteMediator = PostRemoteMediator(
            apiService,
            dao,
            remoteKeyDao,
            db
        )
    ).flow.map { it.map(PostEntity::toDto) }

    override suspend fun getById(id: Long): Post {
        val response = apiService.getPostById(id)
        return ResponseChecker.check(response)
    }

    override suspend fun likeById(id: Long): Post {
        val response = apiService.likePostById(id)
        return ResponseChecker.check(response)
    }

    override suspend fun dislikeById(id: Long): Post {
        val response = apiService.dislikePostById(id)
        return ResponseChecker.check(response)
    }

    override suspend fun removeById(id: Long) {
        val response = apiService.deletePostById(id)
        ResponseChecker.check(response)
    }

    override suspend fun save(post: PostRequest): Post {
        val response = apiService.savePost(post)
        return ResponseChecker.check(response)
    }

    override suspend fun switchAudioPlayer(post: Post, audioPlayerState: Boolean) {
        val newPost = post.copy(
            isPaying = audioPlayerState
        )
        dao.update(PostEntity.fromDto(newPost))
    }
}