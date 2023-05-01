package ru.kozlovss.workingcontacts.domain.usecases

import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import ru.kozlovss.workingcontacts.data.postsdata.dto.Post
import ru.kozlovss.workingcontacts.data.postsdata.repository.PostRepository
import ru.kozlovss.workingcontacts.domain.auth.AppAuth
import ru.kozlovss.workingcontacts.domain.error.mapExceptions
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class GetFeedPostsPagingDataUseCase @Inject constructor(
    private val postRepository: PostRepository,
    private val appAuth: AppAuth,
) {
    fun execute(): Flow<PagingData<Post>> = mapExceptions {
        return appAuth.authStateFlow
            .flatMapLatest { token ->
                postRepository.posts
                    .map { posts ->
                        posts.map { post ->
                            post.copy(ownedByMe = post.authorId == token?.id)
                        }
                    }
            }.flowOn(Dispatchers.Default)
    }
}