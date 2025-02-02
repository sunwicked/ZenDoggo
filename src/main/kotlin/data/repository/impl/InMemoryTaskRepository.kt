class InMemoryTaskRepository : TaskRepository {
    private val tasks = mutableMapOf<String, Task>()

    override suspend fun getAllTasks(): List<Task> = tasks.values.toList()
    
    override suspend fun getTask(id: String): Task? = tasks[id]
    
    override suspend fun createTask(task: Task) {
        tasks[task.id] = task
    }
    
    override suspend fun updateTask(task: Task) {
        tasks[task.id] = task
    }
    
    override suspend fun deleteTask(id: String) {
        tasks.remove(id)
    }
} 