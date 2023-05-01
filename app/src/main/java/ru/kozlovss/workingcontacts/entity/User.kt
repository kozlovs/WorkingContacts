package ru.kozlovss.workingcontacts.entity

data class User(
    val id: Long,
    val login: String,
    val name: String,
    val avatar: String?
)
