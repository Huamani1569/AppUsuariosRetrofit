package com.jeanpiero.usuarios.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val EsquemaColores = lightColorScheme(
    primary = Indigo,
    onPrimary = Superficie,
    primaryContainer = IndigoClaro,
    onPrimaryContainer = TextoPrincipal,
    secondary = Ambar,
    onSecondary = TextoPrincipal,
    background = Fondo,
    onBackground = TextoPrincipal,
    surface = Superficie,
    onSurface = TextoPrincipal,
    onSurfaceVariant = TextoSecundario,
    error = RojoError,
    onError = Superficie
)

@Composable
fun UsuariosTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = EsquemaColores, content = content)
}
