package ru.kozlovss.workingcontacts.data.postsdata.repository

import androidx.paging.*
import kotlinx.coroutines.flow.map
import ru.kozlovss.workingcontacts.data.extensions.checkAndGetBody
import ru.kozlovss.workingcontacts.data.postsdata.api.PostApiService
import ru.kozlovss.workingcontacts.data.postsdata.dao.PostDao
import ru.kozlovss.workingcontacts.data.postsdata.db.PostDb
import ru.kozlovss.workingcontacts.entity.Post
import ru.kozlovss.workingcontacts.data.postsdata.dao.PostRemoteKeyDao
import ru.kozlovss.workingcontacts.entity.PostRequest
import ru.kozlovss.workingcontacts.data.postsdata.entity.PostEntity
import ru.kozlovss.workingcontacts.domain.repository.PostRepository
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val dao: PostDao,
    private val apiService: PostApiService,
    remoteKeyDao: PostRemoteKeyDao,
    db: PostDb
) : PostRepository {

    @OptIn(ExperimentalPagingApi::class)
    override val posts = Pager(
        config = PagingConfig(
            pageSize = 10,
            enablePlaceholders = false
        ),
        pagingSourceFactory = dao::pagingSource,
        remoteMediator = PostRemoteMediator(
            apiService,
            dao,
            remoteKeyDao,
            db
        )
    ).flow.map { it.map(PostEntity::toDto) }

    override suspend fun getById(id: Long) = apiService.getPostById(id).checkAndGetBody()
    override suspend fun likeById(id: Long) = apiService.likePostById(id).checkAndGetBody()
    override suspend fun dislikeById(id: Long) = apiService.dislikePostById(id).checkAndGetBody()
    override suspend fun removeById(id: Long) = apiService.deletePostById(id).checkAndGetBody()
    override suspend fun save(post: PostRequest) = apiService.savePost(post).checkAndGetBody()
    override suspend fun switchAudioPlayer(post: Post, audioPlayerState: Boolean) {
        val newPost = post.copy(
            isPaying = audioPlayerState
        )
        dao.update(PostEntity.fromDto(newPost))
    }
}