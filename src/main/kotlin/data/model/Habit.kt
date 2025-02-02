package data.model

import java.time.LocalDateTime
import java.util.UUID
import kotlin.time.Duration

data class Habit(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val streak: Int = 0,
    val isCompleted: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now()
) 