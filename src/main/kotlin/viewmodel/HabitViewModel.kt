import data.model.Habit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HabitViewModel(
    private val habitRepository: HabitRepository
) : BaseViewModel() {
    private val _habits = MutableStateFlow<List<Habit>>(emptyList())
    val habits: StateFlow<List<Habit>> = _habits.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadHabits() {
        viewModelScope.launch {
            _isLoading.value = true
            _habits.value = habitRepository.getAllHabits()
            _isLoading.value = false
        }
    }

    fun addHabit(habit: Habit) {
        viewModelScope.launch {
            habitRepository.createHabit(habit)
            loadHabits()
        }
    }

    fun updateHabit(habit: Habit) {
        viewModelScope.launch {
            habitRepository.updateHabit(habit)
            loadHabits()
        }
    }

    fun updateStreak(habitId: String, increment: Boolean) {
        viewModelScope.launch {
            habitRepository.updateStreak(habitId, increment)
            loadHabits()
        }
    }
} 