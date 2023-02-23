package ru.kozlovss.workingcontacts.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import ru.kozlovss.workingcontacts.data.dao.EventDao
import ru.kozlovss.workingcontacts.data.dao.PostDao
import ru.kozlovss.workingcontacts.data.entity.EventEntity
import ru.kozlovss.workingcontacts.data.entity.ListLongConverter
import ru.kozlovss.workingcontacts.data.entity.MapUsersPrevConverter
import ru.kozlovss.workingcontacts.data.entity.PostEntity

@Database(
    entities = [PostEntity::class, EventEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(ListLongConverter::class, MapUsersPrevConverter::class)
abstract class Db : RoomDatabase() {
    abstract fun postDao(): PostDao

    abstract fun eventDao(): EventDao
}