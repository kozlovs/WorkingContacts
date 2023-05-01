package ru.kozlovss.workingcontacts.presentation.userslist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import ru.kozlovss.workingcontacts.entity.User
import ru.kozlovss.workingcontacts.databinding.CardUserItemSquareBinding


class UsersAdapter(
    private val onInteractionListener: OnInteractionListener
) : ListAdapter<User, UserViewHolder>(UserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = CardUserItemSquareBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(view, onInteractionListener)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}