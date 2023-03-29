package ru.kozlovss.workingcontacts.presentation.newevent.model

import ru.kozlovss.workingcontacts.data.eventsdata.dto.Event

class NewEventModel(
    val event: Event
) {
    sealed interface State {
        object Idle : State
        object Error : State
        object Loading : State
    }
}