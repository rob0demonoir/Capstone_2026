package cl.duoc.sut_backend.repositories

import cl.duoc.sut_backend.models.Noticia
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface NoticiaRepository: JpaRepository<Noticia, Long> {
    fun findAllByOrderByFechaPublicacionDesc():List<Noticia>
    fun findByAutor(autor:String):List<Noticia>
}