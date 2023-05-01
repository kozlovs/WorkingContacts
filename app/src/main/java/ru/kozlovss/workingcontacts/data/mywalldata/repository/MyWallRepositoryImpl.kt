package ru.kozlovss.workingcontacts.data.mywalldata.repository

import androidx.paging.*
import kotlinx.coroutines.flow.map
import ru.kozlovss.workingcontacts.data.mywalldata.api.MyWallApiService
import ru.kozlovss.workingcontacts.data.mywalldata.dao.MyWallDao
import ru.kozlovss.workingcontacts.data.mywalldata.dao.MyWallRemoteKeyDao
import ru.kozlovss.workingcontacts.data.mywalldata.db.MyWallDb
import ru.kozlovss.workingcontacts.data.postsdata.entity.PostEntity
import ru.kozlovss.workingcontacts.domain.repository.MyWallRepository
import javax.inject.Inject

class MyWallRepositoryImpl @Inject constructor(
    private val dao: MyWallDao,
    wallApiService: MyWallApiService,
    remoteKeyDao: MyWallRemoteKeyDao,
    db: MyWallDb
) : MyWallRepository {

    @OptIn(ExperimentalPagingApi::class)
    override val posts = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        pagingSourceFactory = dao::pagingSource,
        remoteMediator = MyWallRemoteMediator(
            wallApiService,
            dao,
            remoteKeyDao,
            db
        )
    ).flow.map { it.map(PostEntity::toDto) }
}