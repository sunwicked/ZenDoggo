import data.model.Task
import data.model.Habit
import data.model.Routine
import data.model.RoutineType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.LocalTime

class SQLiteRoutineRepository : RoutineRepository {
    private val database = DatabaseManager.getDatabase()
    private val queries = database.routineQueries

    override suspend fun getAllRoutines(): List<Routine> = withContext(Dispatchers.IO) {
        queries.getAllRoutines().executeAsList().map { routine ->
            val items = queries.getRoutineWithItems(routine.id).executeAsList()
            val tasks = mutableListOf<Task>()
            val habits = mutableListOf<Habit>()
            
            items.forEach { item ->
                item.task_id?.let { taskId ->
                    tasks.add(Task(
                        id = taskId,
                        name = item.task_name!!,
                        description = "",
                        isCompleted = false,
                        createdAt = LocalDateTime.now()
                    ))
                }
                item.habit_id?.let { habitId ->
                    habits.add(Habit(
                        id = habitId,
                        name = item.habit_name!!,
                        description = "",
                        streak = 0,
                        isCompleted = false,
                        createdAt = LocalDateTime.now()
                    ))
                }
            }

            Routine(
                id = routine.id,
                name = routine.name,
                type = RoutineType.valueOf(routine.type),
                tasks = tasks,
                habits = habits,
                startTime = routine.start_time,
                endTime = routine.end_time
            )
        }
    }

    override suspend fun getRoutine(id: String): Routine? = withContext(Dispatchers.IO) {
        queries.getRoutineById(id).executeAsOneOrNull()?.let { routine ->
            val items = queries.getRoutineWithItems(routine.id).executeAsList()
            val tasks = mutableListOf<Task>()
            val habits = mutableListOf<Habit>()
            
            items.forEach { item ->
                item.task_id?.let { taskId ->
                    tasks.add(Task(
                        id = taskId,
                        name = item.task_name!!,
                        description = "",
                        isCompleted = false,
                        createdAt = LocalDateTime.now()
                    ))
                }
                item.habit_id?.let { habitId ->
                    habits.add(Habit(
                        id = habitId,
                        name = item.habit_name!!,
                        description = "",
                        streak = 0,
                        isCompleted = false,
                        createdAt = LocalDateTime.now()
                    ))
                }
            }

            Routine(
                id = routine.id,
                name = routine.name,
                type = RoutineType.valueOf(routine.type),
                tasks = tasks,
                habits = habits,
                startTime = routine.start_time,
                endTime = routine.end_time
            )
        }
    }

    override suspend fun getRoutinesByType(type: RoutineType): List<Routine> {
        // TODO: Add a query for filtering by type
        return getAllRoutines().filter { it.type == type }
    }

    override suspend fun createRoutine(routine: Routine) {
        withContext(Dispatchers.IO) {
            database.transaction {
                queries.insertRoutine(
                    id = routine.id,
                    name = routine.name,
                    type = routine.type.name,
                    start_time = routine.startTime,
                    end_time = routine.endTime,
                    created_at = LocalDateTime.now()
                )

                routine.tasks.forEach { task ->
                    queries.addTaskToRoutine(routine.id, task.id)
                }

                routine.habits.forEach { habit ->
                    queries.addHabitToRoutine(routine.id, habit.id)
                }
            }
        }
    }

    override suspend fun updateRoutine(routine: Routine) {
        withContext(Dispatchers.IO) {
            database.transaction {
                queries.updateRoutine(
                    name = routine.name,
                    type = routine.type.name,
                    start_time = routine.startTime,
                    end_time = routine.endTime,
                    id = routine.id
                )

                queries.deleteRoutineTaskLinks(routine.id)
                queries.deleteRoutineHabitLinks(routine.id)

                routine.tasks.forEach { task ->
                    queries.addTaskToRoutine(routine.id, task.id)
                }

                routine.habits.forEach { habit ->
                    queries.addHabitToRoutine(routine.id, habit.id)
                }
            }
        }
    }

    override suspend fun deleteRoutine(id: String) {
        withContext(Dispatchers.IO) {
            database.transaction {
                queries.deleteRoutineTaskLinks(id)
                queries.deleteRoutineHabitLinks(id)
                queries.deleteRoutine(id)
            }
        }
    }
} 