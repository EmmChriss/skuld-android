package com.lab.skuld.ui.widget

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn

val weekDayStrings = listOf("M", "T", "W", "T", "F", "S", "S")

enum class CalendarType {
    WEEKLY,
    MONTHLY;

    fun other(): CalendarType {
        return if (this == WEEKLY) {
            MONTHLY
        } else {
            WEEKLY
        }
    }
}

class CalendarState(defaultDate: LocalDate?) {
    val today: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
    var selectedDate by mutableStateOf(defaultDate ?: today, structuralEqualityPolicy())
    var visibleDate by mutableStateOf(defaultDate ?: today, structuralEqualityPolicy())
    var calendarType by mutableStateOf(CalendarType.WEEKLY)

    val calendarInterval get() = when (calendarType) {
        CalendarType.WEEKLY -> DatePeriod(days = 7)
        CalendarType.MONTHLY -> DatePeriod(months = 1)
    }
}

@Composable
fun rememberCalendarState(): CalendarState {
    return remember { CalendarState(null) }
}

@Composable
fun Calendar(
    state: CalendarState = rememberCalendarState()
) {
    // TODO: don't assume week starts on Monday
    fun getStartOfWeek(currentDate: LocalDate): LocalDate {
        return currentDate.minus(DatePeriod(days = currentDate.dayOfWeek.value - 1))
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CalendarHeader(state)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            weekDayStrings.forEach {
                Text(it, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().weight(1f))
            }
        }

        when (state.calendarType) {
            CalendarType.WEEKLY -> CalendarBodyWeekly(state)
            CalendarType.MONTHLY -> CalendarBodyMonthly(state)
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CalendarHeader(state: CalendarState) {
    Box (
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        AnimatedContent(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight()
                .align(Alignment.CenterStart)
                .padding(start = 8.dp),
            targetState = getFirstDateOfMonth(state.visibleDate),
            transitionSpec = {
                val duration = 500
                val direction = if (state.visibleDate.dayOfMonth > 15) 1 else -1

                val slideIn = slideInVertically(animationSpec = tween(durationMillis = duration))
                    { height -> direction * height } + fadeIn(animationSpec = tween(durationMillis = duration))

                val slideOut = slideOutVertically(animationSpec = tween(durationMillis = duration))
                    { height -> -direction * height } + fadeOut(animationSpec = tween(durationMillis = duration))

                slideIn with slideOut
            }
        ) { date ->
            Text(
                getDateHeaderString(date),
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        Row(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight()
                .align(Alignment.CenterEnd)
        ) {
            IconButton(
                onClick = { state.calendarType = state.calendarType.other() }
            ) { Icon(Icons.Filled.DateRange, "Select date") }
            IconButton(
                onClick = { state.visibleDate = state.visibleDate.minus(state.calendarInterval) }
            ) { Icon(Icons.Filled.KeyboardArrowLeft, "Previous week") }
            IconButton(
                onClick = { state.visibleDate = state.visibleDate.plus(state.calendarInterval) }
            ) { Icon(Icons.Filled.KeyboardArrowRight, "Next week") }
        }
    }
}

@Composable
fun CalendarBodyWeekly(state: CalendarState) {
    val startOfWeek = getFirstDateOfWeek(state.visibleDate)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        (0..6).forEach { i ->
            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                val date = startOfWeek.plus(DatePeriod(days = i))
                DayButton(state, date)
            }
        }
    }
}

@Composable
fun CalendarBodyMonthly(state: CalendarState) {
    val firstDayOfMonth = getFirstDateOfMonth(state.visibleDate)
    val lastDayOfMonth = firstDayOfMonth.plus(DatePeriod(months = 1)).minus(DatePeriod(days = 1))
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        columns = GridCells.Fixed(7),
        userScrollEnabled = false,
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        items(firstDayOfMonth.dayOfWeek.ordinal) {
            DayButton(state, firstDayOfMonth.minus(DatePeriod(days = 7 - it - 1)), true)
        }
        items(lastDayOfMonth.dayOfMonth) {
            DayButton(state, firstDayOfMonth.plus(DatePeriod(days = it)))
        }
        items(7 - lastDayOfMonth.dayOfWeek.ordinal - 1) {
            DayButton(state, lastDayOfMonth.plus(DatePeriod(days = it + 1)), true)
        }
    }
}

@Composable
fun DayButton(state: CalendarState, date: LocalDate, background: Boolean = false) {
    val backgroundAlpha = if (background) 0.5f else 1.0f
    var backgroundColor: Color
    var textColor: Color
    if (date == state.today) {
        backgroundColor = MaterialTheme.colors.secondary.copy(alpha = backgroundAlpha)
        textColor = MaterialTheme.colors.onPrimary.copy(alpha = backgroundAlpha)
    } else if (date == state.selectedDate) {
        backgroundColor = MaterialTheme.colors.primary.copy(alpha = backgroundAlpha)
        textColor = MaterialTheme.colors.onBackground.copy(alpha = backgroundAlpha)
    } else {
        backgroundColor = Color.Unspecified
        textColor = MaterialTheme.colors.onBackground.copy(alpha = backgroundAlpha)
    }

    IconButton(
        modifier = Modifier.background(backgroundColor, CircleShape),
        onClick = { state.selectedDate = date }
    ) { Text(date.dayOfMonth.toString(), color = textColor) }
}

fun getDateHeaderString(currentWeek: LocalDate): String {
    return "${currentWeek.month} ${currentWeek.year}"
}

fun getFirstDateOfWeek(date: LocalDate): LocalDate {
    return date.minus(DatePeriod(days = date.dayOfWeek.ordinal))
}

fun getFirstDateOfMonth(date: LocalDate): LocalDate {
    return date.minus(DatePeriod(days = date.dayOfMonth - 1))
}
