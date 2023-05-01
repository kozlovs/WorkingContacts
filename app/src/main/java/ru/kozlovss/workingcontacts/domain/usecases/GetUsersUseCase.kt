package ru.kozlovss.workingcontacts.domain.usecases

import ru.kozlovss.workingcontacts.entity.User
import ru.kozlovss.workingcontacts.domain.repository.UserRepository
import ru.kozlovss.workingcontacts.domain.error.mapExceptions
import javax.inject.Inject

class GetUsersUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend fun execute(): List<User> = mapExceptions {
        return userRepository.getUsers()
    }
}