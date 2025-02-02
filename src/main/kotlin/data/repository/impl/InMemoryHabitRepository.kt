import data.model.Habit

class InMemoryHabitRepository : HabitRepository {
    private val habits = mutableMapOf<String, Habit>()

    override suspend fun getAllHabits(): List<Habit> = habits.values.toList()
    
    override suspend fun getHabit(id: String): Habit? = habits[id]
    
    override suspend fun createHabit(habit: Habit) {
        habits[habit.id] = habit
    }
    
    override suspend fun updateHabit(habit: Habit) {
        habits[habit.id] = habit
    }
    
    override suspend fun deleteHabit(id: String) {
        habits.remove(id)
    }
    
    override suspend fun updateStreak(id: String, increment: Boolean) {
        habits[id]?.let { habit ->
            val newStreak = if (increment) habit.streak + 1 else 0
            habits[id] = habit.copy(streak = newStreak)
        }
    }
} 