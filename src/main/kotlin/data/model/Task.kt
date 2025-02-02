import java.time.LocalDateTime
import java.util.UUID
import kotlin.time.Duration

data class Task(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val timeScheduled: LocalDateTime? = null,
    val duration: Duration? = null,
    val isCompleted: Boolean = false,
    val isRecurring: Boolean = false
) 