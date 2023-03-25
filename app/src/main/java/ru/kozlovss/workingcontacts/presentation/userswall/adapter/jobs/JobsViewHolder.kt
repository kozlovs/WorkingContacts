package ru.kozlovss.workingcontacts.presentation.userswall.adapter.jobs

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import ru.kozlovss.workingcontacts.data.jobsdata.dto.Job
import ru.kozlovss.workingcontacts.databinding.CardJobBinding
import ru.kozlovss.workingcontacts.domain.util.Formatter

class JobsViewHolder(
    private val binding: CardJobBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(job: Job) {
        binding.apply {
            name.text = job.name
            position.text = job.position
            start.text = Formatter.localDateTimeToJobDateFormat(job.start)
            if (job.finish != null) {
                finish.text = Formatter.localDateTimeToJobDateFormat(job.finish)
            } else {
                finish.text = "until now"
            }
            if (job.link != null) {
                link.text = job.link
                link.isVisible = true
            } else {
                link.isVisible = false
            }
        }
    }
}