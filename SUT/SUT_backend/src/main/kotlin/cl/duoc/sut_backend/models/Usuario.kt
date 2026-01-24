package cl.duoc.sut_backend.models

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "usuarios")
data class Usuario (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(unique = true, nullable = false, length = 12)
    val rut: String,

    @Column(nullable = false)
    val nombre: String,

    @Column(nullable = false)
    val apellido: String,

    @Column(unique = true, nullable = false)
    val email: String,

    @Column(nullable = false)
    val contrasena: String?,

    @Column(nullable = false)
    val direccion: String,

    @Column(name = "fecha_nacimiento", nullable = false)
    val fechaNacimiento: LocalDate,

    @Column(nullable = false)
    val telefono: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val rol: Rol = Rol.VECINO,

    @Column(nullable = false)
    val habilitado: Boolean = true


)

enum class Rol {
    ADMINISTRADOR,
    VECINO
}

