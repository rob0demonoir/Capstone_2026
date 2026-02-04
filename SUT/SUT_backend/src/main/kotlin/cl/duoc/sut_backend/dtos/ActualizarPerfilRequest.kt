package cl.duoc.sut_backend.dtos

data class ActualizarPerfilRequest(
    val telefono: String,
    val direccion: String,
    val email: String // Opcional, por si quiere cambiar correo
)