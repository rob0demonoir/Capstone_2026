package cl.duoc.sut_backend.dtos
import java.time.LocalDate

data class RegistroRequest (
    val rut: String,
    val nombre: String,
    val apellido: String,
    val email: String,
    val contrasena: String,
    val direccion: String,
    val fechaNacimiento: LocalDate,
    val telefono: String,
)
