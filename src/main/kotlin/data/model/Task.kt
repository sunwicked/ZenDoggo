package data.model

import java.time.LocalDateTime
import java.util.UUID

data class Task(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val timeScheduled: LocalDateTime? = null,
    val createdAt: LocalDateTime = LocalDateTime.now()
) 