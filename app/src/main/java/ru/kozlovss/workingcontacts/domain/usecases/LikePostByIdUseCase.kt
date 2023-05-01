package ru.kozlovss.workingcontacts.domain.usecases

import ru.kozlovss.workingcontacts.data.mywalldata.dao.MyWallDao
import ru.kozlovss.workingcontacts.data.postsdata.dao.PostDao
import ru.kozlovss.workingcontacts.data.postsdata.entity.PostEntity
import ru.kozlovss.workingcontacts.domain.repository.PostRepository
import ru.kozlovss.workingcontacts.domain.error.mapExceptions
import javax.inject.Inject

class LikePostByIdUseCase @Inject constructor(
    private val postRepository: PostRepository,
    private val postDao: PostDao,
    private val myWallDao: MyWallDao
) {
    suspend fun execute(id: Long) = mapExceptions {
        val post = postRepository.getById(id)
        val postResponse =
            if (post.likedByMe) {
                postRepository.dislikeById(id)
            } else {
                postRepository.likeById(id)
            }
        val postEntity = PostEntity.fromDto(postResponse)
        if (postDao.containsPostWithId(id)) postDao.insert(postEntity)
        if (postEntity.ownedByMe) myWallDao.insert(postEntity)
    }
}