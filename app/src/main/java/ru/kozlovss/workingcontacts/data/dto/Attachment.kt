package ru.kozlovss.workingcontacts.data.dto

data class Attachment(
    val url: String,
    val attachmentType: AttachmentType?
) {
    enum class AttachmentType {
        IMAGE, VIDEO, AUDIO
    }
}