package cl.duoc.sut_backend.models

import com.fasterxml.jackson.annotation.JsonIgnore // <--- IMPORTANTE: Agregar este import
import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
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
    var email: String,

    @Column(nullable = false)
    var contrasena: String,

    @Column(nullable = false)
    var direccion: String,

    @Column(name = "fecha_nacimiento", nullable = false)
    val fechaNacimiento: LocalDate,

    @Column(nullable = false)
    var telefono: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var rol: Rol,

    @Column(nullable = false)
    val habilitado: Boolean = true

) : UserDetails {

    // ========================================================================
    // SECCIÓN DE RELACIONES (CASCADA)
    // ========================================================================

    // 1. Relación con NOTICIAS
    // 'mappedBy = "autor"' significa que en tu clase Noticia tienes un campo llamado "autor"
    @OneToMany(mappedBy = "autor", cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonIgnore // Evita bucles infinitos al transformar a JSON
    var noticias: MutableList<Noticia> = mutableListOf()

    // 2. Relación con AVISOS
    // 'mappedBy = "publicador"' significa que en tu clase Aviso tienes un campo llamado "publicador"
    // (Si en Aviso se llama "usuario" o "autor", cambia "publicador" por ese nombre aquí)
    @OneToMany(mappedBy = "publicador", cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonIgnore
    var avisos: MutableList<Aviso> = mutableListOf()

    // 3. Relación con SOLICITUDES
    // 'mappedBy = "solicitante"' significa que en tu clase Solicitud tienes un campo "solicitante"
    @OneToMany(mappedBy = "solicitante", cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonIgnore
    var solicitudes: MutableList<SolicitudCertificado> = mutableListOf()

    // ========================================================================
    // MÉTODOS DE SECURITY
    // ========================================================================

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