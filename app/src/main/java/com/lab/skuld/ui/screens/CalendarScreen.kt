package com.lab.skuld.ui.screens

import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.type.DateTime
import com.himanshoe.kalendar.Kalendar
import com.himanshoe.kalendar.model.KalendarType
import com.lab.skuld.ui.rememberLiveArray
import androidx.compose.material.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.google.firebase.Timestamp
import com.himanshoe.kalendar.model.KalendarDay
import com.himanshoe.kalendar.model.KalendarEvent
import kotlinx.datetime.LocalDate
import java.time.Instant
import java.util.Date

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
    val startDate: Date?,
    val endDate: Date?,
    val title: String,
    val checked: Boolean?,
    val contents: String?,
)

fun maybeToEvent(maybe: MaybeEvent): Event? {
    if ((maybe.startDate == null && maybe.endDate == null) || maybe.title == null)
        return null

    return Event(
        maybe.id,
        maybe.startDate?.toDate(),
        maybe.endDate?.toDate(),
        maybe.title,
        maybe.checked,
        maybe.contents
    )
}

@Composable
fun ShowCalendarScreen() {
    val query = Firebase.firestore
        .collection("users/data/${Firebase.auth.currentUser!!.uid}")

    val documents: List<Event> = rememberLiveArray(
        MaybeEvent::class.java,
        query,
        ::maybeToEvent
    )
    val events = documents.map { doc ->
        val date = (doc.startDate ?: doc.endDate!!).let {
            LocalDate(it.year, it.month, it.day)
        }
        KalendarEvent(date, doc.title, doc.contents)
    }
    var currentDay: Date by remember { mutableStateOf(Date.from(Instant.now())) }

    ShowExpandableCalendar(
        events = events,
        onDateSelected = { day, _ ->
            currentDay = Date(day.localDate.year, day.localDate.monthNumber, day.localDate.dayOfMonth)
        },
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        ) {
            documents.filter { doc ->
                val start = doc.startDate?.let { listOf(it.year, it.month, it.day) }
                val end = doc.endDate?.let { listOf(it.year, it.month, it.day) }
                val current = listOf(currentDay.year, currentDay.month, currentDay)
                if (doc.startDate != null && !currentDay.after(doc.startDate))
                    false
                else if (doc.startDate != null && !currentDay.after(doc.startDate))
                    false
                else true
            }.forEach {
                ShowEvent(it)
            }
        }
    }
}

enum class CalendarState {
    COLLAPSED,
    EXPANDED
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
@Preview
private fun ShowExpandableCalendar(
    currentDay: LocalDate? = null,
    events: List<KalendarEvent> = listOf(),
    onDateSelected: (KalendarDay, List<KalendarEvent>) -> Unit = {_, _ ->},
    content: @Composable () -> Unit = {}) {

    val swipeableState = rememberSwipeableState(CalendarState.COLLAPSED)
    val anchors = mapOf(0f to CalendarState.COLLAPSED, 1f to CalendarState.EXPANDED)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .swipeable(
                state = swipeableState,
                anchors = anchors,
                thresholds = { _, _ -> FractionalThreshold(0.3f) },
                orientation = Orientation.Vertical
            )
    ) {
        Crossfade(
            targetState = swipeableState.targetValue
        ) { v ->
            Box(
                modifier = Modifier
                    .animateContentSize()
                    .fillMaxHeight(swipeableState.progress.fraction)
            ) {
                Column {
                    when (v) {
                        CalendarState.COLLAPSED -> Kalendar(
                            kalendarType = KalendarType.Oceanic(true),
                            kalendarEvents = events,
                            onCurrentDayClick = onDateSelected,
                            takeMeToDate = currentDay
                        )

                        CalendarState.EXPANDED -> Kalendar(
                            kalendarType = KalendarType.Firey,
                            kalendarEvents = events,
                            onCurrentDayClick = onDateSelected,
                            takeMeToDate = currentDay
                        )
                    }
                    Spacer(modifier = Modifier.fillMaxWidth())
                    content()
                }
            }
        }
    }
}

@Composable
fun ShowEvent(evt: Event) {
    Box {
        Text(evt.title)
        evt.contents?.let { Text(it) }
    }
}
