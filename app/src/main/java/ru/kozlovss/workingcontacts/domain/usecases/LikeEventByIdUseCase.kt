package ru.kozlovss.workingcontacts.domain.usecases

import ru.kozlovss.workingcontacts.data.eventsdata.dao.EventDao
import ru.kozlovss.workingcontacts.data.eventsdata.entity.EventEntity
import ru.kozlovss.workingcontacts.data.eventsdata.repository.EventRepository
import ru.kozlovss.workingcontacts.domain.error.mapExceptions
import javax.inject.Inject

class LikeEventByIdUseCase @Inject constructor(
    private val eventRepository: EventRepository,
    private val eventDao: EventDao
) {
    suspend fun execute(id: Long) = mapExceptions {
        val event = eventRepository.getById(id)
        val eventResponse =
            if (event.likedByMe) {
                eventRepository.dislikeById(id)
            } else {
                eventRepository.likeById(id)
            }
        eventDao.insert(EventEntity.fromDto(eventResponse))
    }
}