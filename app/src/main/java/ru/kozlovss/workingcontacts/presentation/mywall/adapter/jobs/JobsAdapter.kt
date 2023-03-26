package ru.kozlovss.workingcontacts.presentation.mywall.adapter.jobs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import ru.kozlovss.workingcontacts.R
import ru.kozlovss.workingcontacts.data.jobsdata.dto.Job
import ru.kozlovss.workingcontacts.databinding.CardMyJobBinding

class JobsAdapter (private val onInteractionListener: OnInteractionListener) :
    ListAdapter<Job, JobViewHolder>(JobDiffCallback()) {

    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is Job -> R.layout.card_my_job
            else -> error("unknown item type")
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewTipe: Int): JobViewHolder =
        when (viewTipe) {
            R.layout.card_my_job -> {
                val binding =
                    CardMyJobBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                JobViewHolder(binding, onInteractionListener)
            }
            else -> error("unknown view type")
        }

    override fun onBindViewHolder(
        holder: JobViewHolder,
        position: Int
    ) {
        bindItem(holder, position)
    }

    override fun onBindViewHolder(
        holder: JobViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            payloads.forEach {
                (it as? Payload)?.let { payload ->
                    bindItem(holder, position, payload)
                }
            }
        }
    }

    private fun bindItem(
        holder: JobViewHolder,
        position: Int,
        payload: Payload? = null
    ) {
        when (val feedItem = getItem(position)) {
            is Job -> {
                payload?.let {
                    (holder as? JobViewHolder)?.bind(payload)
                } ?: (holder as? JobViewHolder)?.bind(feedItem)
            }
            else -> error("unknown item type")
        }
    }
}

data class Payload(
    val name: String? = null,
    val position: String? = null,
    val start: String? = null,
    val finish: String? = null,
    val link: String? = null
)