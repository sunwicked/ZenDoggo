import data.model.Routine
import data.model.RoutineType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RoutineViewModel(
    private val repository: RoutineRepository
) : BaseViewModel() {
    private val _routines = MutableStateFlow<List<Routine>>(emptyList())
    val routines: StateFlow<List<Routine>> = _routines.asStateFlow()

    private val _selectedType = MutableStateFlow<RoutineType>(RoutineType.MORNING)
    val selectedType: StateFlow<RoutineType> = _selectedType.asStateFlow()

    fun loadAllRoutines() {
        viewModelScope.launch {
            _routines.value = repository.getAllRoutines()
        }
    }

    fun loadRoutines(type: RoutineType) {
        viewModelScope.launch {
            _routines.value = repository.getRoutinesByType(type)
        }
    }

    fun setSelectedType(type: RoutineType) {
        _selectedType.value = type
        loadRoutines(type)
    }

    fun addRoutine(routine: Routine) {
        viewModelScope.launch {
            repository.createRoutine(routine)
            loadRoutines(routine.type)
        }
    }

    fun updateRoutine(routine: Routine) {
        viewModelScope.launch {
            repository.updateRoutine(routine)
            loadRoutines(routine.type)
        }
    }

    fun deleteRoutine(id: String) {
        viewModelScope.launch {
            val routine = repository.getRoutine(id)
            routine?.let {
                repository.deleteRoutine(id)
                loadRoutines(it.type)
            }
        }
    }
} 