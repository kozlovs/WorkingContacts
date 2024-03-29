package ru.kozlovss.workingcontacts.presentation.newpost.adapter

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.kozlovss.workingcontacts.R
import ru.kozlovss.workingcontacts.entity.User
import ru.kozlovss.workingcontacts.databinding.CardUserItemLineBinding

class UsersPreviewViewHolder(
    private val binding: CardUserItemLineBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(mention: User) = with(binding) {
        name.text = mention.name
        if (mention.avatar != null) {
            Glide.with(avatar)
                .load(mention.avatar)
                .placeholder(R.drawable.baseline_update_24)
                .error(R.drawable.baseline_error_outline_24)
                .timeout(10_000)
                .into(avatar)
        } else {
            avatar.setImageResource(R.drawable.baseline_person_outline_24)
        }
        setListeners(mention)
    }

    private fun setListeners(mention: User) = with(binding) {
        delete.setOnClickListener {
            onInteractionListener.onRemove(mention)
        }
    }
}