package ru.kozlovss.workingcontacts.domain.usecases

import ru.kozlovss.workingcontacts.data.dto.MediaModel
import ru.kozlovss.workingcontacts.data.userdata.repository.UserRepository
import ru.kozlovss.workingcontacts.domain.auth.AppAuth
import ru.kozlovss.workingcontacts.domain.error.NetworkError
import java.io.IOException
import javax.inject.Inject

class RegistrationUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val appAuth: AppAuth
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
        val token = userRepository.register(login, password, name, avatar)
        appAuth.setAuth(token)
    }
}