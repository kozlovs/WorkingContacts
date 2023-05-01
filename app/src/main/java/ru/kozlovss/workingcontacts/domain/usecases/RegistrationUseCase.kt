package ru.kozlovss.workingcontacts.domain.usecases

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.kozlovss.workingcontacts.entity.MediaModel
import ru.kozlovss.workingcontacts.domain.repository.UserRepository
import ru.kozlovss.workingcontacts.domain.error.mapExceptions
import javax.inject.Inject

class RegistrationUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend fun execute(login: String, password: String, name: String, avatar: MediaModel?) = mapExceptions {
        val mediaType = "text/plain".toMediaType()
        val loginRequestBody = login.toRequestBody(mediaType)
        val passwordRequestBody = password.toRequestBody(mediaType)
        val nameRequestBody = name.toRequestBody(mediaType)
        val avatarRequestBody = avatar?.let {
            MultipartBody.Part.createFormData(
                "file",
                it.file?.name,
                requireNotNull(it.file?.asRequestBody())
            )
        }
        val token = userRepository.register(
            loginRequestBody,
            passwordRequestBody,
            nameRequestBody,
            avatarRequestBody
        )
        userRepository.saveTokenOfUser(token)
    }
}