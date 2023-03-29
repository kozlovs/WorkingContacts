package ru.kozlovss.workingcontacts.presentation.mywall.adapter.jobs

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import ru.kozlovss.workingcontacts.data.jobsdata.dto.Job
import ru.kozlovss.workingcontacts.databinding.CardMyJobBinding
import ru.kozlovss.workingcontacts.domain.util.Formatter

class JobViewHolder(
    private val binding: CardMyJobBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(job: Job) = with(binding) {
        name.text = job.name
        position.text = job.position
        start.text = Formatter.localDateTimeToJobDateFormat(job.start)
        finishCaption.isVisible = job.finish != null
        if (job.finish != null) {
            finish.text = Formatter.localDateTimeToJobDateFormat(job.finish)
        } else {
            finish.text = "till now"
        }
        if (job.link != null) {
            link.text = job.link
            link.isVisible = true
        } else {
            link.isVisible = false
        }
        setListeners(job)
    }

    fun bind(payload: Payload) = with(binding) {
        payload.name?.let { name.text = it }
        payload.position?.let { position.text = it }
        payload.start?.let { start.text = Formatter.localDateTimeToJobDateFormat(it) }
        payload.finish?.let { finish.text = Formatter.localDateTimeToJobDateFormat(it) }
        payload.link?.let { link.text = it }
    }

    private fun setListeners(job: Job) = with(binding) {
        remove.setOnClickListener {
            onInteractionListener.onRemove(job)
        }
    }
}