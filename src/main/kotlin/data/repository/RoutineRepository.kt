interface RoutineRepository {
    suspend fun getAllRoutines(): List<Routine>
    suspend fun getRoutine(id: String): Routine?
    suspend fun createRoutine(routine: Routine)
    suspend fun updateRoutine(routine: Routine)
    suspend fun deleteRoutine(id: String)
    suspend fun getRoutinesByType(type: RoutineType): List<Routine>
} 