package com.jeanpiero.usuarios.data.model

// MODELO (la "M" de MVVM): clases que representan el JSON que devuelve https://randomuser.me/api/
// Gson convierte automáticamente cada campo del JSON en la propiedad con el mismo nombre.

data class ApiResponse(
    val results: List<Usuario>
)

data class Usuario(
    val gender: String,
    val name: Nombre,
    val location: Ubicacion,
    val email: String,
    val login: Login,
    val dob: FechaNacimiento,
    val phone: String,
    val cell: String,
    val picture: Imagen,
    val nat: String
) {
    // Nombre completo listo para mostrar en pantalla
    val nombreCompleto: String get() = "${name.first} ${name.last}"
}

data class Nombre(val title: String, val first: String, val last: String)

data class Imagen(val large: String, val medium: String, val thumbnail: String)

data class Ubicacion(val city: String, val state: String, val country: String)

// login.uuid es único por usuario: se usa como clave en la lista
data class Login(val uuid: String, val username: String)

data class FechaNacimiento(val date: String, val age: Int)
