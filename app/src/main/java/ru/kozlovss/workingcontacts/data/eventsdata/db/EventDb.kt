package ru.kozlovss.workingcontacts.data.eventsdata.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.kozlovss.workingcontacts.data.entity.ListLongConverter
import ru.kozlovss.workingcontacts.data.entity.MapUsersPrevConverter
import ru.kozlovss.workingcontacts.data.eventsdata.dao.EventDao
import ru.kozlovss.workingcontacts.data.eventsdata.entity.EventEntity

@Database(
    entities = [EventEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(ListLongConverter::class, MapUsersPrevConverter::class)
abstract class EventDb : RoomDatabase() {

    abstract fun eventDao(): EventDao
}