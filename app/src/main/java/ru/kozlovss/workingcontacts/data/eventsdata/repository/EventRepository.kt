package ru.kozlovss.workingcontacts.data.eventsdata.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.kozlovss.workingcontacts.data.dto.MediaModel
import ru.kozlovss.workingcontacts.data.eventsdata.dto.Event
import ru.kozlovss.workingcontacts.data.eventsdata.dto.EventRequest

interface EventRepository {
    val events: Flow<PagingData<Event>>
    suspend fun getById(id: Long): Event?
    suspend fun likeById(id: Long)
    suspend fun removeById(id: Long)
    suspend fun participateById(id: Long)
    suspend fun save(event: EventRequest, model: MediaModel?)
    suspend fun switchAudioPlayer(event: Event, audioPlayerState: Boolean)
    suspend fun stopAudioPlayer()
}