package ru.kozlovss.workingcontacts.domain.auth

data class AuthState(
    val id: Long = 0L,
    val token: String? = null
)
