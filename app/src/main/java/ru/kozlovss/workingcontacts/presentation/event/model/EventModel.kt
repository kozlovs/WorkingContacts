package ru.kozlovss.workingcontacts.presentation.event.model

import ru.kozlovss.workingcontacts.data.eventsdata.dto.Event

data class EventModel(
    val event: Event
) {
    sealed interface State {
        object Idle : State
        object Error : State
        object Loading : State
    }
}
