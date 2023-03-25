package ru.kozlovss.workingcontacts.presentation.mywall

import ru.kozlovss.workingcontacts.data.jobsdata.dto.Job

interface OnInteractionListener {
    fun onRemove(job: Job)
    fun onEdit(job: Job)
}