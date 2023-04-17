package ru.kozlovss.workingcontacts.presentation.newpost.adapter

import ru.kozlovss.workingcontacts.data.userdata.dto.User

interface OnInteractionListener {
    fun onRemove(user: User)
}