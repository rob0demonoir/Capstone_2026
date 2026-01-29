package cl.duoc.sut_backend.repositories

import cl.duoc.sut_backend.models.SolicitudCertificado
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SolicitudCertificadoRepository: JpaRepository<SolicitudCertificado, Long> {
    fun findBySolicitanteId(solicitanteId: Long): List<SolicitudCertificado>
}