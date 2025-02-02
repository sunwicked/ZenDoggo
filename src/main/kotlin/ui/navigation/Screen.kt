sealed class Screen(val route: String) {
    object Canvas : Screen("canvas")
    object Calendar : Screen("calendar")
    object Tasks : Screen("tasks")
    object Habits : Screen("habits")
    object Routines : Screen("routines")  // This is our main routines screen
} 