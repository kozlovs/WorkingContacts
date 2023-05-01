package ru.kozlovss.workingcontacts.entity

import com.google.gson.annotations.SerializedName

data class Coordinates(
    val lat: String,
    @SerializedName("long")
    val longitude: String
) {
    companion object {
        const val MAX_LATITUDE = 90.0
        const val MIN_LATITUDE = -90.0
        const val MAX_LONGITUDE = 180.0
        const val MIN_LONGITUDE = -180.0

        fun check(coordinates: Coordinates?): Boolean {
            if (coordinates == null) return true
            val lat = coordinates.lat.trim().toDoubleOrNull() ?: return false
            val lon = coordinates.longitude.trim().toDoubleOrNull() ?: return false
            if (lat > MAX_LATITUDE || lat < MIN_LATITUDE) return false
            if (lon > MAX_LONGITUDE || lon < MIN_LONGITUDE) return false
            return true
        }

        fun getDataFromFields(lat: String?, lon: String?): Coordinates? =
            if (lat.isNullOrBlank() || lon.isNullOrBlank()) null
            else Coordinates(lat.trim(), lon.trim())
    }
}
