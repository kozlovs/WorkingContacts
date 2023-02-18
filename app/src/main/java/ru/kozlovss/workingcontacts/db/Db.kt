package ru.kozlovss.workingcontacts.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.kozlovss.workingcontacts.dao.EventDao
import ru.kozlovss.workingcontacts.dao.PostDao
import ru.kozlovss.workingcontacts.entity.EventEntity
import ru.kozlovss.workingcontacts.entity.PostEntity

@Database(
    entities = [PostEntity::class, EventEntity::class],
    version = 1,
    exportSchema = false
)
abstract class Db: RoomDatabase() {
    abstract fun postDao(): PostDao

    abstract fun eventDao(): EventDao
}