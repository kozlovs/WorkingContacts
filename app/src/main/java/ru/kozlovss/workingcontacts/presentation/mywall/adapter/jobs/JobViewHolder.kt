package ru.kozlovss.workingcontacts.presentation.mywall.adapter.jobs

import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import ru.kozlovss.workingcontacts.R
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
        menu.setOnClickListener {
            PopupMenu(it.context, it).apply {
                inflate(R.menu.options_post_menu)
                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.remove -> {
                            onInteractionListener.onRemove(job)
                            true
                        }
                        R.id.edit -> {
                            onInteractionListener.onEdit(job)
                            true
                        }
                        else -> false
                    }
                }
            }.show()
        }
    }
}