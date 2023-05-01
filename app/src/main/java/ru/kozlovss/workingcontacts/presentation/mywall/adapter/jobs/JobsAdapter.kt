package ru.kozlovss.workingcontacts.presentation.mywall.adapter.jobs

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import ru.kozlovss.workingcontacts.R
import ru.kozlovss.workingcontacts.entity.Job
import ru.kozlovss.workingcontacts.databinding.CardJobBinding

class JobsAdapter(
    private val onInteractionListener: OnInteractionListener,
    private val context: Context
) : ListAdapter<Job, JobViewHolder>(JobDiffCallback()) {

    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is Job -> R.layout.card_job
            else -> error(context.getString(R.string.unknown_item_type))
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewTipe: Int): JobViewHolder =
        when (viewTipe) {
            R.layout.card_job -> {
                val binding =
                    CardJobBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                JobViewHolder(binding, onInteractionListener)
            }

            else -> error(context.getString(R.string.unknown_item_type))
        }

    override fun onBindViewHolder(
        holder: JobViewHolder,
        position: Int
    ) {
        bindItem(holder, position)
    }

    private fun bindItem(
        holder: JobViewHolder,
        position: Int
    ) {
        when (val feedItem = getItem(position)) {
            is Job -> (holder as? JobViewHolder)?.bind(feedItem)
            else -> error(context.getString(R.string.unknown_item_type))
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