package com.jeanpiero.usuarios.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Una sola instancia de Retrofit para toda la app (patrón Singleton con "object").
// En el ejemplo de clase el Retrofit.Builder() estaba dentro del Composable;
// en MVVM se mueve a la capa de datos para que la pantalla no sepa nada de la red.
object RetrofitClient {

    private const val BASE_URL = "https://randomuser.me/"

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
