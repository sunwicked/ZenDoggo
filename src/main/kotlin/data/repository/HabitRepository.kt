interface HabitRepository {
    suspend fun getAllHabits(): List<Habit>
    suspend fun getHabit(id: String): Habit?
    suspend fun createHabit(habit: Habit)
    suspend fun updateHabit(habit: Habit)
    suspend fun deleteHabit(id: String)
    suspend fun updateStreak(id: String, increment: Boolean)
} 