package cl.duoc.sut_mobile.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Esquema Oscuro (Opcional, por si el usuario tiene modo noche)
private val DarkColorScheme = darkColorScheme(
    primary = Teal100,
    onPrimary = Teal900,
    secondary = Orange500,
    onSecondary = White,
    tertiary = Teal700,
    background = Slate900,
    surface = Color(0xFF1E293B) // Slate-800
)

// Esquema Claro (El principal que diseñamos)
private val LightColorScheme = lightColorScheme(
    // 1. EL COLOR PRINCIPAL (Barras, Headers, FABs) -> Teal Oscuro
    primary = Teal900,
    onPrimary = White,
    primaryContainer = Teal100,
    onPrimaryContainer = Teal900,

    // 2. EL COLOR DE ACCIÓN (Botones, Checks, Floating Action) -> Naranja
    secondary = Orange500,
    onSecondary = White,
    secondaryContainer = Orange50,
    onSecondaryContainer = Orange700,

    // 3. COLOR TERCIARIO (Detalles, bordes) -> Teal Medio
    tertiary = Teal700,
    onTertiary = White,

    // 4. FONDOS -> Slate muy suave (No blanco puro)
    background = Slate50,
    onBackground = Slate900,

    // 5. SUPERFICIES (Tarjetas) -> Blanco
    surface = White,
    onSurface = Slate900,
    surfaceVariant = Color(0xFFE2E8F0), // Slate-200 (Para bordes o divisores)
    onSurfaceVariant = Slate500,

    error = ErrorRed,
    onError = White
)

@Composable
fun SUT_MobileTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    // LO DESACTIVAMOS POR DEFECTO para que tu marca Teal/Orange prevalezca
    // sobre los colores del fondo de pantalla del usuario.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Pintamos la barra de estado del color primario (Teal900)
            window.statusBarColor = colorScheme.primary.toArgb()

            // Si es modo claro, los iconos de la barra deben ser blancos (porque el fondo es oscuro)
            // WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
            // En este caso, como Teal900 es oscuro, queremos iconos blancos SIEMPRE en la barra.
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Asegúrate de tener tu Typography.kt estándar
        content = content
    )
}