package ru.kozlovss.workingcontacts.data.mywalldata.repository

import androidx.paging.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.kozlovss.workingcontacts.data.mywalldata.api.MyWallApiService
import ru.kozlovss.workingcontacts.data.mywalldata.dao.MyWallDao
import ru.kozlovss.workingcontacts.data.mywalldata.dao.MyWallRemoteKeyDao
import ru.kozlovss.workingcontacts.data.mywalldata.db.MyWallDb
import ru.kozlovss.workingcontacts.data.postsdata.api.PostApiService
import ru.kozlovss.workingcontacts.data.postsdata.dao.PostDao
import ru.kozlovss.workingcontacts.data.postsdata.dto.Post
import ru.kozlovss.workingcontacts.data.postsdata.entity.PostEntity
import ru.kozlovss.workingcontacts.domain.error.NetworkError
import ru.kozlovss.workingcontacts.domain.util.ResponseChecker
import java.io.IOException
import javax.inject.Inject

class MyWallRepositoryImpl @Inject constructor(
    private val dao: MyWallDao,
    private val mainDao: PostDao,
    wallApiService: MyWallApiService,
    private val postApiService: PostApiService,
    remoteKeyDao: MyWallRemoteKeyDao,
    db: MyWallDb
) : MyWallRepository {

    @OptIn(ExperimentalPagingApi::class)
    override val posts: Flow<PagingData<Post>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        pagingSourceFactory = dao::pagingSource,
        remoteMediator = MyWallRemoteMediator(
            wallApiService,
            dao,
            remoteKeyDao,
            db
        )
    ).flow.map { it.map(PostEntity::toDto) }

    override suspend fun getById(id: Long): Post {
        try {
            val response = postApiService.getPostById(id)
            return ResponseChecker.check(response)
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

    private suspend fun makeRequestLikeById(id: Long) {
        try {
            val response = postApiService.likePostById(id)
            val body = ResponseChecker.check(response)
            dao.insert(PostEntity.fromDto(body))
            mainDao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError()
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    private suspend fun makeRequestDislikeById(id: Long) {
        try {
            val response = postApiService.dislikePostById(id)
            val body = ResponseChecker.check(response)
            dao.insert(PostEntity.fromDto(body))
            mainDao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError()
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun removeById(id: Long) {
        try {
            dao.removeById(id)
            mainDao.removeById(id)
            val response = postApiService.deletePostById(id)
            ResponseChecker.check(response)
        } catch (e: IOException) {
            throw NetworkError()
        } catch (e: Exception) {
            throw UnknownError()
        }
    }
}