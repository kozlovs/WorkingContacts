package ru.kozlovss.workingcontacts.dto

import ru.kozlovss.workingcontacts.enumeration.AttachmentType

data class Attachment(
    val url: String,
    val type: AttachmentType
)