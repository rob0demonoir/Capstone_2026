package cl.duoc.sut_mobile.model

enum class EstadoSolicitud {
    PENDIENTE, APROBADA, RECHAZADA
}

data class Solicitud(
    val id: Long,
    val tipo: String, // "RESIDENCIA", etc.
    val fechaSolicitud: String,
    val estado: EstadoSolicitud,
    val comentarioAdmin: String?,
    val nombreSolicitante: String
)

data class CrearSolicitudRequest(
    val tipo: String,
    val comentario: String
)

data class ResponderSolicitudRequest(
    val estado: EstadoSolicitud,
    val comentarioAdmin: String
)