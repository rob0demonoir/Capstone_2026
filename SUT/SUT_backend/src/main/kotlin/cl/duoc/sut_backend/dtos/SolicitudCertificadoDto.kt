package cl.duoc.sut_backend.dtos

import cl.duoc.sut_backend.models.TipoCertificado
import cl.duoc.sut_backend.models.EstadoSolicitud
import cl.duoc.sut_backend.models.SolicitudCertificado
import java.time.LocalDateTime

data class CrearSolicitudRequest(
    val tipo: TipoCertificado
)

data class SolicitudResponse(
    val id: Long,
    val tipo: TipoCertificado,
    val fechaSolicitud: LocalDateTime,
    val estado: EstadoSolicitud,
    val comentarioAdmin: String?,
    val nombreSolicitante: String
)

data class ResponderSolicitudRequest(
    val estado: EstadoSolicitud,
    val comentarioAdmin: String?
)