package com.lab.skuld.ui.screens

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lab.skuld.model.Event
import com.lab.skuld.model.MaybeEvent
import com.lab.skuld.model.maybeToEvent
import com.lab.skuld.ui.UIContextViewModel
import com.lab.skuld.ui.rememberLiveArray
import com.lab.skuld.ui.widget.Calendar
import com.lab.skuld.ui.widget.CalendarState
import com.lab.skuld.ui.widget.rememberCalendarState
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.plus
import kotlinx.datetime.toJavaLocalTime
import java.time.format.DateTimeFormatter

data class CalendarShownEvent(
    val event: Event,
    val shownOn: LocalDate,
    val shownTime: LocalTime?
)

data class CalendarHeader(
    val shownOn: LocalDate,
    val shownHour: Int?
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShowCalendarScreen() {
    val query = Firebase.firestore
        .collection("users/data/${Firebase.auth.currentUser!!.uid}")

    val events by rememberLiveArray(
        MaybeEvent::class.java,
        query,
        ::maybeToEvent
    )

    val uiContextViewModel: UIContextViewModel = viewModel()

    val calendarState = rememberCalendarState()

    val filteredEvents by remember {
        derivedStateOf {
            events.filter { event ->
                // (event.startDate != null && event.startDate.date <= calendarState.selectedDate) &&
                // (event.endDate != null && event.endDate.date >= calendarState.selectedDate) &&
                uiContextViewModel.searchBar.query.text.split(' ').all {
                    event.title.lowercase().contains(it.lowercase()) ||
                        (event.contents != null && event.contents.lowercase().contains(it.lowercase()))
                }
            }
        }
    }

    val aggregatedEvents by remember {
        derivedStateOf {
            filteredEvents.flatMap {
                buildList {
                    var currentDate = it.startDate!!.date
                    while (currentDate <= it.endDate!!.date) {
                        val associatedDateTime = when {
                            it.startDate.date == currentDate -> it.startDate.time
                            it.endDate.date == currentDate -> it.endDate.time
                            else -> null
                        }
                        this.add(CalendarShownEvent(it, currentDate, associatedDateTime))
                        currentDate = currentDate.plus(DatePeriod(days = 1))
                    }
                }
            }.sortedBy {
                LocalDateTime(it.shownOn, it.shownTime ?: LocalTime.fromMillisecondOfDay(0))
            }.groupBy {
                CalendarHeader(it.shownOn, it.shownTime?.hour)
            }
        }
    }

    val headerIndexes by remember {
        derivedStateOf {
            val m1 = mutableMapOf<LocalDate, Int>()
            val m2 = mutableListOf<Pair<Int, LocalDate>>()

            var idx = 0
            aggregatedEvents.forEach {
                if (m1[it.key.shownOn] == null) {
                    m1[it.key.shownOn] = idx
                    m2.add(Pair(idx, it.key.shownOn))
                }
                idx += it.value.size + 1
            }

            Pair(m1.toMap(), m2.toList())
        }
    }

    val scrollState = rememberLazyListState()
    LaunchedEffect(calendarState.selectedDate) {
        val idx = headerIndexes.first[calendarState.selectedDate]
        if (idx != null) {
            scrollState.animateScrollToItem(idx)
        }
    }

    val firstVisibleHeader by remember {
        derivedStateOf(structuralEqualityPolicy()) {
            var firstVisibleItem = scrollState.firstVisibleItemIndex
            headerIndexes.second.lastOrNull {
                firstVisibleItem >= it.first
            }?.second
        }
    }

    LaunchedEffect(firstVisibleHeader) {
        if (firstVisibleHeader != null) {
            calendarState.selectedDate = firstVisibleHeader!!
            calendarState.visibleDate = firstVisibleHeader!!
        }
    }

    Column {
        Calendar(calendarState)
        Divider()

        LazyColumn(
            state = scrollState,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            aggregatedEvents.iterator().forEach { entry ->
                stickyHeader(
                    key = Pair(entry.key.shownOn, entry.key.shownHour),
                    contentType = "HEADER"
                ) {
                    ShowHourHeader(entry.key.shownOn, entry.key.shownHour)
                }
                items(
                    entry.value,
                    key = { Pair(it.event, it.shownOn) },
                    contentType = { "ITEM" }
                ) {
                    ShowEvent(entry.key, it)
                }
            }

            /* This empty box makes sure that the list is scrollable past the last item */
            stickyHeader {}
            item {
                Box(modifier = Modifier.fillParentMaxSize()) {}
            }
        }
    }
}

fun formatTime(t: LocalTime): String = t.toJavaLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))

@Composable
fun ShowEvent(calendarHeader: CalendarHeader, calendarEvent: CalendarShownEvent) {
    val evt = calendarEvent.event
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            elevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(evt.title)
                    evt.contents?.let { Text(it) }
                }

                val s = evt.startDate
                val startString = when {
                    s != null && s.date != calendarHeader.shownOn -> "${s.date}"
                    s != null -> formatTime(s.time)
                    else -> ""
                }
                val e = evt.endDate
                val endString = when {
                    e != null && e.date != calendarHeader.shownOn -> "${e.date}"
                    e != null -> formatTime(e.time)
                    else -> ""
                }
                Text("$startString - $endString")
            }
        }
    }
}

@Composable
fun ShowHourHeader(date: LocalDate, hour: Int?) {
    val timeString = hour?.let { formatTime(LocalTime(hour, 0, 0, 0)) } ?: "All day"

    Box(
        modifier = Modifier
            .background(MaterialTheme.colors.onBackground)
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Text(timeString, modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colors.onSecondary, textAlign = TextAlign.Left)
        Text(date.toString(), modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colors.onSecondary, textAlign = TextAlign.Right)
    }
}
