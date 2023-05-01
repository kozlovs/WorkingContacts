package ru.kozlovss.workingcontacts.presentation.userswall.adapter.jobs

import androidx.recyclerview.widget.DiffUtil
import ru.kozlovss.workingcontacts.entity.Job

class JobsDiffCallback : DiffUtil.ItemCallback<Job>() {
    override fun areItemsTheSame(oldItem: Job, newItem: Job) = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Job, newItem: Job) = oldItem == newItem
}