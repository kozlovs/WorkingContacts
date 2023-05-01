package ru.kozlovss.workingcontacts.entity

data class Attachment(
    val url: String,
    val type: Type
) {
    enum class Type {
        IMAGE, VIDEO, AUDIO
    }
}