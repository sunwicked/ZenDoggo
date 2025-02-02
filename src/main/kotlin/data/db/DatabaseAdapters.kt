import app.cash.sqldelight.ColumnAdapter
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset

/**
 * Adapters for converting between SQLite types and Kotlin types.
 * SQLDelight handles nullability at the database level based on schema definitions.
 */
object DatabaseAdapters {
    /**
     * Converts between SQLite INTEGER (Long) and LocalDateTime.
     * Used for both nullable and non-nullable columns:
     * - Non-nullable columns: created_at
     * - Nullable columns: scheduled_time
     */
    val dateTimeAdapter = object : ColumnAdapter<LocalDateTime, Long> {
        override fun decode(databaseValue: Long): LocalDateTime =
            LocalDateTime.ofEpochSecond(databaseValue, 0, ZoneOffset.UTC)
        
        override fun encode(value: LocalDateTime): Long =
            value.toEpochSecond(ZoneOffset.UTC)
    }

    /**
     * Converts between SQLite INTEGER (Long) and LocalTime.
     * Used for both nullable and non-nullable columns:
     * - Nullable columns: start_time, end_time in Routine
     * Stores time as seconds since midnight.
     */
    val timeAdapter = object : ColumnAdapter<LocalTime, Long> {
        override fun decode(databaseValue: Long): LocalTime =
            LocalTime.ofSecondOfDay(databaseValue)
        
        override fun encode(value: LocalTime): Long =
            value.toSecondOfDay().toLong()
    }
} 