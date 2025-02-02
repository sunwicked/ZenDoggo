package data.db

import app.cash.sqldelight.db.SqlDriver
import org.zendoggo.db.ZenDoggoDatabase
import org.zendoggo.db.Task
import org.zendoggo.db.Habit
import org.zendoggo.db.Routine

interface Database {
    val database: ZenDoggoDatabase

    companion object {
        fun create(driver: SqlDriver): Database {
            return DatabaseImpl(
                ZenDoggoDatabase(
                    driver = driver,
                    taskAdapter = Task.Adapter(
                        created_atAdapter = DatabaseAdapters.dateTimeAdapter,
                        scheduled_timeAdapter = DatabaseAdapters.dateTimeAdapter
                    ),
                    habitAdapter = Habit.Adapter(
                        created_atAdapter = DatabaseAdapters.dateTimeAdapter
                    ),
                    routineAdapter = Routine.Adapter(
                        start_timeAdapter = DatabaseAdapters.timeAdapter,
                        end_timeAdapter = DatabaseAdapters.timeAdapter,
                        created_atAdapter = DatabaseAdapters.dateTimeAdapter
                    )
                )
            )
        }
    }
}

private class DatabaseImpl(
    override val database: ZenDoggoDatabase
) : Database 