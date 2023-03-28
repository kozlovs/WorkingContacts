package ru.kozlovss.workingcontacts.presentation.event.adapter

import androidx.recyclerview.widget.DiffUtil
import ru.kozlovss.workingcontacts.data.dto.User

class SpeakerDiffCallback: DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: User, newItem: User) = oldItem == newItem
}