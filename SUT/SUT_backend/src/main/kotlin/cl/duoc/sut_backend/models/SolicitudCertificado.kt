package cl.duoc.sut_backend.models

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "solicitud_certificados")
data class SolicitudCertificado(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val tipo: TipoCertificado,

    @Column(name = "fecha_Solicitud", nullable=false)
    val fechaSolicitud: LocalDateTime = LocalDateTime.now(),

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var estado: EstadoSolicitud = EstadoSolicitud.PENDIENTE,

    @Column(name="comentario_admin")
    var comentarioAdmin: String? = null,

    @Column(name="ruta_certificado")
    var rutaCertificado: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitante_id", nullable=false)
    val solicitante : Usuario,
)

enum class TipoCertificado {
    RESIDENCIA
}

enum class EstadoSolicitud {
    PENDIENTE,
    APROBADA,
    RECHAZADA
}