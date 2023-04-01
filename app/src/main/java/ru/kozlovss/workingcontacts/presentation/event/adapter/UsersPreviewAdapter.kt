package ru.kozlovss.workingcontacts.presentation.event.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import ru.kozlovss.workingcontacts.data.dto.User
import ru.kozlovss.workingcontacts.databinding.CardUserItemBinding

class UsersPreviewAdapter : ListAdapter<User, UsersPreviewViewHolder>(UsersPreviewDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersPreviewViewHolder {
        val view = CardUserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UsersPreviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: UsersPreviewViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}