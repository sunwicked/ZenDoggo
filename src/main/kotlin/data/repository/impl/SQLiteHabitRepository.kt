import data.model.Habit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SQLiteHabitRepository : HabitRepository {
    private val database = DatabaseManager.getDatabase()
    private val queries = database.habitQueries

    override suspend fun getAllHabits(): List<Habit> = withContext(Dispatchers.IO) {
        queries.getAllHabits().executeAsList().map { habit ->
            Habit(
                id = habit.id,
                name = habit.name,
                description = habit.description,
                streak = habit.streak.toInt(),
                isCompleted = habit.is_completed,
                createdAt = habit.created_at
            )
        }
    }

    override suspend fun getHabit(id: String): Habit? = withContext(Dispatchers.IO) {
        queries.getHabitById(id).executeAsOneOrNull()?.let { habit ->
            Habit(
                id = habit.id,
                name = habit.name,
                description = habit.description,
                streak = habit.streak.toInt(),
                isCompleted = habit.is_completed,
                createdAt = habit.created_at
            )
        }
    }

    override suspend fun createHabit(habit: Habit) {
        withContext(Dispatchers.IO) {
            queries.insertHabit(
                id = habit.id,
                name = habit.name,
                description = habit.description,
                streak = habit.streak.toLong(),
                is_completed = habit.isCompleted,
                created_at = habit.createdAt
            )
        }
    }

    override suspend fun updateHabit(habit: Habit) {
        withContext(Dispatchers.IO) {
            queries.updateHabit(
                is_completed = habit.isCompleted,
                streak = habit.streak.toLong(),
                id = habit.id
            )
        }
    }

    override suspend fun updateStreak(habitId: String, increment: Boolean) {
        withContext(Dispatchers.IO) {
            database.transaction {
                val habit = queries.getHabitById(habitId).executeAsOne()
                val newStreak = if (increment) habit.streak + 1 else 0
                queries.updateHabit(
                    is_completed = habit.is_completed,
                    streak = newStreak,
                    id = habitId
                )
            }
        }
    }

    override suspend fun deleteHabit(id: String) {
        withContext(Dispatchers.IO) {
            queries.deleteHabit(id)
        }
    }
} 