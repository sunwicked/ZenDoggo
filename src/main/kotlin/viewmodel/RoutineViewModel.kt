import data.model.Routine
import data.model.RoutineType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RoutineViewModel(
    private val routineRepository: RoutineRepository
) : BaseViewModel() {
    private val _routines = MutableStateFlow<List<Routine>>(emptyList())
    val routines: StateFlow<List<Routine>> = _routines.asStateFlow()

    private val _selectedType = MutableStateFlow<RoutineType>(RoutineType.MORNING)
    val selectedType: StateFlow<RoutineType> = _selectedType.asStateFlow()

    fun loadRoutines(type: RoutineType? = null) {
        viewModelScope.launch {
            _routines.value = if (type != null) {
                routineRepository.getRoutinesByType(type)
            } else {
                routineRepository.getAllRoutines()
            }
        }
    }

    fun setSelectedType(type: RoutineType) {
        _selectedType.value = type
        loadRoutines(type)
    }

    fun addRoutine(routine: Routine) {
        viewModelScope.launch {
            routineRepository.createRoutine(routine)
            loadRoutines(_selectedType.value)
        }
    }

    fun updateRoutine(routine: Routine) {
        viewModelScope.launch {
            routineRepository.updateRoutine(routine)
            loadRoutines(_selectedType.value)
        }
    }
} 