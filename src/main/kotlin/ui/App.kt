import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp

@Composable
fun App(
    taskViewModel: TaskViewModel,
    habitViewModel: HabitViewModel,
    routineViewModel: RoutineViewModel
) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Canvas) }

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
                        icon = { Icon(Icons.Outlined.Dashboard, "Canvas") },
                        label = { Text("Canvas") },
                        selected = currentScreen == Screen.Canvas,
                        onClick = { currentScreen = Screen.Canvas }
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
                Screen.Canvas -> CanvasScreen(taskViewModel, habitViewModel, routineViewModel, padding)
                Screen.Calendar -> CalendarScreen(padding)
                else -> {}
            }
        }
    }
} 