@file:OptIn(ExperimentalMaterial3Api::class)

package com.jeanpiero.usuarios.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jeanpiero.usuarios.ui.components.ItemUsuario
import com.jeanpiero.usuarios.ui.components.PantallaCargando
import com.jeanpiero.usuarios.ui.components.PantallaError
import com.jeanpiero.usuarios.viewmodel.UsuarioViewModel
import com.jeanpiero.usuarios.viewmodel.UsuariosUiState

// VISTA: solo dibuja lo que el ViewModel le indica y le avisa de los clics.
@Composable
fun UsuariosScreen(viewModel: UsuarioViewModel, listState: LazyListState) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Usuarios Retrofit", fontWeight = FontWeight.Bold)
                        Text("Semana 7 · MVVM + API REST", style = MaterialTheme.typography.bodySmall)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.cargarUsuarios() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualizar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TarjetaSegundoPlano(
                estado = viewModel.estadoSegundoPlano,
                enCurso = viewModel.trabajoEnCurso,
                onSincronizar = { viewModel.sincronizarEnSegundoPlano() }
            )

            // Filtro: vuelve a llamar al API con el parámetro ?nat=
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(UsuarioViewModel.NACIONALIDADES) { (codigo, texto) ->
                    FilterChip(
                        selected = viewModel.nacionalidad == codigo,
                        onClick = { viewModel.cambiarNacionalidad(codigo) },
                        label = { Text(texto) }
                    )
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                when (val estado = viewModel.uiState) {
                    is UsuariosUiState.Cargando -> PantallaCargando()
                    is UsuariosUiState.Error -> PantallaError(
                        mensaje = estado.mensaje,
                        onReintentar = { viewModel.cargarUsuarios() }
                    )
                    is UsuariosUiState.Exito -> LazyColumn(
                        state = listState,
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                "${estado.usuarios.size} usuarios recibidos del servicio web",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        items(estado.usuarios, key = { it.login.uuid }) { user ->
                            ItemUsuario(user = user, onClick = { viewModel.seleccionarUsuario(user) })
                        }
                    }
                }
            }
        }
    }
}

// Tarjeta que lanza el proceso en segundo plano con WorkManager y muestra su estado
@Composable
private fun TarjetaSegundoPlano(estado: String, enCurso: Boolean, onSincronizar: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Proceso en segundo plano (WorkManager)", fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(estado, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                Button(onClick = onSincronizar, enabled = !enCurso) {
                    Icon(Icons.Default.Sync, contentDescription = null)
                    Text("Sincronizar", modifier = Modifier.padding(start = 6.dp))
                }
            }
            if (enCurso) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}
