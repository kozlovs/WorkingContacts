package ru.kozlovss.workingcontacts.presentation.userswall.adapter.jobs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import ru.kozlovss.workingcontacts.entity.Job
import ru.kozlovss.workingcontacts.databinding.CardJobBinding

class JobsAdapter: ListAdapter<Job, JobsViewHolder>(JobsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobsViewHolder {
        val view = CardJobBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JobsViewHolder(view)
    }

    override fun onBindViewHolder(holder: JobsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}