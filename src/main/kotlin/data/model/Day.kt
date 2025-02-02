import java.time.LocalDate
import java.util.*

data class Day(
    val id: String = UUID.randomUUID().toString(),
    val date: LocalDate,
    val routines: List<Routine> = emptyList()
) {
    val progress: Float
        get() {
            val totalItems = routines.sumOf { routine ->
                routine.tasks.size + routine.habits.size
            }
            if (totalItems == 0) return 0f
            
            val completedItems = routines.sumOf { routine ->
                routine.tasks.count { it.isCompleted } + 
                routine.habits.count { it.isCompleted }
            }
            
            return completedItems.toFloat() / totalItems
        }
} 