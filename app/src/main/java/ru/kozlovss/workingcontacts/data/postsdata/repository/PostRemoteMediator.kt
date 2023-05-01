package ru.kozlovss.workingcontacts.data.postsdata.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import ru.kozlovss.workingcontacts.data.extensions.checkAndGetBody
import ru.kozlovss.workingcontacts.data.postsdata.api.PostApiService
import ru.kozlovss.workingcontacts.data.postsdata.dao.PostDao
import ru.kozlovss.workingcontacts.data.postsdata.db.PostDb
import ru.kozlovss.workingcontacts.data.postsdata.dao.PostRemoteKeyDao
import ru.kozlovss.workingcontacts.data.postsdata.entity.PostEntity
import ru.kozlovss.workingcontacts.data.postsdata.entity.PostRemoteKeyEntity
import ru.kozlovss.workingcontacts.data.postsdata.entity.toEntity

@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediator(
    private val apiService: PostApiService,
    private val dao: PostDao,
    private val remoteKeyDao: PostRemoteKeyDao,
    private val db: PostDb
) : RemoteMediator<Int, PostEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostEntity>
    ): MediatorResult {
        try {
            val response = when (loadType) {
                LoadType.REFRESH -> {
                    remoteKeyDao.max()?.let {
                        apiService.getPostsAfter(it, state.config.pageSize)
                    } ?: apiService.getLatestPosts(state.config.initialLoadSize)
                }
                LoadType.PREPEND -> {
                    return MediatorResult.Success(true)
                }
                LoadType.APPEND -> {
                    val id = remoteKeyDao.min() ?: return MediatorResult.Success(false)
                    apiService.getPostsBefore(id, state.config.pageSize)
                }
            }.checkAndGetBody()

            db.withTransaction {
                when (loadType) {
                    LoadType.REFRESH -> {
                        if (remoteKeyDao.isEmpty()) {
                            remoteKeyDao.clear()
                            remoteKeyDao.insert(
                                listOf(
                                    PostRemoteKeyEntity(
                                        type = PostRemoteKeyEntity.KeyType.AFTER,
                                        id = response.first().id,
                                    ),
                                    PostRemoteKeyEntity(
                                        type = PostRemoteKeyEntity.KeyType.BEFORE,
                                        id = response.last().id,
                                    ),
                                )
                            )
                            dao.clear()
                        } else {
                            remoteKeyDao.insert(
                                PostRemoteKeyEntity(
                                    type = PostRemoteKeyEntity.KeyType.AFTER,
                                    id = response.first().id,
                                )
                            )
                        }
                    }
                    LoadType.APPEND -> {
                        remoteKeyDao.insert(
                            PostRemoteKeyEntity(
                                type = PostRemoteKeyEntity.KeyType.BEFORE,
                                id = response.last().id,
                            )
                        )
                    }
                    else -> Unit
                }
                dao.insert(response.toEntity())
            }
            return MediatorResult.Success(endOfPaginationReached = response.isEmpty())
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }

    override suspend fun initialize() =
        if (dao.isEmpty()) {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        } else {
            InitializeAction.SKIP_INITIAL_REFRESH
        }
}