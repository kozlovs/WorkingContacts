package ru.kozlovss.workingcontacts.domain.usecases

import ru.kozlovss.workingcontacts.data.userdata.dto.User
import ru.kozlovss.workingcontacts.data.userdata.repository.UserRepository
import ru.kozlovss.workingcontacts.domain.error.NetworkError
import java.io.IOException
import javax.inject.Inject

class GetUserByIdUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend fun execute(id: Long): User {
        try {
            return userRepository.getUserById(id)
        } catch (e: IOException) {
            throw NetworkError()
        } catch (e: Exception) {
            e.printStackTrace()
            throw UnknownError()
        }
    }
}