import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import org.zendoggo.db.ZenDoggoDatabase
import java.io.File

object DatabaseManager {
    private const val DB_FILE = "zendoggo.db"
    private lateinit var database: ZenDoggoDatabase

    fun initialize() {
        val dbFile = File(DB_FILE)
        val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:${dbFile.absolutePath}")
        
        if (!dbFile.exists()) {
            ZenDoggoDatabase.Schema.create(driver)
        }
        
        database = ZenDoggoDatabase(
            driver = driver,
            taskAdapter = org.zendoggo.db.Task.Adapter(
                created_atAdapter = DatabaseAdapters.dateTimeAdapter,
                scheduled_timeAdapter = DatabaseAdapters.dateTimeAdapter
            ),
            habitAdapter = org.zendoggo.db.Habit.Adapter(
                created_atAdapter = DatabaseAdapters.dateTimeAdapter
            ),
            routineAdapter = org.zendoggo.db.Routine.Adapter(
                start_timeAdapter = DatabaseAdapters.timeAdapter,
                end_timeAdapter = DatabaseAdapters.timeAdapter,
                created_atAdapter = DatabaseAdapters.dateTimeAdapter
            )
        )
    }

    fun getDatabase(): ZenDoggoDatabase {
        if (!::database.isInitialized) {
            initialize()
        }
        return database
    }
} 