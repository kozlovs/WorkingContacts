package ru.kozlovss.workingcontacts.presentation.userslist.adapter

import ru.kozlovss.workingcontacts.data.dto.User

interface OnInteractionListener {
    fun onSelect(user: User)
}