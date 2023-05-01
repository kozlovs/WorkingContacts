package ru.kozlovss.workingcontacts.presentation.mywall.adapter.jobs

import ru.kozlovss.workingcontacts.entity.Job

interface OnInteractionListener {
    fun onRemove(job: Job)
}