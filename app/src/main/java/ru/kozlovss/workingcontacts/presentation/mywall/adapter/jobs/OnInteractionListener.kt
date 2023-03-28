package ru.kozlovss.workingcontacts.presentation.mywall.adapter.jobs

import ru.kozlovss.workingcontacts.data.jobsdata.dto.Job

interface OnInteractionListener {
    fun onRemove(job: Job)
}