package ru.kozlovss.workingcontacts.presentation.mywall.adapter.jobs

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import ru.kozlovss.workingcontacts.R
import ru.kozlovss.workingcontacts.data.jobsdata.dto.Job
import ru.kozlovss.workingcontacts.databinding.CardJobBinding
import ru.kozlovss.workingcontacts.domain.util.Formatter

class JobViewHolder(
    private val binding: CardJobBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(job: Job) = with(binding) {
        name.text = job.name
        position.text = job.position
        period.text = if (job.finish.isNullOrBlank()) {
            root.context.getString(
                R.string.period_start_till_now,
                Formatter.localDateTimeToJobDateFormat(job.start)
            )
        } else {
            root.context.getString(
                R.string.period_start_and_finish,
                Formatter.localDateTimeToJobDateFormat(job.start),
                Formatter.localDateTimeToJobDateFormat(job.finish)
            )
        }
        link.isVisible = job.link != null
        job.link?.let { link.text = job.link }

        setListeners(job)
    }

    private fun setListeners(job: Job) = with(binding) {
        remove.setOnClickListener {
            onInteractionListener.onRemove(job)
        }
    }
}