package cl.duoc.sut_mobile.network

import cl.duoc.sut_mobile.model.LoginRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("api/autenticacion/login")
    suspend fun login(@Body request: LoginRequest): Response<String>
}