package ru.kozlovss.workingcontacts.presentation.events.model

import ru.kozlovss.workingcontacts.data.eventsdata.dto.Event

class EventsModel(
    val posts: List<Event> = emptyList(),
    val empty: Boolean = false,
) {
    sealed interface State {
        object Idle : State
        object Error : State
        object Loading : State
    }
}