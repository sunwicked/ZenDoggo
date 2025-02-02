package data.model

import androidx.compose.ui.geometry.Offset
import java.time.LocalDateTime
import java.util.*

data class Task(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val priority: Priority = Priority.MEDIUM,
    val progress: Float = 0f,
    val timeScheduled: LocalDateTime? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    var position: Offset = Offset.Zero
) 