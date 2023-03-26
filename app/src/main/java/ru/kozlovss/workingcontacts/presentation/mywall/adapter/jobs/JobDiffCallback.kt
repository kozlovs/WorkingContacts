package ru.kozlovss.workingcontacts.presentation.mywall.adapter.jobs

import androidx.recyclerview.widget.DiffUtil
import ru.kozlovss.workingcontacts.data.jobsdata.dto.Job

class JobDiffCallback : DiffUtil.ItemCallback<Job>() {
    override fun areItemsTheSame(oldItem: Job, newItem: Job) = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Job, newItem: Job) = oldItem == newItem

    override fun getChangePayload(oldItem: Job, newItem: Job): Any =
        Payload(
            name = newItem.name.takeIf { it != oldItem.name },
            position = newItem.position.takeIf { it != oldItem.position },
            start = newItem.start.takeIf { it != oldItem.start },
            finish = newItem.finish.takeIf { it != oldItem.finish },
            link = newItem.link.takeIf { it != oldItem.link }
        )
}