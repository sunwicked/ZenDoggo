import data.model.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SQLiteTaskRepository : TaskRepository {
    private val database = DatabaseManager.getDatabase()
    private val queries = database.taskQueries

    override suspend fun getAllTasks(): List<Task> = withContext(Dispatchers.IO) {
        queries.getAllTasks().executeAsList().map { task ->
            Task(
                id = task.id,
                name = task.name,
                description = task.description,
                isCompleted = task.is_completed,
                timeScheduled = task.scheduled_time,
                createdAt = task.created_at
            )
        }
    }

    override suspend fun getTask(id: String): Task? = withContext(Dispatchers.IO) {
        queries.getTaskById(id).executeAsOneOrNull()?.let { task ->
            Task(
                id = task.id,
                name = task.name,
                description = task.description,
                isCompleted = task.is_completed,
                timeScheduled = task.scheduled_time,
                createdAt = task.created_at
            )
        }
    }

    override suspend fun createTask(task: Task) {
        withContext(Dispatchers.IO) {
            queries.insertTask(
                id = task.id,
                name = task.name,
                description = task.description,
                is_completed = task.isCompleted,
                created_at = task.createdAt,
                scheduled_time = task.timeScheduled
            )
        }
    }

    override suspend fun updateTask(task: Task) {
        withContext(Dispatchers.IO) {
            queries.updateTask(
                is_completed = task.isCompleted,
                id = task.id
            )
        }
    }

    override suspend fun deleteTask(taskId: String) {
        withContext(Dispatchers.IO) {
            queries.deleteTask(taskId)
        }
    }
} 