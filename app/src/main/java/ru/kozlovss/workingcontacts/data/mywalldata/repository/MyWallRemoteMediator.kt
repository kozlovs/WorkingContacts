package ru.kozlovss.workingcontacts.data.mywalldata.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import ru.kozlovss.workingcontacts.data.extensions.checkAndGetBody
import ru.kozlovss.workingcontacts.data.mywalldata.api.MyWallApiService
import ru.kozlovss.workingcontacts.data.mywalldata.dao.MyWallDao
import ru.kozlovss.workingcontacts.data.mywalldata.dao.MyWallRemoteKeyDao
import ru.kozlovss.workingcontacts.data.mywalldata.db.MyWallDb
import ru.kozlovss.workingcontacts.data.postsdata.entity.PostEntity
import ru.kozlovss.workingcontacts.data.postsdata.entity.PostRemoteKeyEntity
import ru.kozlovss.workingcontacts.data.postsdata.entity.toEntity


@OptIn(ExperimentalPagingApi::class)
class MyWallRemoteMediator(
    private val apiService: MyWallApiService,
    private val dao: MyWallDao,
    private val remoteKeyDao: MyWallRemoteKeyDao,
    private val db: MyWallDb
) : RemoteMediator<Int, PostEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostEntity>
    ): MediatorResult {
        try {
            val response = when (loadType) {
                LoadType.REFRESH -> {
                    remoteKeyDao.max()?.let {
                        apiService.getMyWallPostsAfter(it, state.config.pageSize)
                    } ?: apiService.getMyWallLatestPosts(state.config.initialLoadSize)
                }
                LoadType.PREPEND -> {
                    return MediatorResult.Success(true)
                }
                LoadType.APPEND -> {
                    val id = remoteKeyDao.min() ?: return MediatorResult.Success(false)
                    apiService.getMyWallPostsBefore(id, state.config.pageSize)
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

    override suspend fun initialize(): InitializeAction =
        if (dao.isEmpty()) {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        } else {
            InitializeAction.SKIP_INITIAL_REFRESH
        }
}