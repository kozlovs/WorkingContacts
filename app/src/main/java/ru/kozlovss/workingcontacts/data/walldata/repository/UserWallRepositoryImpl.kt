package ru.kozlovss.workingcontacts.data.walldata.repository

import androidx.paging.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.Response
import ru.kozlovss.workingcontacts.data.postsdata.api.PostApiService
import ru.kozlovss.workingcontacts.data.postsdata.dto.Post
import ru.kozlovss.workingcontacts.data.postsdata.entity.PostEntity
import ru.kozlovss.workingcontacts.data.walldata.api.UserWallApiService
import ru.kozlovss.workingcontacts.data.walldata.dao.UserWallDao
import ru.kozlovss.workingcontacts.data.walldata.dao.UserWallRemoteKeyDao
import ru.kozlovss.workingcontacts.data.walldata.db.UserWallDb
import ru.kozlovss.workingcontacts.domain.error.ApiError
import ru.kozlovss.workingcontacts.domain.error.NetworkError
import java.io.IOException
import javax.inject.Inject

class UserWallRepositoryImpl @Inject constructor(
    private val dao: UserWallDao,
    val wallApiService: UserWallApiService,
    private val postApiService: PostApiService,
    val remoteKeyDao: UserWallRemoteKeyDao,
    val db: UserWallDb
) : UserWallRepository {

    override var userId: Long? = null

    @OptIn(ExperimentalPagingApi::class)
    override var posts: Flow<PagingData<Post>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        pagingSourceFactory = dao::pagingSource,
        remoteMediator = UserWallRemoteMediator(
            wallApiService,
            dao,
            remoteKeyDao,
            db,
            userId
        )
    ).flow.map { it.map(PostEntity::toDto) }

    override suspend fun getById(id: Long): Post {
        try {
            val response = postApiService.getPostById(id)
            return checkResponse(response)
        } catch (e: IOException) {
            throw NetworkError()
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun likeById(id: Long) {
        val post = getById(id)
        dao.likeById(id)
        if (post.likedByMe) {
            makeRequestDislikeById(id)
        } else {
            makeRequestLikeById(id)
        }
    }

    override fun clearData() {
        userId = null
    }

    @OptIn(ExperimentalPagingApi::class)
    override suspend fun getUserPosts(id: Long) {
        userId = id
        remoteKeyDao.clear()
        dao.clear()
        posts = Pager(
            config = PagingConfig(pageSize = 10, enablePlaceholders = false),
            pagingSourceFactory = dao::pagingSource,
            remoteMediator = UserWallRemoteMediator(
                wallApiService,
                dao,
                remoteKeyDao,
                db,
                id
            )
        ).flow.map { it.map(PostEntity::toDto) }
    }

    private suspend fun makeRequestLikeById(id: Long) {
        try {
            val response = postApiService.likePostById(id)
            val body = checkResponse(response)
            dao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError()
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    private suspend fun makeRequestDislikeById(id: Long) {
        try {
            val response = postApiService.dislikePostById(id)
            val body = checkResponse(response)
            dao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError()
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    private fun <T> checkResponse(response: Response<T>): T {
        if (!response.isSuccessful) throw ApiError(response.code(), response.message())
        return response.body() ?: throw ApiError(response.code(), response.message())
    }
}