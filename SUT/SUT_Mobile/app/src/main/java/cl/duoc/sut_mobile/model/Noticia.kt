package cl.duoc.sut_mobile.model

data class Noticia (
    val id: Long,
    val titulo: String,
    val contenido: String,
    val fecha: String,
    val autor: String,
    val urlImagen: String?
)

data class CrearNoticiaRequest(
    val titulo: String,
    val contenido: String,
    val urlImagen: String? = null // <--- Agregado para coincidir con tu Backend
)