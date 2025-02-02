import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import data.model.Routine
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

@Composable
fun CalendarScreen(
    routineViewModel: RoutineViewModel,
    paddingValues: PaddingValues
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val routines by routineViewModel.routines.collectAsState()

    // Filter routines for selected date
    val routinesForSelectedDay = routines.filter { routine ->
        // TODO: Add proper date filtering logic based on your requirements
        true // For now, show all routines
    }

    LaunchedEffect(selectedDate) {
        routineViewModel.loadAllRoutines()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        // Month navigation
        MonthSelector(
            currentMonth = currentMonth,
            onPreviousMonth = { currentMonth = currentMonth.minusMonths(1) },
            onNextMonth = { currentMonth = currentMonth.plusMonths(1) }
        )

        // Calendar grid
        CalendarGrid(
            yearMonth = currentMonth,
            selectedDate = selectedDate,
            onDateSelected = { selectedDate = it }
        )

        // Daily overview
        DailyOverview(
            selectedDate = selectedDate,
            routines = routinesForSelectedDay,
            onEditRoutine = { /* Handle edit */ },
            onDeleteRoutine = { routineViewModel.deleteRoutine(it.id) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun MonthSelector(
    currentMonth: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousMonth) {
            Icon(Icons.Default.KeyboardArrowLeft, "Previous Month")
        }
        
        Text(
            text = currentMonth.format(java.time.format.DateTimeFormatter.ofPattern("MMMM yyyy")),
            style = MaterialTheme.typography.h6
        )
        
        IconButton(onClick = onNextMonth) {
            Icon(Icons.Default.KeyboardArrowRight, "Next Month")
        }
    }
}

@Composable
private fun CalendarGrid(
    yearMonth: YearMonth,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Weekday headers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            DayOfWeek.values().forEach { dayOfWeek ->
                Text(
                    text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.caption
                )
            }
        }

        // Calendar days
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            contentPadding = PaddingValues(4.dp)
        ) {
            // Add empty items for days before the first day of the month
            val firstDayOfMonth = yearMonth.atDay(1)
            val dayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
            items(dayOfWeek) {
                Box(modifier = Modifier.aspectRatio(1f))
            }

            // Add items for each day of the month
            items(yearMonth.lengthOfMonth()) { day ->
                val date = yearMonth.atDay(day + 1)
                CalendarDay(
                    date = date,
                    isSelected = date == selectedDate,
                    onDateSelected = onDateSelected
                )
            }
        }
    }
}

@Composable
private fun CalendarDay(
    date: LocalDate,
    isSelected: Boolean,
    onDateSelected: (LocalDate) -> Unit
) {
    val isToday = date == LocalDate.now()
    
    Card(
        modifier = Modifier
            .padding(2.dp)
            .aspectRatio(1f)
            .clickable { onDateSelected(date) },
        elevation = if (isSelected) 4.dp else 0.dp,
        border = if (isToday) BorderStroke(1.dp, MaterialTheme.colors.primary) else null,
        backgroundColor = if (isSelected) MaterialTheme.colors.primary else MaterialTheme.colors.surface
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                color = if (isSelected) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface
            )
        }
    }
}

@Composable
private fun DailyOverview(
    selectedDate: LocalDate,
    routines: List<Routine>,
    onEditRoutine: (Routine) -> Unit,
    onDeleteRoutine: (Routine) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = 2.dp
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            item {
                Text(
                    text = "Routines",
                    style = MaterialTheme.typography.subtitle1
                )
            }

            items(routines) { routine ->
                RoutineItem(
                    routine = routine,
                    onEditClick = { onEditRoutine(routine) },
                    onDeleteClick = { onDeleteRoutine(routine) }
                )
            }
        }
    }
} 