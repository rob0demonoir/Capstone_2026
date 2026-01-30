package cl.duoc.sut_backend.repositories

import cl.duoc.sut_backend.models.Aviso
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AvisoRepository : JpaRepository<Aviso, Long> {
    // Para mostrar los más recientes primero
    fun findAllByOrderByFechaPublicacionDesc(): List<Aviso>
}