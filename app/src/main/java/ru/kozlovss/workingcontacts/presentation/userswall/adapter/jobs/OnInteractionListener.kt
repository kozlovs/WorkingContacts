package ru.kozlovss.workingcontacts.presentation.userswall.adapter.jobs

import ru.kozlovss.workingcontacts.data.jobsdata.dto.Job

interface OnInteractionListener {
    fun onRemove(job: Job)
    fun onEdit(job: Job)
}