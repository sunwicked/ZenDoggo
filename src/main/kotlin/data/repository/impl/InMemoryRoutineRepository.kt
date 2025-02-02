class InMemoryRoutineRepository : RoutineRepository {
    private val routines = mutableMapOf<String, Routine>()

    override suspend fun getAllRoutines(): List<Routine> = routines.values.toList()
    
    override suspend fun getRoutine(id: String): Routine? = routines[id]
    
    override suspend fun createRoutine(routine: Routine) {
        routines[routine.id] = routine
    }
    
    override suspend fun updateRoutine(routine: Routine) {
        routines[routine.id] = routine
    }
    
    override suspend fun deleteRoutine(id: String) {
        routines.remove(id)
    }
    
    override suspend fun getRoutinesByType(type: RoutineType): List<Routine> {
        return routines.values.filter { it.type == type }
    }
} 