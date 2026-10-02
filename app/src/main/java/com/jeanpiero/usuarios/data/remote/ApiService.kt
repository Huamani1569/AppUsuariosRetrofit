package com.jeanpiero.usuarios.data.remote

import com.jeanpiero.usuarios.data.model.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

// Interface del API: Retrofit genera la implementación de cada petición HTTP.
interface ApiService {

    // GET https://randomuser.me/api/?results=20&nat=es
    @GET("api/")
    suspend fun getUsers(
        @Query("results") cantidad: Int = 20,
        @Query("nat") nacionalidad: String? = null   // null = sin filtro (Retrofit no envía el parámetro)
    ): ApiResponse
}
