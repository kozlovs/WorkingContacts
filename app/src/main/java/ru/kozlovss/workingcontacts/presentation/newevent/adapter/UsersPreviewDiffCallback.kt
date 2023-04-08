package ru.kozlovss.workingcontacts.presentation.newevent.adapter

import androidx.recyclerview.widget.DiffUtil
import ru.kozlovss.workingcontacts.data.dto.User

class UsersPreviewDiffCallback: DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: User, newItem: User) = oldItem == newItem
}