package com.jeanpiero.usuarios

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.work.WorkManager
import com.jeanpiero.usuarios.data.remote.RetrofitClient
import com.jeanpiero.usuarios.data.repository.UsuarioRepository
import com.jeanpiero.usuarios.ui.navigation.AppNavigation
import com.jeanpiero.usuarios.ui.theme.UsuariosTheme
import com.jeanpiero.usuarios.viewmodel.UsuarioViewModel

// Punto de entrada: crea el ViewModel con su repositorio (Retrofit) y WorkManager.
class MainActivity : ComponentActivity() {

    private val usuarioViewModel: UsuarioViewModel by viewModels {
        viewModelFactory {
            initializer {
                UsuarioViewModel(
                    repository = UsuarioRepository(RetrofitClient.api),
                    workManager = WorkManager.getInstance(applicationContext)
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Íconos de la barra de estado en blanco (la barra superior es oscura)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT))
        setContent {
            UsuariosTheme {
                AppNavigation(usuarioViewModel)
            }
        }
    }
}
