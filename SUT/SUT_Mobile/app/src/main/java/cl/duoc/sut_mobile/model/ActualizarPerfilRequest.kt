package cl.duoc.sut_mobile.model
import cl.duoc.sut_mobile.model.Usuario

data class ActualizarPerfilRequest(
    val telefono: String,
    val direccion: String,
    val email: String)