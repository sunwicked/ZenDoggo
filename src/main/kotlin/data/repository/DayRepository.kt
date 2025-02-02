import java.time.LocalDate

interface DayRepository {
    suspend fun getDay(date: LocalDate): Day?
    suspend fun createDay(day: Day)
    suspend fun updateDay(day: Day)
    suspend fun getDaysInRange(startDate: LocalDate, endDate: LocalDate): List<Day>
} 