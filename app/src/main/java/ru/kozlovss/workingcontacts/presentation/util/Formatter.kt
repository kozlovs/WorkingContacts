package ru.kozlovss.workingcontacts.presentation.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object Formatter {

    private val POST_DATE_FORMAT = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy")
    private val JOB_DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    fun numberToShortFormat(number: Int): String {
        return when {
            number in  1_000..1_099 -> "1K"
            number in  1_100..9_999 && number % 1000 == 0 -> "${number / 1000}K"
            number in  1_100..9_999 && number % 1000 != 0 -> ((number / 100).toDouble() / 10).toString() + "K"
            number in 10_000..999_999 -> "${number / 1000}K"
            number >= 1_000_000 && number % 1000000 == 0 -> "${number / 1000000}M"
            number >= 1_000_000 && number % 1000000 != 0 -> ((number / 100000).toDouble() / 10).toString() + "M"
            else -> number.toString()
        }
    }

    fun localDateTimeToPostDateFormat(published: String): String {
        val dateTime = LocalDateTime.parse(published.substring(0, 19))
        val now = LocalDateTime.now()
        val secondsAgo = dateTime.until(now, ChronoUnit.SECONDS)
        return when (secondsAgo) {
            in 0..60 -> "только что"
            in 61..(60 * 60) -> "${minutesToText(dateTime.minute)} назад"
            in (60 * 60 + 1)..(24 * 60 * 60) -> "${hoursToText(dateTime.hour)} назад"
            else -> dateTimeToFormatString(dateTime)
        }
    }

    private fun dateTimeToFormatString(published: LocalDateTime) = published.format(POST_DATE_FORMAT)
    private fun minutesToText(minutes: Int): String {
        if (minutes in 11..14)
            return "$minutes минут"
        return when(minutes % 10) {
            1 -> "$minutes минуту"
            2, 3, 4 -> "$minutes минуты"
            else -> "$minutes минут"
        }
    }

    private fun hoursToText(hours: Int): String {
        if (hours in 11..14)
            return "$hours часов"
        return when(hours % 10) {
            1 -> "$hours час"
            2, 3, 4 -> "$hours часа"
            else -> "$hours часов"
        }
    }

    fun localDateTimeToJobDateFormat(published: String): String {
        val dateTime = LocalDateTime.parse(published.substring(0, 19))
        return dateTime.format(JOB_DATE_FORMAT)
    }
}