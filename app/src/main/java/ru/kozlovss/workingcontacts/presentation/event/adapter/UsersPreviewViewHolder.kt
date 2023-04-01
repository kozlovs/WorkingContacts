package ru.kozlovss.workingcontacts.presentation.event.adapter

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.kozlovss.workingcontacts.R
import ru.kozlovss.workingcontacts.data.dto.User
import ru.kozlovss.workingcontacts.databinding.CardUserItemBinding

class UsersPreviewViewHolder(
    private val binding: CardUserItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(speaker: User) = with(binding) {
        name.text = speaker.name
        if (speaker.avatar != null) {
            Glide.with(avatar)
                .load(speaker.avatar)
                .placeholder(R.drawable.baseline_update_24)
                .error(R.drawable.baseline_error_outline_24)
                .timeout(10_000)
                .into(avatar)
        } else {
            avatar.setImageResource(R.drawable.baseline_person_outline_24)
        }
    }
}