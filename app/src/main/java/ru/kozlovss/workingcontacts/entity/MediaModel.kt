package ru.kozlovss.workingcontacts.entity

import android.net.Uri
import java.io.File

data class MediaModel(val uri: Uri?, val file: File?, val type: Attachment.Type)