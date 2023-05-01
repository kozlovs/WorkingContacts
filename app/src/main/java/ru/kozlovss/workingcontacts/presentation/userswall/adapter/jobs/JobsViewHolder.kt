package ru.kozlovss.workingcontacts.presentation.userswall.adapter.jobs

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import ru.kozlovss.workingcontacts.R
import ru.kozlovss.workingcontacts.entity.Job
import ru.kozlovss.workingcontacts.databinding.CardJobBinding
import ru.kozlovss.workingcontacts.presentation.util.Formatter

class JobsViewHolder(
    private val binding: CardJobBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(job: Job) = with(binding) {
        remove.visibility = View.GONE
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
    }
}