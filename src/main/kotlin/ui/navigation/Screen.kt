sealed class Screen(val route: String) {
    object Canvas : Screen("canvas")
    object Calendar : Screen("calendar")
} 