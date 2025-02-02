package data.model

import androidx.compose.ui.geometry.Offset
import java.time.LocalDateTime
import java.util.*

data class Habit(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val streak: Int = 0,
    val isCompleted: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    var position: Offset = Offset.Zero
) 