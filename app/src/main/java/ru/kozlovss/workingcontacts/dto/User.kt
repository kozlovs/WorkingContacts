package ru.kozlovss.workingcontacts.dto

data class User(
    val id: Long,
    val login: String,
    val name: String,
    val avatar: String?
) {
    fun toPreview() = UserPreview(this.name, this.avatar)
}

data class UserPreview(
    val name: String,
    val avatar: String?
)
