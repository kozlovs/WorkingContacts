package ru.kozlovss.workingcontacts.presentation.newpost.adapter

import ru.kozlovss.workingcontacts.entity.User

interface OnInteractionListener {
    fun onRemove(user: User)
}