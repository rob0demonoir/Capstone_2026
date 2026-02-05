package cl.duoc.sut_mobile.network

import cl.duoc.sut_mobile.model.ActualizarPerfilRequest
import cl.duoc.sut_mobile.model.Aviso
import cl.duoc.sut_mobile.model.CrearNoticiaRequest
import cl.duoc.sut_mobile.model.CrearSolicitudRequest
import cl.duoc.sut_mobile.model.LoginRequest
import cl.duoc.sut_mobile.model.LoginResponse
import cl.duoc.sut_mobile.model.Solicitud
import cl.duoc.sut_mobile.model.ResponderSolicitudRequest
import retrofit2.http.Path
import retrofit2.http.Streaming
import cl.duoc.sut_mobile.model.Usuario
import cl.duoc.sut_mobile.model.Noticia
import cl.duoc.sut_mobile.model.RegistroRequest
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.DELETE
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.Part


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

    @Multipart
    @POST("api/noticias")
    suspend fun publicarNoticia(
        @Part("titulo") titulo: RequestBody,
        @Part("contenido") contenido: RequestBody, // El backend espera "contenido", no "descripcion"
        @Part imagen: MultipartBody.Part? // Puede ser nula
    ): Response<ResponseBody>

    @POST("api/autenticacion/registro")
    suspend fun registrarUsuario(@Body request: RegistroRequest): Response<ResponseBody>

    // 1. Listar todos (Solo Admin)
    @GET("api/usuarios")
    suspend fun getUsuarios(): Response<List<Usuario>>
    // 2. Eliminar usuario por ID
    @DELETE("api/usuarios/{id}")
    suspend fun eliminarUsuario(@Path("id") id: Long): Response<ResponseBody>

    // 3. Cambiar Rol (Ya que estamos aquí, asegurémoslo)
    @PUT("api/usuarios/{id}/rol")
    @Headers("Content-Type: text/plain")
    suspend fun cambiarRol(@Path("id") id: Long, @Body nuevoRol: String): Response<Usuario>

    // 2. Cambiar Rol
    // Enviamos el String directo ("ADMIN" o "VECINO") en el cuerpo
    @PUT("api/usuarios/{id}/rol")
    suspend fun actualizarRol(@Path("id") id: Long, @Body nuevoRol: String): Response<ResponseBody>

    @GET("api/avisos")
    suspend fun getAvisos(): Response<List<Aviso>>

    @Multipart
    @POST("api/avisos")
    suspend fun publicarAviso(
        @Part("titulo") titulo: RequestBody,
        @Part("descripcion") descripcion: RequestBody,
        @Part("precio") precio: RequestBody?, // Puede ser nulo
        @Part("tipo") tipo: RequestBody,
        @Part imagen: MultipartBody.Part? // El archivo real (puede ser nulo)
    ): Response<ResponseBody>

    @DELETE("api/avisos/{id}")
    suspend fun borrarAviso(@Path("id") id: Long): Response<ResponseBody>

    @Multipart
    @POST("api/uploads")
    suspend fun subirImagen(@Part file: MultipartBody.Part): Response<Map<String, String>>

    @PUT("api/usuarios/perfil")
    suspend fun actualizarPerfil(@Body request: ActualizarPerfilRequest): Response<Usuario>

}