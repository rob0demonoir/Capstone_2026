package cl.duoc.sut_backend.models

import jakarta.persistence.*
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties
import org.springframework.security.core.GrantedAuthority
import java.time.LocalDate

//implementar detalles de usuario
import org.springframework.security.core.userdetails.UserDetails

//implementar autorizacion para acceso
import org.springframework.security.core.authority.SimpleGrantedAuthority

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
    var contrasena: String,

    @Column(nullable = false)
    val direccion: String,

    @Column(name = "fecha_nacimiento", nullable = false)
    val fechaNacimiento: LocalDate,

    @Column(nullable = false)
    val telefono: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var rol: Rol,

    @Column(nullable = false)
    val habilitado: Boolean = true


) : UserDetails {
    override fun getAuthorities(): Collection<GrantedAuthority> {
        return listOf(SimpleGrantedAuthority("ROLE_" + rol.name))
    }

    override fun getPassword(): String = contrasena
    override fun getUsername(): String = email
    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = habilitado
}



enum class Rol {
    ADMINISTRADOR,
    VECINO
}

