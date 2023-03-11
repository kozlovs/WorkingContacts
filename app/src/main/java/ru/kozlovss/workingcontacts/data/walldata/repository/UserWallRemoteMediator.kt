package ru.kozlovss.workingcontacts.data.walldata.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import ru.kozlovss.workingcontacts.data.postsdata.entity.PostEntity
import ru.kozlovss.workingcontacts.data.postsdata.entity.PostRemoteKeyEntity
import ru.kozlovss.workingcontacts.data.postsdata.entity.toEntity
import ru.kozlovss.workingcontacts.data.walldata.api.UserWallApiService
import ru.kozlovss.workingcontacts.data.walldata.dao.UserWallDao
import ru.kozlovss.workingcontacts.data.walldata.dao.UserWallRemoteKeyDao
import ru.kozlovss.workingcontacts.data.walldata.db.UserWallDb
import ru.kozlovss.workingcontacts.domain.error.ApiError

@OptIn(ExperimentalPagingApi::class)
class UserWallRemoteMediator(
    private val apiService: UserWallApiService,
    private val dao: UserWallDao,
    private val remoteKeyDao: UserWallRemoteKeyDao,
    private val db: UserWallDb,
    private val userId: Long?
    ) : RemoteMediator<Int, PostEntity>() {

        override suspend fun load(
            loadType: LoadType,
            state: PagingState<Int, PostEntity>
        ): MediatorResult {
            try {
                if (userId == null) throw Exception("User id is null")
                val response = when (loadType) {
                    LoadType.REFRESH -> {
                        remoteKeyDao.max()?.let {
                            apiService.getWallPostsAfterByUserId(userId, it, state.config.pageSize)
                        } ?: apiService.getWallLatestByUserId(userId, state.config.initialLoadSize)
                    }
                    LoadType.PREPEND -> {
                        return MediatorResult.Success(true)
                    }
                    LoadType.APPEND -> {
                        val postId = remoteKeyDao.min() ?: return MediatorResult.Success(false)
                        apiService.getWallPostsBeforeByUserId(userId, postId, state.config.pageSize)
                    }
                }

                if (!response.isSuccessful) {
                    throw ApiError(response.code(), response.message())
                }
                val body = response.body() ?: throw ApiError(
                    response.code(),
                    response.message(),
                )

                db.withTransaction {
                    when (loadType) {
                        LoadType.REFRESH -> {
                            if (remoteKeyDao.isEmpty()) {
                                remoteKeyDao.clear()
                                remoteKeyDao.insert(
                                    listOf(
                                        PostRemoteKeyEntity(
                                            type = PostRemoteKeyEntity.KeyType.AFTER,
                                            id = body.first().id,
                                        ),
                                        PostRemoteKeyEntity(
                                            type = PostRemoteKeyEntity.KeyType.BEFORE,
                                            id = body.last().id,
                                        ),
                                    )
                                )
                                dao.clear()
                            } else {
                                remoteKeyDao.insert(
                                    PostRemoteKeyEntity(
                                        type = PostRemoteKeyEntity.KeyType.AFTER,
                                        id = body.first().id,
                                    )
                                )
                            }
                        }
                        LoadType.APPEND -> {
                            remoteKeyDao.insert(
                                PostRemoteKeyEntity(
                                    type = PostRemoteKeyEntity.KeyType.BEFORE,
                                    id = body.last().id,
                                )
                            )
                        }
                        else -> Unit
                    }
                    dao.insert(body.toEntity())
                }
                return MediatorResult.Success(endOfPaginationReached = body.isEmpty())
            } catch (e: Exception) {
                return MediatorResult.Error(e)
            }
        }

        override suspend fun initialize(): InitializeAction =
            if (dao.isEmpty()) {
                InitializeAction.LAUNCH_INITIAL_REFRESH
            } else {
                InitializeAction.SKIP_INITIAL_REFRESH
            }
}