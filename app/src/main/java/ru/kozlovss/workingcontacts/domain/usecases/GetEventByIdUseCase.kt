package ru.kozlovss.workingcontacts.domain.usecases

import ru.kozlovss.workingcontacts.data.eventsdata.dto.Event
import ru.kozlovss.workingcontacts.data.eventsdata.repository.EventRepository
import ru.kozlovss.workingcontacts.domain.error.mapExceptions
import javax.inject.Inject

class GetEventByIdUseCase @Inject constructor(
    private val eventRepository: EventRepository
) {
    suspend fun execute(id: Long): Event = mapExceptions {
        return eventRepository.getById(id)
    }
}