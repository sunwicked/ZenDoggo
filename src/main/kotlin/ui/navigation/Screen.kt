sealed class Screen(val route: String) {
    object Tasks : Screen("tasks")
    object Habits : Screen("habits")
    object Routines : Screen("routines")
    object Calendar : Screen("calendar")
} 