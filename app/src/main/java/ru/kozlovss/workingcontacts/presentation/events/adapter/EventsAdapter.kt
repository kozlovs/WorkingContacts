package ru.kozlovss.workingcontacts.presentation.events.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.kozlovss.workingcontacts.R
import ru.kozlovss.workingcontacts.entity.Event
import ru.kozlovss.workingcontacts.databinding.CardEventBinding

class EventsAdapter(
    private val onInteractionListener: OnInteractionListener,
    private val context: Context
    ) :
    PagingDataAdapter<Event, RecyclerView.ViewHolder>(EventDiffCallback()) {
    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is Event -> R.layout.card_event
            else -> error(context.getString(R.string.unknown_item_type))
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewTipe: Int): RecyclerView.ViewHolder =
        when (viewTipe) {
            R.layout.card_event -> {
                val binding =
                    CardEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                EventViewHolder(binding, onInteractionListener)
            }
            else -> error(context.getString(R.string.unknown_item_type))
        }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        bindItem(holder, position)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
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
        holder: RecyclerView.ViewHolder,
        position: Int,
        payload: Payload? = null
    ) {
        when (val feedItem = getItem(position)) {
            is Event -> {
                payload?.let {
                    (holder as? EventViewHolder)?.bind(payload)
                } ?: (holder as? EventViewHolder)?.bind(feedItem)
            }
            else -> error(context.getString(R.string.unknown_item_type))
        }
    }
}

data class Payload(
    val likedByMe: Boolean? = null,
    val likes: Int? = null,
    val participatedByMe: Boolean? = null,
    val participateIds: Int? = null,
    val content: String? = null,
    val isPlay: Boolean? = null
)