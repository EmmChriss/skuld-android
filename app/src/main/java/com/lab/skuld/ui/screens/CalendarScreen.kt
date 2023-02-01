package com.lab.skuld.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lab.skuld.ui.rememberLiveArray
import com.lab.skuld.ui.widget.Calendar
import com.lab.skuld.ui.widget.rememberCalendarState
import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import java.util.Date

data class CalendarEvent(
    val start: LocalDateTime,
    val duration: DateTimePeriod,
    val title: String,
)

@Composable
fun ShowCalendarScreen() {
    val query = Firebase.firestore
        .collection("users/data/${Firebase.auth.currentUser!!.uid}")

    val documents: List<Event> = rememberLiveArray(
        MaybeEvent::class.java,
        query,
        ::maybeToEvent
    )
    val events by remember {
        derivedStateOf {
            documents.map { doc ->
                val date = (doc.startDate ?: doc.endDate!!).let {
                    LocalDate(it.year, it.month, it.day)
                }
                CalendarEvent(
                    start = date.atTime(LocalTime.fromSecondOfDay(10000)),
                    duration = DateTimePeriod(hours = 1),
                    title = doc.title
                )
            }
        }
    }

    val calendarState = rememberCalendarState()

    val shownEvents by remember {
        derivedStateOf {
            events.filter { evt ->
                val startDateTime = evt.start
                when (startDateTime.date) {
                    calendarState.selectedDate -> true
                    else -> false
                }
            }
        }
    }

    Calendar(calendarState)

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
    ) {
        shownEvents.forEach {
            ShowEvent(it)
        }
    }
}

@Composable
fun ShowEvent(evt: CalendarEvent) {
    Column {
        Text(evt.title)
        evt.title.let { Text(it) }
    }
}
