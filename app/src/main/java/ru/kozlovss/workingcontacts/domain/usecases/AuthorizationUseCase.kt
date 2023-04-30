package ru.kozlovss.workingcontacts.domain.usecases

import ru.kozlovss.workingcontacts.data.userdata.repository.UserRepository
import ru.kozlovss.workingcontacts.domain.error.catchExceptions
import javax.inject.Inject

class AuthorizationUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend fun execute(login: String, password: String) = catchExceptions {
        val token = userRepository.login(login, password)
        userRepository.saveTokenOfUser(token)
    }
}