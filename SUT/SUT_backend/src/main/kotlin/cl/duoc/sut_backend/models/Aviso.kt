package cl.duoc.sut_backend.models

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "avisos")
data class Aviso (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val titulo: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    val descripcion: String,

    @Column(nullable = true)
    val precio: Int? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val tipo: TipoAviso,

    @Column(name="fecha_publicacion", nullable = false)
    val fechaPublicacion: LocalDateTime = LocalDateTime.now(),

    @Column(name="url_imagen")
    val urlImagen: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="publicador_id", nullable = false)
    val publicador: Usuario
)

enum class TipoAviso {
    VENTA,      // "Vendo bicicleta"
    SERVICIO,   // "Ofrezco gasfitería", "Cuido niños"
    EVENTO,     // "Venta de Garage", "Bingo bailable"
    BUSCO       // "Busco dato de flete", "Se me perdió el gato"
}