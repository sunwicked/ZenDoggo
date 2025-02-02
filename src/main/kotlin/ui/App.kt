import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp

@Composable
fun App(
    taskViewModel: TaskViewModel,
    habitViewModel: HabitViewModel,
    routineViewModel: RoutineViewModel
) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Tasks) }

    ZenDoggoTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("ZenDoggo") },
                    elevation = 4.dp
                )
            },
            bottomBar = {
                BottomNavigation {
                    BottomNavigationItem(
                        icon = { Icon(Icons.Default.CheckCircle, "Tasks") },
                        label = { Text("Tasks") },
                        selected = currentScreen == Screen.Tasks,
                        onClick = { currentScreen = Screen.Tasks }
                    )
                    BottomNavigationItem(
                        icon = { Icon(Icons.Default.Refresh, "Habits") },
                        label = { Text("Habits") },
                        selected = currentScreen == Screen.Habits,
                        onClick = { currentScreen = Screen.Habits }
                    )
                    BottomNavigationItem(
                        icon = { Icon(Icons.Default.List, "Routines") },
                        label = { Text("Routines") },
                        selected = currentScreen == Screen.Routines,
                        onClick = { currentScreen = Screen.Routines }
                    )
                    BottomNavigationItem(
                        icon = { Icon(Icons.Default.DateRange, "Calendar") },
                        label = { Text("Calendar") },
                        selected = currentScreen == Screen.Calendar,
                        onClick = { currentScreen = Screen.Calendar }
                    )
                }
            }
        ) { padding ->
            when (currentScreen) {
                Screen.Tasks -> TasksScreen(taskViewModel, padding)
                Screen.Habits -> HabitsScreen(habitViewModel, padding)
                Screen.Routines -> RoutinesScreen(routineViewModel, padding)
                Screen.Calendar -> CalendarScreen(padding)
                else -> {}
            }
        }
    }
} 