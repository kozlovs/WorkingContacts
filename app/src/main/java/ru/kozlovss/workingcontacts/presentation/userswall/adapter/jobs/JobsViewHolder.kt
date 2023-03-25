package ru.kozlovss.workingcontacts.presentation.userswall.adapter.jobs

import android.view.View
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import ru.kozlovss.workingcontacts.R
import ru.kozlovss.workingcontacts.data.jobsdata.dto.Job
import ru.kozlovss.workingcontacts.databinding.CardJobBinding

class JobsViewHolder(
    private val binding: CardJobBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(job: Job) {
        binding.apply {
            name.text = job.name
            position.text = job.position
            start.text = job.start
            if (job.finish != null) {
                finish.text = job.finish
                finish.isVisible = true
            } else {
                finish.isVisible = false
            }
            if (job.link != null) {
                link.text = job.link
                link.isVisible = true
            } else {
                link.isVisible = false
            }
        }
    }

    private fun showMenu(v: View, job: Job) {
        PopupMenu(v.context, v).apply {
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