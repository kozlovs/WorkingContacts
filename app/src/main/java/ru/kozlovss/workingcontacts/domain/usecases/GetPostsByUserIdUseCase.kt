package ru.kozlovss.workingcontacts.domain.usecases

import ru.kozlovss.workingcontacts.data.postsdata.dto.Post
import ru.kozlovss.workingcontacts.data.walldata.repository.UserWallRepository
import ru.kozlovss.workingcontacts.domain.error.NetworkError
import java.io.IOException
import javax.inject.Inject

class GetPostsByUserIdUseCase @Inject constructor(
    private val wallRepository: UserWallRepository
) {
    suspend fun execute(id: Long): List<Post> {
        try {
            return wallRepository.getAll(id)
        } catch (e: IOException) {
            throw NetworkError()
        } catch (e: Exception) {
            e.printStackTrace()
            throw UnknownError()
        }
    }
}