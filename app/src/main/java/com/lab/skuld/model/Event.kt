package com.lab.skuld.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

class MaybeEvent {
    @DocumentId
    val id = ""
    val startDate: Timestamp? = null
    val endDate: Timestamp? = null
    val title: String? = null
    val checked: Boolean? = null
    val contents: String? = null
}

data class Event(
    val id: String,
    val startDate: LocalDateTime?,
    val endDate: LocalDateTime?,
    val title: String,
    val checked: Boolean?,
    val contents: String?,
)

data class CreateEvent(
    val startDate: LocalDateTime?,
    val endDate: LocalDateTime?,
    val title: String,
    val checked: Boolean?,
    val contents: String?,
)

fun timestampToDateTime(timestamp: Timestamp): LocalDateTime {
    val instant = java.time.Instant.ofEpochSecond(timestamp.seconds, timestamp.nanoseconds.toLong())
    val dateTime = java.time.LocalDateTime.ofInstant(instant, java.time.ZoneOffset.UTC)
    return LocalDateTime(dateTime.year, dateTime.month, dateTime.dayOfMonth, dateTime.hour, dateTime.minute, dateTime.second, dateTime.nano)
}

fun dateTimeToTimestamp(dateTime: LocalDateTime): Timestamp {
    val instant = dateTime.toInstant(TimeZone.UTC)
    return Timestamp(instant.epochSeconds, instant.nanosecondsOfSecond)
}

fun maybeToEvent(maybe: MaybeEvent): Event? {
    if ((maybe.startDate == null && maybe.endDate == null) || maybe.title == null)
        return null

    return Event(
        maybe.id,
        maybe.startDate?.let { timestampToDateTime(it) },
        maybe.endDate?.let { timestampToDateTime(it) },
        maybe.title,
        maybe.checked,
        maybe.contents
    )
}
