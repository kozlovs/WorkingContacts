package ru.kozlovss.workingcontacts.presentation.events.model

import ru.kozlovss.workingcontacts.entity.Event

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