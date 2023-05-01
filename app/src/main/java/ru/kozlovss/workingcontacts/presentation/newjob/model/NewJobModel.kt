package ru.kozlovss.workingcontacts.presentation.newjob.model

import ru.kozlovss.workingcontacts.entity.Job

class NewJobModel(
    val job: Job
) {
    sealed interface State {
        object Idle : State
        object Error : State
        object Loading : State
    }
}