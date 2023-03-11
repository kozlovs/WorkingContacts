package ru.kozlovss.workingcontacts.data.walldata.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.kozlovss.workingcontacts.data.entity.ListLongConverter
import ru.kozlovss.workingcontacts.data.entity.MapUsersPrevConverter
import ru.kozlovss.workingcontacts.data.postsdata.entity.PostEntity
import ru.kozlovss.workingcontacts.data.postsdata.entity.PostRemoteKeyEntity
import ru.kozlovss.workingcontacts.data.walldata.dao.UserWallDao
import ru.kozlovss.workingcontacts.data.walldata.dao.UserWallRemoteKeyDao

@Database(
    entities = [PostEntity::class, PostRemoteKeyEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(ListLongConverter::class, MapUsersPrevConverter::class)
abstract class UserWallDb: RoomDatabase() {
    abstract fun userWallDao(): UserWallDao

    abstract fun userWallRemoteKeyDao(): UserWallRemoteKeyDao
}