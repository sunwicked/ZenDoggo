import java.time.LocalDate

class InMemoryDayRepository : DayRepository {
    private val days = mutableMapOf<LocalDate, Day>()

    override suspend fun getDay(date: LocalDate): Day? = days[date]
    
    override suspend fun createDay(day: Day) {
        days[day.date] = day
    }
    
    override suspend fun updateDay(day: Day) {
        days[day.date] = day
    }
    
    override suspend fun getDaysInRange(startDate: LocalDate, endDate: LocalDate): List<Day> {
        return days.values.filter { day ->
            !day.date.isBefore(startDate) && !day.date.isAfter(endDate)
        }.sortedBy { it.date }
    }
} 