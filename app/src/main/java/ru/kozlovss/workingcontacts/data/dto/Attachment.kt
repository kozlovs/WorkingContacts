package ru.kozlovss.workingcontacts.data.dto

import ru.kozlovss.workingcontacts.data.enumeration.AttachmentType

data class Attachment(
    val url: String,
    val type: AttachmentType
)