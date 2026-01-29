package cl.duoc.sut_backend.models

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "noticias")
data class Noticia (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?,

    @Column(nullable = false)
    val titulo: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    val contenido: String,

    @Column(name="fecha_publicacion", nullable = false)
    val fechaPublicacion: LocalDateTime = LocalDateTime.now(),

    @Column(name="url_imagen")
    val urlImagen: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_id", nullable = false)
    val autor: Usuario
)