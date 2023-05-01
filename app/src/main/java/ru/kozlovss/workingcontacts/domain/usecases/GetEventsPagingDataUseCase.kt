package ru.kozlovss.workingcontacts.domain.usecases

import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import ru.kozlovss.workingcontacts.entity.Event
import ru.kozlovss.workingcontacts.domain.repository.EventRepository
import ru.kozlovss.workingcontacts.domain.auth.AppAuth
import ru.kozlovss.workingcontacts.domain.error.mapExceptions
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class GetEventsPagingDataUseCase @Inject constructor(
    private val eventRepository: EventRepository,
    private val appAuth: AppAuth,
) {
    fun execute(): Flow<PagingData<Event>> = mapExceptions {
        return appAuth.authStateFlow
            .flatMapLatest { token ->
                eventRepository.events
                    .map { events ->
                        events.map { event ->
                            event.copy(ownedByMe = event.authorId == token?.id)
                        }
                    }
            }.flowOn(Dispatchers.Default)
    }
}