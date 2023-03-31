package ru.kozlovss.workingcontacts.data.dto

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
    }
}
