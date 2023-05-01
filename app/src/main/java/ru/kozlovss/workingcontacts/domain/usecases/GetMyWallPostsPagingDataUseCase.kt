package ru.kozlovss.workingcontacts.domain.usecases

import androidx.paging.PagingData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import ru.kozlovss.workingcontacts.domain.repository.MyWallRepository
import ru.kozlovss.workingcontacts.entity.Post
import ru.kozlovss.workingcontacts.domain.error.mapExceptions
import javax.inject.Inject

class GetMyWallPostsPagingDataUseCase @Inject constructor(
    private val myWallRepository: MyWallRepository
) {
    fun execute(): Flow<PagingData<Post>> = mapExceptions {
        return myWallRepository.posts.flowOn(Dispatchers.Default)
    }
}