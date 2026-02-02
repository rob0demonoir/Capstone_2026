package cl.duoc.sut_mobile.model

data class Noticia (
    val id: Long,
    val titulo: String,
    val contenido: String,
    val fecha: String,
    val autor: String,
    val urlImagen: String?
)