package ru.kozlovss.workingcontacts.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.kozlovss.workingcontacts.data.eventsdata.dto.Event
import ru.kozlovss.workingcontacts.data.eventsdata.dto.EventRequest

interface EventRepository {
    val events: Flow<PagingData<Event>>
    suspend fun getById(id: Long): Event
    suspend fun likeById(id: Long): Event
    suspend fun dislikeById(id: Long): Event
    suspend fun removeById(id: Long)
    suspend fun participateById(id: Long): Event
    suspend fun notParticipateById(id: Long): Event
    suspend fun save(event: EventRequest): Event
    suspend fun switchAudioPlayer(event: Event, audioPlayerState: Boolean)
}