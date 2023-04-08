package ru.kozlovss.workingcontacts.presentation.newevent.adapter

import ru.kozlovss.workingcontacts.data.dto.User

interface OnInteractionListener {
    fun onRemove(user: User)
}