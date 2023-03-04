package ru.kozlovss.workingcontacts.data.mywalldata.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.kozlovss.workingcontacts.data.entity.ListLongConverter
import ru.kozlovss.workingcontacts.data.entity.MapUsersPrevConverter
import ru.kozlovss.workingcontacts.data.mywalldata.dao.MyWallDao
import ru.kozlovss.workingcontacts.data.mywalldata.dao.MyWallRemoteKeyDao
import ru.kozlovss.workingcontacts.data.postsdata.entity.PostEntity
import ru.kozlovss.workingcontacts.data.postsdata.entity.PostRemoteKeyEntity

@Database(
    entities = [PostEntity::class, PostRemoteKeyEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(ListLongConverter::class, MapUsersPrevConverter::class)
abstract class MyWallDb: RoomDatabase() {
    abstract fun myWallDao(): MyWallDao

    abstract fun myWallRemoteKeyDao(): MyWallRemoteKeyDao
}