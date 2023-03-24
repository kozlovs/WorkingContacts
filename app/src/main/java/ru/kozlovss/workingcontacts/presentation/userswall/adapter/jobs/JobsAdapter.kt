package ru.kozlovss.workingcontacts.presentation.userswall.adapter.jobs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import ru.kozlovss.workingcontacts.data.jobsdata.dto.Job
import ru.kozlovss.workingcontacts.databinding.CardJobBinding

class JobsAdapter(private val onInteractionListener: OnInteractionListener): ListAdapter<Job, JobsViewHolder>(JobsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobsViewHolder {
        val view = CardJobBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JobsViewHolder(view, onInteractionListener)
    }

    override fun onBindViewHolder(holder: JobsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}