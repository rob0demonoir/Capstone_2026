package cl.duoc.sut_mobile.network

import cl.duoc.sut_mobile.model.LoginRequest
import cl.duoc.sut_mobile.model.LoginResponse
import cl.duoc.sut_mobile.model.Usuario
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET


interface ApiService {
    @POST("api/autenticacion/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("api/usuarios/perfil")
    suspend fun getPerfil(): Response<Usuario>
}