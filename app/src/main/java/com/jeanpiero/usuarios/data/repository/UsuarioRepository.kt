package com.jeanpiero.usuarios.data.repository

import com.jeanpiero.usuarios.data.model.Usuario
import com.jeanpiero.usuarios.data.remote.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Repositorio: único punto de acceso a los datos. El ViewModel le pide los usuarios
// y no le importa si vienen de internet, de Room o de otra fuente.
class UsuarioRepository(private val api: ApiService) {

    // withContext(Dispatchers.IO): la petición de red se hace fuera del hilo principal
    suspend fun obtenerUsuarios(cantidad: Int, nacionalidad: String?): List<Usuario> =
        withContext(Dispatchers.IO) {
            api.getUsers(cantidad, nacionalidad).results
        }
}
