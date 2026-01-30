package cl.duoc.sut_backend.dtos

import cl.duoc.sut_backend.models.TipoAviso
import java.time.LocalDateTime

data class CrearAvisoRequest(
    val titulo: String,
    val descripcion: String,
    val precio: Int?,
    val tipo: TipoAviso,
    val urlImagen: String? = null
)

data class AvisoResponse(
    val id: Long,
    val titulo: String,
    val descripcion: String,
    val precio: Int?,
    val tipo: TipoAviso,
    val fechaPublicacion: LocalDateTime,
    val nombrePublicador: String,
    val telefonoContacto: String, // ¡Dato clave para ventas!
    val urlImagen: String?,
    val esMio: Boolean // Para saber si puedo ponerle un botón de "Borrar" en la App
)