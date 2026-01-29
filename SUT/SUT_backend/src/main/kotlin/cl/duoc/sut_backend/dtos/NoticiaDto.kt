package cl.duoc.sut_backend.dtos

import java.time.LocalDateTime

data class CrearNoticiaRequest(
    val titulo: String,
    val contenido: String,
    val urlImagen: String? = null
)

data class NoticiaResponse(
    val id: Long,
    val titulo: String,
    val contenido: String,
    val fecha: LocalDateTime,
    val autor: String,
    val urlImagen: String
)