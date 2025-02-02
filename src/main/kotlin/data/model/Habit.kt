import java.time.LocalDateTime
import java.util.*
import kotlin.time.Duration

data class Habit(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val timeScheduled: LocalDateTime? = null,
    val duration: Duration? = null,
    val streak: Int = 0,
    val isCompleted: Boolean = false
) 