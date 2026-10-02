package com.jeanpiero.usuarios.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.jeanpiero.usuarios.data.model.Usuario
import com.jeanpiero.usuarios.data.repository.UsuarioRepository
import com.jeanpiero.usuarios.worker.SincronizacionWorker
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

// VIEWMODEL: guarda el estado de la pantalla y pide los datos al repositorio.
// Sobrevive a la rotación de pantalla, así que no se vuelve a llamar al API al girar el celular.
class UsuarioViewModel(
    private val repository: UsuarioRepository,
    private val workManager: WorkManager
) : ViewModel() {

    var uiState: UsuariosUiState by mutableStateOf(UsuariosUiState.Cargando)
        private set

    // Filtro por nacionalidad (null = todas)
    var nacionalidad: String? by mutableStateOf(null)
        private set

    // Usuario elegido para ver su detalle (null = se muestra la lista)
    var usuarioSeleccionado: Usuario? by mutableStateOf(null)
        private set

    // Estado del proceso en segundo plano (WorkManager)
    var estadoSegundoPlano by mutableStateOf("Aún no se ha ejecutado")
        private set
    var trabajoEnCurso by mutableStateOf(false)
        private set

    private var observadorTrabajo: Job? = null

    init {
        cargarUsuarios()
    }

    // Llama al servicio web y actualiza el estado: Cargando → Éxito / Error
    fun cargarUsuarios() {
        uiState = UsuariosUiState.Cargando
        viewModelScope.launch {
            uiState = try {
                UsuariosUiState.Exito(repository.obtenerUsuarios(CANTIDAD, nacionalidad))
            } catch (e: IOException) {
                UsuariosUiState.Error("Sin conexión a internet. Revisa tu red e inténtalo de nuevo.")
            } catch (e: HttpException) {
                UsuariosUiState.Error("El servidor respondió con el error ${e.code()}.")
            }
        }
    }

    fun cambiarNacionalidad(codigo: String?) {
        if (codigo == nacionalidad) return
        nacionalidad = codigo
        cargarUsuarios()
    }

    fun seleccionarUsuario(usuario: Usuario) {
        usuarioSeleccionado = usuario
    }

    fun cerrarDetalle() {
        usuarioSeleccionado = null
    }

    // Encola un trabajo único en WorkManager y observa su estado para mostrarlo en pantalla.
    fun sincronizarEnSegundoPlano() {
        val restricciones = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED) // solo con internet
            .build()
        val solicitud = OneTimeWorkRequestBuilder<SincronizacionWorker>()
            .setConstraints(restricciones)
            .build()

        workManager.enqueueUniqueWork(NOMBRE_TRABAJO, ExistingWorkPolicy.REPLACE, solicitud)
        trabajoEnCurso = true

        observadorTrabajo?.cancel() // deja de observar el trabajo anterior
        observadorTrabajo = viewModelScope.launch {
            workManager.getWorkInfoByIdFlow(solicitud.id).collect { info ->
                if (info != null) {
                    estadoSegundoPlano = when (info.state) {
                        WorkInfo.State.ENQUEUED -> "En cola: esperando conexión a internet..."
                        WorkInfo.State.RUNNING -> "Ejecutando en segundo plano..."
                        WorkInfo.State.SUCCEEDED -> {
                            val cantidad = info.outputData.getInt(SincronizacionWorker.CLAVE_CANTIDAD, 0)
                            "Completado: se descargaron $cantidad usuarios en segundo plano"
                        }
                        WorkInfo.State.FAILED -> "Falló la sincronización. Inténtalo de nuevo."
                        WorkInfo.State.CANCELLED -> "Trabajo cancelado"
                        WorkInfo.State.BLOCKED -> "Bloqueado por otro trabajo"
                    }
                    trabajoEnCurso = !info.state.isFinished
                }
            }
        }
    }

    companion object {
        private const val CANTIDAD = 20
        private const val NOMBRE_TRABAJO = "sincronizacion_usuarios"

        // Opciones del filtro: código de nacionalidad del API → texto para el chip
        val NACIONALIDADES: List<Pair<String?, String>> = listOf(
            null to "Todos",
            "es" to "España",
            "mx" to "México",
            "us" to "EE. UU.",
            "br" to "Brasil",
            "fr" to "Francia"
        )
    }
}
