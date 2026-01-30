package cl.duoc.sut_backend.controllers

import cl.duoc.sut_backend.dtos.AvisoResponse
import cl.duoc.sut_backend.dtos.CrearAvisoRequest
import cl.duoc.sut_backend.models.Aviso
import cl.duoc.sut_backend.models.Rol
import cl.duoc.sut_backend.repositories.AvisoRepository
import cl.duoc.sut_backend.repositories.UsuarioRepository
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/avisos")
class AvisoController(
    private val avisoRepository: AvisoRepository,
    private val usuarioRepository: UsuarioRepository
) {

    @GetMapping
    fun obtenerAvisos(): ResponseEntity<List<AvisoResponse>> {
        // Obtenemos email del usuario actual para saber cuáles avisos son suyos
        val emailActual = SecurityContextHolder.getContext().authentication?.name

        val avisos = avisoRepository.findAllByOrderByFechaPublicacionDesc()

        val response = avisos.map { aviso ->
            AvisoResponse(
                id = aviso.id!!,
                titulo = aviso.titulo,
                descripcion = aviso.descripcion,
                precio = aviso.precio,
                tipo = aviso.tipo,
                fechaPublicacion = aviso.fechaPublicacion,
                nombrePublicador = "${aviso.publicador.nombre} ${aviso.publicador.apellido}",
                telefonoContacto = aviso.publicador.telefono,
                urlImagen = aviso.urlImagen,
                // Si el email del aviso coincide con el logueado, es mío
                esMio = (emailActual == aviso.publicador.email)
            )
        }
        return ResponseEntity.ok(response)
    }

    @PostMapping
    fun publicarAviso(@RequestBody request: CrearAvisoRequest): ResponseEntity<String> {
        val email = SecurityContextHolder.getContext().authentication!!.name
        val vecino = usuarioRepository.findByEmail(email).orElseThrow()

        val nuevoAviso = Aviso(
            titulo = request.titulo,
            descripcion = request.descripcion,
            precio = request.precio,
            tipo = request.tipo,
            urlImagen = request.urlImagen,
            publicador = vecino
        )

        avisoRepository.save(nuevoAviso)
        return ResponseEntity.ok("Aviso publicado correctamente")
    }

    @DeleteMapping("/{id}")
    fun borrarAviso(@PathVariable id: Long): ResponseEntity<String> {
        val email = SecurityContextHolder.getContext().authentication!!.name
        val usuario = usuarioRepository.findByEmail(email).orElseThrow()

        val aviso = avisoRepository.findById(id)
            .orElseThrow { RuntimeException("Aviso no encontrado") }

        // Lógica de seguridad: Solo borras si eres el dueño O eres Administrador
        if (aviso.publicador.email == email || usuario.rol == Rol.ADMINISTRADOR) {
            avisoRepository.delete(aviso)
            return ResponseEntity.ok("Aviso eliminado")
        } else {
            return ResponseEntity.status(403).body("No tienes permiso para borrar este aviso")
        }
    }
}