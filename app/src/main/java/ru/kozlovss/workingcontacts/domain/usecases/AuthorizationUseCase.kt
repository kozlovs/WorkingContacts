package ru.kozlovss.workingcontacts.domain.usecases

import ru.kozlovss.workingcontacts.data.userdata.repository.UserRepository
import ru.kozlovss.workingcontacts.domain.auth.AppAuth
import ru.kozlovss.workingcontacts.domain.error.NetworkError
import java.io.IOException
import javax.inject.Inject

class AuthorizationUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val appAuth: AppAuth
) {
    suspend fun execute(login: String, password: String) {
        try {
            authorization(login, password)
        } catch (e: IOException) {
            throw NetworkError()
        } catch (e: Exception) {
            e.printStackTrace()
            throw UnknownError()
        }
    }

    private suspend fun authorization(login: String, password: String) {
        val token = userRepository.login(login, password)
        appAuth.setAuth(token)
    }
}