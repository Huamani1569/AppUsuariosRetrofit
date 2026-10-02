package com.jeanpiero.usuarios.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.jeanpiero.usuarios.data.remote.RetrofitClient
import com.jeanpiero.usuarios.data.repository.UsuarioRepository
import kotlinx.coroutines.delay

// Proceso en segundo plano (WorkManager): descarga usuarios del servicio web
// aunque el usuario salga de la pantalla. Se ejecuta solo si hay conexión a internet
// (la restricción se define en el ViewModel al encolar el trabajo).
class SincronizacionWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            delay(2000) // simula un proceso más largo para que se vea el estado "En ejecución"
            val usuarios = UsuarioRepository(RetrofitClient.api).obtenerUsuarios(50, null)
            // Datos de salida que el ViewModel lee cuando el trabajo termina
            Result.success(workDataOf(CLAVE_CANTIDAD to usuarios.size))
        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        const val CLAVE_CANTIDAD = "cantidad_usuarios"
    }
}
