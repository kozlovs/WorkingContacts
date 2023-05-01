package ru.kozlovss.workingcontacts.domain.usecases

import ru.kozlovss.workingcontacts.data.eventsdata.dao.EventDao
import ru.kozlovss.workingcontacts.data.eventsdata.entity.EventEntity
import ru.kozlovss.workingcontacts.domain.repository.EventRepository
import ru.kozlovss.workingcontacts.domain.error.mapExceptions
import javax.inject.Inject

class ParticipateEventByIdUseCase @Inject constructor(
    private val eventRepository: EventRepository,
    private val eventDao: EventDao
) {
    suspend fun execute(id: Long) = mapExceptions {
        val event = eventRepository.getById(id)
        val eventResponse =
            if (event.participatedByMe) {
                eventRepository.notParticipateById(id)
            } else {
                eventRepository.participateById(id)
            }
        eventDao.insert(EventEntity.fromDto(eventResponse))
    }
}