package ru.kozlovss.workingcontacts.presentation.userslist.adapter

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.kozlovss.workingcontacts.R
import ru.kozlovss.workingcontacts.data.userdata.dto.User
import ru.kozlovss.workingcontacts.databinding.CardUserItemSquareBinding

class UserViewHolder(
    private val binding: CardUserItemSquareBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(user: User) = with(binding) {
        name.text = user.name
        if (user.avatar != null) {
            Glide.with(avatar)
                .load(user.avatar)
                .placeholder(R.drawable.baseline_update_24)
                .error(R.drawable.baseline_error_outline_24)
                .timeout(10_000)
                .into(avatar)
        } else {
            avatar.setImageResource(R.drawable.baseline_person_outline_24)
        }
        setListeners(user)
    }


    private fun setListeners(user: User) = with(binding) {
        root.setOnClickListener {
            onInteractionListener.onSelect(user)
        }
    }
}