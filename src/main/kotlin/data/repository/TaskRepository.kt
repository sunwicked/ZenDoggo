import data.model.Task

interface TaskRepository {
    suspend fun getAllTasks(): List<Task>
    suspend fun getTask(id: String): Task?
    suspend fun createTask(task: Task)
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(id: String)
} 