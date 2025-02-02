import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Mac-style colors
private val LightColors = lightColors(
    primary = Color(0xFF007AFF),       // iOS Blue
    primaryVariant = Color(0xFF0051C3),
    secondary = Color(0xFF5856D6),     // iOS Purple
    background = Color(0xFFF2F2F7),    // Light gray background
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF1C1C1E),  // Almost black
    onSurface = Color(0xFF1C1C1E)
)

private val DarkColors = darkColors(
    primary = Color(0xFF0A84FF),       // iOS Blue (Dark)
    primaryVariant = Color(0xFF0051C3),
    secondary = Color(0xFF5E5CE6),     // iOS Purple (Dark)
    background = Color(0xFF1C1C1E),    // Dark background
    surface = Color(0xFF2C2C2E),       // Slightly lighter dark
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun ZenDoggoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = if (darkTheme) DarkColors else LightColors,
        content = content
    )
} 