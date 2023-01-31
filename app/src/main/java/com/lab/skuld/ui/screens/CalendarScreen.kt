package com.lab.skuld.ui.screens

import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.himanshoe.kalendar.Kalendar
import com.himanshoe.kalendar.color.KalendarThemeColor
import com.himanshoe.kalendar.model.KalendarDay
import com.himanshoe.kalendar.model.KalendarEvent
import com.himanshoe.kalendar.model.KalendarType
import com.lab.skuld.ui.rememberLiveArray
import com.lab.skuld.ui.widget.Calendar
import kotlinx.datetime.LocalDate
import java.util.Date

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
    var currentDay: Date by remember { mutableStateOf(Date()) }

    ShowExpandableCalendar(
        events = events,
        onDateSelected = { day, _ ->
            currentDay = Date(day.localDate.year - 1900, day.localDate.monthNumber - 1, day.localDate.dayOfMonth)
         },
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        ) {
            documents.filter { doc ->
                val currentDate = currentDay
                val startDate = doc.startDate
                val endDate = doc.endDate
                when {
                    startDate != null && startDate.after(currentDate) -> false
                    endDate != null && endDate.before(currentDate) -> false
                    else -> true
                }
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

    Calendar()

    /*
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
                            takeMeToDate = currentDay,
                            kalendarThemeColor = KalendarThemeColor(
                                backgroundColor = MaterialTheme.colors.primaryVariant,
                                dayBackgroundColor = MaterialTheme.colors.secondary,
                                headerTextColor = MaterialTheme.colors.onBackground,
                            )
                        )

                        CalendarState.EXPANDED -> Kalendar(
                            kalendarType = KalendarType.Firey,
                            kalendarEvents = events,
                            onCurrentDayClick = onDateSelected,
                            takeMeToDate = currentDay,
                            kalendarThemeColor= KalendarThemeColor(
                                backgroundColor= MaterialTheme.colors.primaryVariant,
                                dayBackgroundColor = MaterialTheme.colors.secondary,
                                headerTextColor = MaterialTheme.colors.onBackground,
                            )

                        )
                    }
                    Spacer(modifier = Modifier.fillMaxWidth())
                    content()
                }
            }
        }
    }
     */
}

@Composable
fun ShowEvent(evt: Event) {
    Box {
        Text(evt.title)
        evt.contents?.let { Text(it) }
    }
}
