package ru.kozlovss.workingcontacts.presentation.mywall.adapter.posts

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import ru.kozlovss.workingcontacts.R
import ru.kozlovss.workingcontacts.entity.Post
import ru.kozlovss.workingcontacts.databinding.CardPostBinding

class PostsAdapter(
    private val onInteractionListener: OnInteractionListener,
    private val context: Context
) :
    PagingDataAdapter<Post, PostViewHolder>(PostDiffCallback()) {
    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is Post -> R.layout.card_post
            else -> error(context.getString(R.string.unknown_item_type))
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewTipe: Int): PostViewHolder =
        when (viewTipe) {
            R.layout.card_post -> {
                val binding =
                    CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                PostViewHolder(binding, onInteractionListener)
            }

            else -> error(context.getString(R.string.unknown_item_type))
        }

    override fun onBindViewHolder(
        holder: PostViewHolder,
        position: Int
    ) {
        bindItem(holder, position)
    }

    override fun onBindViewHolder(
        holder: PostViewHolder,
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
        holder: PostViewHolder,
        position: Int,
        payload: Payload? = null
    ) {
        when (val feedItem = getItem(position)) {
            is Post -> {
                payload?.let {
                    (holder as? PostViewHolder)?.bind(payload)
                } ?: (holder as? PostViewHolder)?.bind(feedItem)
            }

            else -> error(context.getString(R.string.unknown_item_type))
        }
    }
}

data class Payload(
    val likedByMe: Boolean? = null,
    val likes: Int? = null,
    val content: String? = null,
    val isPlay: Boolean? = null
)