package ru.kozlovss.workingcontacts.presentation.userslist.adapter

import ru.kozlovss.workingcontacts.entity.User

interface OnInteractionListener {
    fun onSelect(user: User)
}