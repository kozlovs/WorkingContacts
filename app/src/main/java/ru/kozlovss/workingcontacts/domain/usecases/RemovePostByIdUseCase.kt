package ru.kozlovss.workingcontacts.domain.usecases

import ru.kozlovss.workingcontacts.data.mywalldata.dao.MyWallDao
import ru.kozlovss.workingcontacts.data.postsdata.dao.PostDao
import ru.kozlovss.workingcontacts.data.postsdata.repository.PostRepository
import ru.kozlovss.workingcontacts.domain.error.catchExceptions
import javax.inject.Inject

class RemovePostByIdUseCase @Inject constructor(
    private val postRepository: PostRepository,
    private val postDao: PostDao,
    private val myWallDao: MyWallDao
) {
    suspend fun execute(id: Long) = catchExceptions {
        postRepository.removeById(id)
        postDao.removeById(id)
        myWallDao.removeById(id)
    }
}