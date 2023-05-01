package ru.kozlovss.workingcontacts.data.eventsdata.repository

import androidx.paging.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.kozlovss.workingcontacts.data.eventsdata.api.EventApiService
import ru.kozlovss.workingcontacts.data.eventsdata.dao.EventDao
import ru.kozlovss.workingcontacts.data.eventsdata.dao.EventRemoteKeyDao
import ru.kozlovss.workingcontacts.data.eventsdata.db.EventDb
import ru.kozlovss.workingcontacts.data.eventsdata.dto.Event
import ru.kozlovss.workingcontacts.data.eventsdata.dto.EventRequest
import ru.kozlovss.workingcontacts.data.eventsdata.entity.EventEntity
import ru.kozlovss.workingcontacts.data.extensions.checkAndGetBody
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor(
    private val dao: EventDao,
    private val apiService: EventApiService,
    remoteKeyDao: EventRemoteKeyDao,
    db: EventDb
) : EventRepository {

    @OptIn(ExperimentalPagingApi::class)
    override val events: Flow<PagingData<Event>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        pagingSourceFactory = dao::pagingSource,
        remoteMediator = EventRemoteMediator(
            apiService,
            dao,
            remoteKeyDao,
            db
        )
    ).flow.map { it.map(EventEntity::toDto) }

    override suspend fun getById(id: Long) = apiService.getEventById(id).checkAndGetBody()
    override suspend fun likeById(id: Long) = apiService.likeEventById(id).checkAndGetBody()
    override suspend fun dislikeById(id: Long) = apiService.dislikeEventById(id).checkAndGetBody()
    override suspend fun participateById(id: Long) = apiService.participateEventById(id).checkAndGetBody()
    override suspend fun notParticipateById(id: Long) = apiService.notParticipateEventById(id).checkAndGetBody()
    override suspend fun removeById(id: Long) = apiService.deleteEventById(id).checkAndGetBody()
    override suspend fun save(event: EventRequest) = apiService.saveEvent(event).checkAndGetBody()
    override suspend fun switchAudioPlayer(event: Event, audioPlayerState: Boolean) {
        val newEvent = event.copy(
            isPaying = audioPlayerState
        )
        dao.update(EventEntity.fromDto(newEvent))
    }
}