package ru.kozlovss.workingcontacts.data.userdata.dto

data class User(
    val id: Long,
    val login: String,
    val name: String,
    val avatar: String?
)
