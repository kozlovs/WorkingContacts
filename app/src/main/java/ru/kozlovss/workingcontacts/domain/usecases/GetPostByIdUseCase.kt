package ru.kozlovss.workingcontacts.domain.usecases

import ru.kozlovss.workingcontacts.data.postsdata.dto.Post
import ru.kozlovss.workingcontacts.domain.repository.PostRepository
import ru.kozlovss.workingcontacts.domain.error.mapExceptions
import javax.inject.Inject

class GetPostByIdUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend fun execute(id: Long): Post = mapExceptions {
        return postRepository.getById(id)
    }
}