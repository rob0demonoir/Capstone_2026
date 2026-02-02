package cl.duoc.sut_backend.controllers

import cl.duoc.sut_backend.dtos.CrearNoticiaRequest
import cl.duoc.sut_backend.dtos.NoticiaResponse
import cl.duoc.sut_backend.models.Noticia
import cl.duoc.sut_backend.repositories.NoticiaRepository
import cl.duoc.sut_backend.repositories.UsuarioRepository
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/noticias")
class NoticiaController(
    private val noticiaRepository: NoticiaRepository,
    private val usuarioRepository: UsuarioRepository
) {

    @GetMapping
    fun obtenerNoticias(): ResponseEntity<List<NoticiaResponse>> {
        val noticias = noticiaRepository.findAllByOrderByFechaPublicacionDesc()

        val response = noticias.map { n ->
            NoticiaResponse(
                id = n.id,
                titulo = n.titulo,
                contenido = n.contenido,
                fecha = n.fechaPublicacion,
                autor = "${n.autor.nombre} ${n.autor.apellido}",
                urlImagen = n.urlImagen
            )
        }
        return ResponseEntity.ok(response)
    }

    @PostMapping
    fun crearNoticia(@RequestBody request: CrearNoticiaRequest): ResponseEntity<String> {
        // CORRECCIÓN 1: Agregamos '!!' para asegurar que authentication no es nulo
        val email = SecurityContextHolder.getContext().authentication!!.name
        val autor = usuarioRepository.findByEmail(email).orElseThrow{ RuntimeException("Usuario no encontrado.") }

        val nuevaNoticia = Noticia(
            id = 0, // CORRECCIÓN 2: Pasamos explícitamente id = 0
            titulo = request.titulo,
            contenido = request.contenido,
            urlImagen = request.urlImagen,
            autor = autor
        )

        noticiaRepository.save(nuevaNoticia)
        return ResponseEntity.ok("Noticia publicada correctamente")
    }
}