package ru.kozlovss.workingcontacts.domain.usecases

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.kozlovss.workingcontacts.data.dto.MediaModel
import ru.kozlovss.workingcontacts.data.userdata.repository.UserRepository
import ru.kozlovss.workingcontacts.domain.error.NetworkError
import java.io.IOException
import javax.inject.Inject

class RegistrationUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend fun execute(login: String, password: String, name: String, avatar: MediaModel?) {
        try {
            registration(login, password, name, avatar)
        } catch (e: IOException) {
            throw NetworkError()
        } catch (e: Exception) {
            e.printStackTrace()
            throw UnknownError()
        }
    }

    private suspend fun registration(login: String, password: String, name: String, avatar: MediaModel?) {
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