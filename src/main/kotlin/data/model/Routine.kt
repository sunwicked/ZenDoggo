import java.time.LocalTime
import java.util.UUID

enum class RoutineType {
    MORNING, AFTERNOON, EVENING, NIGHT, CUSTOM
}

data class Routine(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val type: RoutineType,
    val tasks: List<Task> = emptyList(),
    val habits: List<Habit> = emptyList(),
    val startTime: LocalTime? = null,
    val endTime: LocalTime? = null
) 