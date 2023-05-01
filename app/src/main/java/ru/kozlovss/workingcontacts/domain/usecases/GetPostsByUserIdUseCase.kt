package ru.kozlovss.workingcontacts.domain.usecases

import ru.kozlovss.workingcontacts.data.postsdata.dto.Post
import ru.kozlovss.workingcontacts.data.walldata.repository.UserWallRepository
import ru.kozlovss.workingcontacts.domain.error.mapExceptions
import javax.inject.Inject

class GetPostsByUserIdUseCase @Inject constructor(
    private val wallRepository: UserWallRepository
) {
    suspend fun execute(id: Long): List<Post> = mapExceptions {
        return wallRepository.getAll(id)
    }
}