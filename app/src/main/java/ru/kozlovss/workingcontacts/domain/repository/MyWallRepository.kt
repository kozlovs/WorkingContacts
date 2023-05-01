package ru.kozlovss.workingcontacts.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.kozlovss.workingcontacts.data.postsdata.dto.Post

interface MyWallRepository {
    val posts: Flow<PagingData<Post>>
}