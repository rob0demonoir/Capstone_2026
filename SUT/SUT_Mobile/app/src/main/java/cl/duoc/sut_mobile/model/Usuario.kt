package cl.duoc.sut_mobile.model

data class Usuario(
    val id: Long,
    val nombre: String,
    val apellido: String,
    val email: String,
    val rol: String,
    val rut: String
)