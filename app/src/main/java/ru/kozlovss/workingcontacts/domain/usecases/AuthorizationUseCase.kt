package ru.kozlovss.workingcontacts.domain.usecases

import ru.kozlovss.workingcontacts.domain.repository.UserRepository
import ru.kozlovss.workingcontacts.domain.error.mapExceptions
import javax.inject.Inject

class AuthorizationUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend fun execute(login: String, password: String) = mapExceptions {
        val token = userRepository.login(login, password)
        userRepository.saveTokenOfUser(token)
    }
}