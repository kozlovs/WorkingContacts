package ru.kozlovss.workingcontacts.domain.usecases

import androidx.paging.PagingData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import ru.kozlovss.workingcontacts.data.mywalldata.repository.MyWallRepository
import ru.kozlovss.workingcontacts.data.postsdata.dto.Post
import ru.kozlovss.workingcontacts.domain.error.NetworkError
import java.io.IOException
import javax.inject.Inject

class GetMyWallPostsPagingDataUseCase @Inject constructor(
    private val myWallRepository: MyWallRepository
) {
    fun execute(): Flow<PagingData<Post>> {
        try {
            return myWallRepository.posts.flowOn(Dispatchers.Default)
        } catch (e: IOException) {
            throw NetworkError()
        } catch (e: Exception) {
            e.printStackTrace()
            throw UnknownError()
        }
    }
}