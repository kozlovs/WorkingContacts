package ru.kozlovss.workingcontacts.data.dto

data class Attachment(
    val url: String,
    val type: Type
) {
    enum class Type {
        IMAGE, VIDEO, AUDIO
    }
}