import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    // Initialize repositories
    val taskRepository = InMemoryTaskRepository()
    val habitRepository = InMemoryHabitRepository()
    val routineRepository = InMemoryRoutineRepository()

    // Initialize ViewModels
    val taskViewModel = TaskViewModel(taskRepository)
    val habitViewModel = HabitViewModel(habitRepository)
    val routineViewModel = RoutineViewModel(routineRepository)

    Window(
        onCloseRequest = ::exitApplication,
        title = "ZenDoggo"
    ) {
        App(
            taskViewModel = taskViewModel,
            habitViewModel = habitViewModel,
            routineViewModel = routineViewModel
        )
    }
}
