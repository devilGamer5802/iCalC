import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

// Light Theme Colors
val LightBackground = Color(0xFFF1F2F3)
val LightButtonGray = Color(0xFFDCDDE1)
val LightButtonBlue = Color(0xFF4C8DFF)
val LightTextPrimary = Color(0xFF000000)
val LightTextSecondary = Color(0xFF636363)


// Dark Theme Colors
val DarkBackground = Color(0xFF000000) // Changed to pure black
val DarkButtonGray = Color(0xFF2E2F38) // This is a good dark gray for buttons
val DarkButtonOrange = Color(0xFFFFA500) // A nice, bright orange
val DarkTextPrimary = Color(0xFFFFFFFF)
val DarkTextSecondary = Color(0xFFB8B8B8)

private val DarkColorScheme = darkColorScheme(
    primary = DarkButtonOrange, // Operators will now be orange

)