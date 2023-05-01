package ru.kozlovss.workingcontacts.domain.usecases

import ru.kozlovss.workingcontacts.data.eventsdata.dao.EventDao
import ru.kozlovss.workingcontacts.data.eventsdata.repository.EventRepository
import ru.kozlovss.workingcontacts.domain.error.mapExceptions
import javax.inject.Inject

class RemoveEventByIdUseCase @Inject constructor(
    private val eventRepository: EventRepository,
    private val eventDao: EventDao
) {
    suspend fun execute(id: Long) = mapExceptions {
        eventRepository.removeById(id)
        eventDao.removeById(id)
    }
}