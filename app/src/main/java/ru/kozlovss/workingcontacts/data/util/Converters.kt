package ru.kozlovss.workingcontacts.data.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.kozlovss.workingcontacts.data.userdata.dto.UserPreview

class ListLongConverter {
    @TypeConverter
    fun toJson(list: List<Long>) = Gson().toJson(list)

    @TypeConverter
    fun fromJson(json: String): List<Long> {
        return Gson().fromJson(json, object : TypeToken<List<Long>>() {}.type)
    }
}

class MapUsersPrevConverter {
    @TypeConverter
    fun toJson(map: Map<Long, UserPreview>) = Gson().toJson(map)

    @TypeConverter
    fun fromJson(json: String): Map<Long, UserPreview> {
        return Gson().fromJson(json, object : TypeToken<Map<Long, UserPreview>>() {}.type)
    }
}