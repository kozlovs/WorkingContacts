package ru.kozlovss.workingcontacts.presentation.newevent.adapter

import ru.kozlovss.workingcontacts.entity.User


interface OnInteractionListener {
    fun onRemove(user: User)
}