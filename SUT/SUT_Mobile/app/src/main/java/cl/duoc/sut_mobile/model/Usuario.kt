package cl.duoc.sut_mobile.model

data class Usuario(
    val id: Long,
    val rut: String,
    val nombre: String,
    val apellido: String,
    val email: String,
    val direccion: String,
    val telefono: String,
    val rol: String //
)