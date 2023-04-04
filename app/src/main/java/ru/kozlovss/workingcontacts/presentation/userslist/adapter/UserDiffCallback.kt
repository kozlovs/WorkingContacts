package ru.kozlovss.workingcontacts.presentation.userslist.adapter

import androidx.recyclerview.widget.DiffUtil
import ru.kozlovss.workingcontacts.data.dto.User

class UserDiffCallback : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User) = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: User, newItem: User) = oldItem == newItem
}