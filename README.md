# Usuarios Retrofit 👥

Ejemplo de la sesión de Semana 7 (randomuser.me) llevado a la arquitectura **MVVM**, más un proceso en **segundo plano con WorkManager**.

**Estudiante:** Jean Piero Huamani Rodriguez · **Curso:** Desarrollo de Aplicaciones Móviles · **Semana 7:** MVVM + servicios web REST con Retrofit

Kotlin · Jetpack Compose · MVVM · Retrofit 2 · Gson · Coil · WorkManager

## Servicio web consumido
`GET https://randomuser.me/api/?results=20&nat=es`

## Funcionalidades
- Lista de 20 usuarios con foto (Coil), nombre y ciudad, leídos del servicio web REST.
- Filtro por nacionalidad (Todos, España, México, EE. UU., Brasil, Francia): vuelve a llamar al API con `?nat=`.
- Detalle del usuario: correo, teléfono, celular, ubicación, edad, usuario y nacionalidad.
- Botón **Sincronizar**: encola un `CoroutineWorker` con WorkManager (restricción: requiere internet) que descarga 50 usuarios en segundo plano; la pantalla muestra su estado (En cola → Ejecutando → Completado).
- Estados de pantalla: Cargando, Éxito y Error (sin internet) con botón **Reintentar**.

## Arquitectura MVVM
En el ejemplo de clase, `Retrofit.Builder()`, la llamada al API y la lista estaban dentro de un solo `@Composable` (`Greeting`). Ahora cada responsabilidad está en su capa: **Model** (data/model), **datos** (remote + repository), **ViewModel** (estado y lógica) y **View** (ui/screens, solo dibuja).

```
View (Compose)  ──eventos──▶  ViewModel  ──pide datos──▶  Repositorio  ──▶  Retrofit (ApiService)  ──HTTP GET──▶  API REST
      ▲                          │                                                                         │
      └────── estado (UiState) ──┘◀──────────────────────── objetos Kotlin (Gson) ◀──────── JSON ────────────┘
```

```
com.jeanpiero.usuarios/
├── data/
│   ├── model/ApiResponse.kt          // MODELO: ApiResponse, Usuario, Nombre, Imagen...
│   ├── remote/ApiService.kt          // Interface del API (@GET)
│   ├── remote/RetrofitClient.kt      // Retrofit.Builder() (Singleton)
│   └── repository/UsuarioRepository.kt
├── worker/SincronizacionWorker.kt    // WorkManager: proceso en segundo plano
├── viewmodel/
│   ├── UsuariosUiState.kt            // Cargando / Exito / Error
│   └── UsuarioViewModel.kt           // estado, filtro, WorkManager
├── ui/
│   ├── navigation/AppNavigation.kt   // Lista ↔ Detalle
│   ├── screens/UsuariosScreen.kt, DetalleUsuarioScreen.kt
│   ├── components/ItemUsuario.kt, EstadosPantalla.kt
│   └── theme/
└── MainActivity.kt                   // crea el ViewModel con su repositorio
```

## Abrir y generar el APK
1. Android Studio → **File → Open** → carpeta `AppUsuariosRetrofit` → espera el **Gradle Sync**.
2. Ejecuta con ▶ **Run** (el celular o emulador necesita internet).
3. APK: **Build → Generate App Bundles or APKs → Generate APKs** → queda en `app/build/outputs/apk/debug/app-debug.apk`.
