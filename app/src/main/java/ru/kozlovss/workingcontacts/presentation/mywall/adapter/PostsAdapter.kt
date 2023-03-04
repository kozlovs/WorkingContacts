package ru.kozlovss.workingcontacts.presentation.mywall.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.kozlovss.workingcontacts.R
import ru.kozlovss.workingcontacts.data.postsdata.dto.Post
import ru.kozlovss.workingcontacts.databinding.CardPostBinding

class PostsAdapter(private val onInteractionListener: OnInteractionListener) :
    PagingDataAdapter<Post, RecyclerView.ViewHolder>(PostDiffCallback()) {
    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is Post -> R.layout.card_post
            else -> error("unknown item type")
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewTipe: Int): RecyclerView.ViewHolder =
        when (viewTipe) {
            R.layout.card_post -> {
                val binding =
                    CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                PostViewHolder(binding, onInteractionListener)
            }
            else -> error("unknown view type")
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
            is Post -> {
                payload?.let {
                    (holder as? PostViewHolder)?.bind(payload)
                } ?: (holder as? PostViewHolder)?.bind(feedItem)
            }
            else -> error("unknown item type")
        }
    }
}

data class Payload(
    val likedByMe: Boolean? = null,
    val likes: Int? = null,
    val content: String? = null,
)