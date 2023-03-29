package ru.kozlovss.workingcontacts.presentation.event.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import ru.kozlovss.workingcontacts.data.dto.User
import ru.kozlovss.workingcontacts.databinding.CardUserItemBinding

class SpeakersAdapter : ListAdapter<User, SpeakerViewHolder>(SpeakerDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpeakerViewHolder {
        val view = CardUserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SpeakerViewHolder(view)
    }

    override fun onBindViewHolder(holder: SpeakerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}