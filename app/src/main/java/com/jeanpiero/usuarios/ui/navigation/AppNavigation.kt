package com.jeanpiero.usuarios.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import com.jeanpiero.usuarios.ui.screens.DetalleUsuarioScreen
import com.jeanpiero.usuarios.ui.screens.UsuariosScreen
import com.jeanpiero.usuarios.viewmodel.UsuarioViewModel

// Navegación simple: si hay un usuario seleccionado se muestra su detalle; si no, la lista.
@Composable
fun AppNavigation(viewModel: UsuarioViewModel) {
    val seleccionado = viewModel.usuarioSeleccionado
    // Se guarda aquí para que la lista conserve su posición al volver del detalle
    val listState = rememberLazyListState()
    if (seleccionado == null) {
        UsuariosScreen(viewModel, listState)
    } else {
        BackHandler { viewModel.cerrarDetalle() } // botón atrás del celular
        DetalleUsuarioScreen(usuario = seleccionado, onVolver = { viewModel.cerrarDetalle() })
    }
}
