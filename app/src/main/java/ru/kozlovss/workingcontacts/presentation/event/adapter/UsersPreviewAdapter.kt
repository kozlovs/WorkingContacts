package ru.kozlovss.workingcontacts.presentation.event.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import ru.kozlovss.workingcontacts.entity.User
import ru.kozlovss.workingcontacts.databinding.CardUserItemLineBinding

class UsersPreviewAdapter : ListAdapter<User, UsersPreviewViewHolder>(UsersPreviewDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersPreviewViewHolder {
        val view = CardUserItemLineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UsersPreviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: UsersPreviewViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}