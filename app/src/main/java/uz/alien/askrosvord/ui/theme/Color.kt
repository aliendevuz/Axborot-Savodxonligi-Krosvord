package uz.alien.askrosvord.ui.theme

import androidx.compose.ui.graphics.Color

// Light Theme Colors - Kunduzgi mavzu
object LightColors {
    val Primary = Color(0xFF2563EB) // Ochiq ko'k
    val PrimaryContainer = Color(0xFFDEEBFF)
    val Secondary = Color(0xFF059669) // Yashil
    val SecondaryContainer = Color(0xFFD1FAE5)
    val Tertiary = Color(0xFF7C3AED) // Binafsha
    val TertiaryContainer = Color(0xFFEDE9FE)
    val Error = Color(0xFFDC2626)
    val ErrorContainer = Color(0xFFFFEBEE)
    val Background = Color(0xFFF8FAFC)
    val Surface = Color(0xFFFFFFFF)
    val SurfaceVariant = Color(0xFFF1F5F9)
    val OnPrimary = Color(0xFFFFFFFF)
    val OnSecondary = Color(0xFFFFFFFF)
    val OnTertiary = Color(0xFFFFFFFF)
    val OnBackground = Color(0xFF1E293B)
    val OnSurface = Color(0xFF334155)
    val OnSurfaceVariant = Color(0xFF64748B)
    val Outline = Color(0xFFCBD5E1)
}

// Dark Theme Colors - Tungi mavzu
object DarkColors {
    val Primary = Color(0xFF60A5FA) // Yorqin ko'k
    val PrimaryContainer = Color(0xFF1E3A8A)
    val Secondary = Color(0xFF34D399) // Yashil
    val SecondaryContainer = Color(0xFF065F46)
    val Tertiary = Color(0xFFA78BFA) // Binafsha
    val TertiaryContainer = Color(0xFF5B21B6)
    val Error = Color(0xFFEF4444)
    val ErrorContainer = Color(0xFF7F1D1D)
    val Background = Color(0xFF0F172A)
    val Surface = Color(0xFF1E293B)
    val SurfaceVariant = Color(0xFF334155)
    val OnPrimary = Color(0xFF1E293B)
    val OnSecondary = Color(0xFF1E293B)
    val OnTertiary = Color(0xFF1E293B)
    val OnBackground = Color(0xFFF1F5F9)
    val OnSurface = Color(0xFFE2E8F0)
    val OnSurfaceVariant = Color(0xFFCBD5E1)
    val Outline = Color(0xFF475569)
}

// Crossword Grid Colors - Krossvord ranglar
object CrosswordColors {
    // Light mode
    val LightWall = Color(0xFF334155)
    val LightWallGradientStart = Color(0xFF475569)
    val LightWallGradientEnd = Color(0xFF1E293B)
    val LightCellBorder = Color(0xFFE2E8F0)
    val LightSelectedCell = Color(0xFF2563EB)
    val LightSelectedWordCell = Color(0xFFDEEBFF)
    val LightRevealedCell = Color(0xFFFEF3C7)
    val LightCorrectCell = Color(0xFFD1FAE5)
    val LightIncorrectCell = Color(0xFFFEE2E2)

    // Dark mode
    val DarkWall = Color(0xFF0F172A)
    val DarkWallGradientStart = Color(0xFF1E293B)
    val DarkWallGradientEnd = Color(0xFF0F172A)
    val DarkCellBorder = Color(0xFF475569)
    val DarkSelectedCell = Color(0xFF60A5FA)
    val DarkSelectedWordCell = Color(0xFF1E3A8A)
    val DarkRevealedCell = Color(0xFF92400E)
    val DarkCorrectCell = Color(0xFF065F46)
    val DarkIncorrectCell = Color(0xFF7F1D1D)
}