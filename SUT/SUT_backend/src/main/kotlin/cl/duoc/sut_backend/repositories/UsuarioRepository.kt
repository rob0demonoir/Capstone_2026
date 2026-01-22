package cl.duoc.sut_backend.repositories
import cl.duoc.sut_backend.models.Usuario
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

//los Repository se hacen con interface
@Repository
interface UsuarioRepository: JpaRepository<Usuario, Long> {
    fun findByEmail(email: String): Optional<Usuario>
    fun findByRut(rut: String): Optional<Usuario>
    fun existsByEmail(email: String): Boolean
    fun existsByRut(rut: String): Boolean
}