package ru.kozlovss.workingcontacts.data.dto

import com.google.gson.annotations.SerializedName

data class Coordinates(
    val lat: String,
    @SerializedName("long")
    val longitude: String
)
