package idv.neo.module.utils

import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.TimeZone.Companion.currentSystemDefault
import kotlinx.datetime.number
import kotlin.time.Instant

fun Long.toFormattedTime(): String {
    val instant = Instant.fromEpochMilliseconds(this)
    val dateTime = instant.toLocalDateTime(currentSystemDefault())
    return "${dateTime.hour.toString().padStart(2, '0')}:${dateTime.minute.toString().padStart(2, '0')}:${dateTime.second.toString().padStart(2, '0')}"
}

fun Long.toFormattedDate(): String {
    val instant = Instant.fromEpochMilliseconds(this)
    val dateTime = instant.toLocalDateTime(currentSystemDefault())
    return "${dateTime.year}-${dateTime.month.number.toString().padStart(2, '0')}-${dateTime.day.toString().padStart(2, '0')}"
}

fun Long.toFullFormattedTime(): String {
    val instant = Instant.fromEpochMilliseconds(this)
    val dateTime = instant.toLocalDateTime(currentSystemDefault())
    val ymd = "${dateTime.year}${dateTime.month.number.toString().padStart(2, '0')}${dateTime.day.toString().padStart(2, '0')}"
    val hms = "${dateTime.hour.toString().padStart(2, '0')}${dateTime.minute.toString().padStart(2, '0')}${dateTime.second.toString().padStart(2, '0')}"
    return "${ymd}${hms}"
}

expect fun getCurrentTimeMillis(): Long
