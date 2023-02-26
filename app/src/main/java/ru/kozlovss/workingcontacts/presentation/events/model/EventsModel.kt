package ru.kozlovss.workingcontacts.presentation.events.model

import ru.kozlovss.workingcontacts.data.eventsdata.dto.Event

class EventsModel(
    val posts: List<Event> = emptyList(),
    val empty: Boolean = false,
) {
    sealed interface EventsModelState {
        object Idle : EventsModelState
        object Error : EventsModelState
        object Loading : EventsModelState
        object Refreshing : EventsModelState
    }
}