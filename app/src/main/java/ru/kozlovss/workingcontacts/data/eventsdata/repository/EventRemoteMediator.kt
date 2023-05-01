package ru.kozlovss.workingcontacts.data.eventsdata.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import ru.kozlovss.workingcontacts.data.eventsdata.api.EventApiService
import ru.kozlovss.workingcontacts.data.eventsdata.dao.EventDao
import ru.kozlovss.workingcontacts.data.eventsdata.dao.EventRemoteKeyDao
import ru.kozlovss.workingcontacts.data.eventsdata.db.EventDb
import ru.kozlovss.workingcontacts.data.eventsdata.entity.EventEntity
import ru.kozlovss.workingcontacts.data.eventsdata.entity.EventRemoteKeyEntity
import ru.kozlovss.workingcontacts.data.eventsdata.entity.toEntity
import ru.kozlovss.workingcontacts.data.extensions.checkAndGetBody

@OptIn(ExperimentalPagingApi::class)
class EventRemoteMediator(
    private val apiService: EventApiService,
    private val dao: EventDao,
    private val remoteKeyDao: EventRemoteKeyDao,
    private val db: EventDb
) : RemoteMediator<Int, EventEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, EventEntity>
    ): MediatorResult {
        try {
            val response = when (loadType) {
                LoadType.REFRESH -> {
                    remoteKeyDao.max()?.let {
                        apiService.getEventsAfter(it, state.config.pageSize)
                    } ?: apiService.getLatestEvents(state.config.initialLoadSize)
                }

                LoadType.PREPEND -> {
                    return MediatorResult.Success(true)
                }

                LoadType.APPEND -> {
                    val id = remoteKeyDao.min() ?: return MediatorResult.Success(false)
                    apiService.getEventsBefore(id, state.config.pageSize)
                }
            }.checkAndGetBody()

            db.withTransaction {
                when (loadType) {
                    LoadType.REFRESH -> {
                        if (remoteKeyDao.isEmpty()) {
                            remoteKeyDao.clear()
                            remoteKeyDao.insert(
                                listOf(
                                    EventRemoteKeyEntity(
                                        type = EventRemoteKeyEntity.KeyType.AFTER,
                                        id = response.first().id,
                                    ),
                                    EventRemoteKeyEntity(
                                        type = EventRemoteKeyEntity.KeyType.BEFORE,
                                        id = response.last().id,
                                    ),
                                )
                            )
                            dao.clear()
                        } else {
                            remoteKeyDao.insert(
                                EventRemoteKeyEntity(
                                    type = EventRemoteKeyEntity.KeyType.AFTER,
                                    id = response.first().id,
                                )
                            )
                        }
                    }

                    LoadType.APPEND -> {
                        remoteKeyDao.insert(
                            EventRemoteKeyEntity(
                                type = EventRemoteKeyEntity.KeyType.BEFORE,
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