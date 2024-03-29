package ru.kozlovss.workingcontacts.data.postsdata.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.kozlovss.workingcontacts.data.postsdata.dao.PostDao
import ru.kozlovss.workingcontacts.data.util.ListLongConverter
import ru.kozlovss.workingcontacts.data.util.MapUsersPrevConverter
import ru.kozlovss.workingcontacts.data.postsdata.dao.PostRemoteKeyDao
import ru.kozlovss.workingcontacts.data.postsdata.entity.PostEntity
import ru.kozlovss.workingcontacts.data.postsdata.entity.PostRemoteKeyEntity

@Database(
    entities = [PostEntity::class, PostRemoteKeyEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(ListLongConverter::class, MapUsersPrevConverter::class)
abstract class PostDb : RoomDatabase() {
    abstract fun postDao(): PostDao

    abstract fun postRemoteKeyDao(): PostRemoteKeyDao
}