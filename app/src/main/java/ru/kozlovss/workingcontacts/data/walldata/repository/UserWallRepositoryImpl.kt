package ru.kozlovss.workingcontacts.data.walldata.repository

import ru.kozlovss.workingcontacts.data.extensions.checkAndGetBody
import ru.kozlovss.workingcontacts.data.walldata.api.UserWallApiService
import ru.kozlovss.workingcontacts.domain.repository.UserWallRepository
import javax.inject.Inject

class UserWallRepositoryImpl @Inject constructor(
    private val wallApiService: UserWallApiService
) : UserWallRepository {
    override suspend fun getAll(id: Long) = wallApiService.getWallByUserId(id).checkAndGetBody()
}