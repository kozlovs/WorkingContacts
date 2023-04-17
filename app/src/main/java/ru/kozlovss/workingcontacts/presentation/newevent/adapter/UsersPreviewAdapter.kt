package ru.kozlovss.workingcontacts.presentation.newevent.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import ru.kozlovss.workingcontacts.data.userdata.dto.User
import ru.kozlovss.workingcontacts.databinding.CardUserItemLineBinding

class UsersPreviewAdapter(private val onInteractionListener: OnInteractionListener) : ListAdapter<User, UsersPreviewViewHolder>(UsersPreviewDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersPreviewViewHolder {
        val view = CardUserItemLineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UsersPreviewViewHolder(view, onInteractionListener)
    }

    override fun onBindViewHolder(holder: UsersPreviewViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}