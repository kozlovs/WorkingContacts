package ru.kozlovss.workingcontacts.data.eventsdata.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.kozlovss.workingcontacts.data.dto.PhotoModel
import ru.kozlovss.workingcontacts.data.eventsdata.dto.Event

interface EventRepository {
    val events: Flow<PagingData<Event>>
    suspend fun getById(id: Long): Event?
    suspend fun likeById(id: Long)
    suspend fun removeById(id: Long)
    suspend fun save(post: Event)
    suspend fun saveWithAttachment(post: Event, photo: PhotoModel)
}