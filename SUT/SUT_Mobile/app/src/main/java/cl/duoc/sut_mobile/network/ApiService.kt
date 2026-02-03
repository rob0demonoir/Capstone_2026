package cl.duoc.sut_mobile.network

import cl.duoc.sut_mobile.model.CrearSolicitudRequest
import cl.duoc.sut_mobile.model.LoginRequest
import cl.duoc.sut_mobile.model.LoginResponse
import cl.duoc.sut_mobile.model.Solicitud
import cl.duoc.sut_mobile.model.ResponderSolicitudRequest
import retrofit2.http.Path
import retrofit2.http.Streaming
import cl.duoc.sut_mobile.model.Usuario
import cl.duoc.sut_mobile.model.Noticia
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.PUT


interface ApiService {
    @POST("api/autenticacion/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("api/usuarios/perfil")
    suspend fun getPerfil(): Response<Usuario>

    @GET("api/noticias")
    suspend fun getNoticias(): Response<List<Noticia>>

    @POST("api/solicitudes")
    suspend fun crearSolicitud(@Body request: CrearSolicitudRequest): Response<String>

    @GET("api/solicitudes/mis-solicitudes")
    suspend fun getMisSolicitudes(): Response<List<Solicitud>>

    @Streaming
    @GET("api/solicitudes/{id}/descargar")
    suspend fun descargarCertificado(@Path("id") id: Long): Response<ResponseBody>

    // Obtener TODAS las solicitudes (Para el Admin)
    @GET("api/solicitudes")
    suspend fun getTodasSolicitudes(): Response<List<Solicitud>>

    // Responder (Aprobar/Rechazar)
    @PUT("api/solicitudes/{id}/responder")
    suspend fun responderSolicitud(
        @Path("id") id: Long,
        @Body request: ResponderSolicitudRequest
    ): Response<String>

}