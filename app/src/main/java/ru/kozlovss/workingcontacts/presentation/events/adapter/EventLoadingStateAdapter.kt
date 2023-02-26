package ru.kozlovss.workingcontacts.presentation.events.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.kozlovss.workingcontacts.databinding.ItemLoadingBinding

class EventLoadingStateAdapter(private val retryListener: () -> Unit) :
    LoadStateAdapter<EventLoadingViewHolder>() {
    override fun onBindViewHolder(holder: EventLoadingViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): EventLoadingViewHolder =
        EventLoadingViewHolder(
            ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            retryListener,
        )
}

class EventLoadingViewHolder(
    private val itemLoadingBinding: ItemLoadingBinding,
    private val retryListener: () -> Unit
) : RecyclerView.ViewHolder(itemLoadingBinding.root) {
    fun bind(loadState: LoadState) {
        itemLoadingBinding.apply {
            progress.isVisible = loadState is LoadState.Loading
            retryButton.isVisible = loadState is LoadState.Error
            retryButton.setOnClickListener {
                retryListener()
            }
        }
    }
}