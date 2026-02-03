package cl.duoc.sut_mobile.model

data class RegistroRequest(
    val rut: String,
    val nombre: String,
    val apellido: String,
    val email: String,
    val contrasena: String,
    val direccion: String,
    val telefono: String,
    val fechaNacimiento: String // Formato "YYYY-MM-DD"
)