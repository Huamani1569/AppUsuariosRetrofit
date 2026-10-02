package com.jeanpiero.usuarios.viewmodel

import com.jeanpiero.usuarios.data.model.Usuario

// Estados posibles de la pantalla mientras se consume el servicio web.
// La vista solo "pinta" el estado que le entrega el ViewModel.
sealed interface UsuariosUiState {
    data object Cargando : UsuariosUiState
    data class Exito(val usuarios: List<Usuario>) : UsuariosUiState
    data class Error(val mensaje: String) : UsuariosUiState
}
