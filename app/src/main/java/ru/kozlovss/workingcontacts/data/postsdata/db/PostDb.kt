package ru.kozlovss.workingcontacts.data.postsdata.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.kozlovss.workingcontacts.data.eventsdata.dao.EventDao
import ru.kozlovss.workingcontacts.data.postsdata.dao.PostDao
import ru.kozlovss.workingcontacts.data.entity.ListLongConverter
import ru.kozlovss.workingcontacts.data.entity.MapUsersPrevConverter
import ru.kozlovss.workingcontacts.data.eventsdata.entity.EventEntity
import ru.kozlovss.workingcontacts.data.postsdata.dao.PostRemoteKeyDao
import ru.kozlovss.workingcontacts.data.postsdata.entity.PostEntity
import ru.kozlovss.workingcontacts.data.postsdata.entity.PostRemoteKeyEntity

@Database(
    entities = [PostEntity::class, PostRemoteKeyEntity::class, EventEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(ListLongConverter::class, MapUsersPrevConverter::class)
abstract class PostDb : RoomDatabase() {
    abstract fun postDao(): PostDao

    abstract fun postRemoteKeyDao(): PostRemoteKeyDao

    abstract fun eventDao(): EventDao
}