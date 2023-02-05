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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.toLowerCase
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
import kotlinx.datetime.LocalTime
import kotlinx.datetime.toJavaLocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShowCalendarScreen() {
    val query = Firebase.firestore
        .collection("users/data/${Firebase.auth.currentUser!!.uid}")

    val events: List<Event> = rememberLiveArray(
        MaybeEvent::class.java,
        query,
        ::maybeToEvent
    )

    val uiContextViewModel: UIContextViewModel = viewModel()

    val calendarState = rememberCalendarState()

    val filteredEvents = events.filter { event ->
        (event.startDate != null && event.startDate.date <= calendarState.selectedDate) &&
        (event.endDate != null && event.endDate.date >= calendarState.selectedDate) &&
        uiContextViewModel.searchBar.query.text.split(' ').all {
            event.title.lowercase().contains(it.lowercase()) ||
                (event.contents != null && event.contents.lowercase().contains(it.lowercase()))
        }
    }.map {
        val associatedTime = when {
            it.startDate?.date == calendarState.selectedDate -> it.startDate.time
            it.endDate?.date == calendarState.selectedDate -> it.endDate.time
            else -> null
        }
        Pair(it, associatedTime)
    }.sortedBy { it.second }.groupBy { it.second?.hour }

    val scrollState = rememberLazyListState()
    LaunchedEffect(scrollState.isScrollInProgress) {

    }

    Column {
        Calendar(calendarState)
        Divider()

        LazyColumn(
            state = scrollState,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            filteredEvents.iterator().forEach { entry ->
                stickyHeader { ShowHourHeader(entry.key) }
                items(entry.value) { ShowEvent(it.first, calendarState) }
            }
        }
    }
}

fun formatTime(t: LocalTime): String = t.toJavaLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))

@Composable
fun ShowEvent(evt: Event, calendarState: CalendarState) {
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
                    s != null && s.date != calendarState.selectedDate -> "${s.date}"
                    s != null -> formatTime(s.time)
                    else -> ""
                }
                val e = evt.endDate
                val endString = when {
                    e != null && e.date != calendarState.selectedDate -> "${e.date}"
                    e != null -> formatTime(e.time)
                    else -> ""
                }
                Text("$startString - $endString")
            }
        }
    }
}

@Composable
fun ShowHourHeader(hour: Int?) {
    val timeString = hour?.let { formatTime(LocalTime(hour, 0, 0, 0)) } ?: "All day"

    Box(
        modifier = Modifier
            .background(MaterialTheme.colors.onBackground)
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Text(timeString, color = MaterialTheme.colors.onSecondary)
    }
}
