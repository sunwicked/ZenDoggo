package data.model

import data.model.Task
import java.time.LocalTime
import java.util.UUID
import androidx.compose.ui.geometry.Offset

enum class RoutineType {
    MORNING, AFTERNOON, EVENING, NIGHT
}

data class Routine(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val type: RoutineType,
    val tasks: List<Task> = emptyList(),
    val habits: List<Habit> = emptyList(),
    val startTime: LocalTime? = null,
    val endTime: LocalTime? = null,
    var position: Offset = Offset.Zero  // For canvas positioning
) 