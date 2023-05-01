package ru.kozlovss.workingcontacts.domain.usecases

import ru.kozlovss.workingcontacts.data.userdata.repository.UserRepository
import ru.kozlovss.workingcontacts.domain.error.mapExceptions
import javax.inject.Inject

class LogOutUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend fun execute() = mapExceptions {
        userRepository.clearTokenOfUser()
    }
}