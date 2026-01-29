package cl.duoc.sut_backend.controllers

import cl.duoc.sut_backend.dtos.CrearSolicitudRequest
import cl.duoc.sut_backend.dtos.SolicitudResponse
import cl.duoc.sut_backend.models.SolicitudCertificado
import cl.duoc.sut_backend.repositories.SolicitudCertificadoRepository
import cl.duoc.sut_backend.repositories.UsuarioRepository
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/solicitudes")
class SolicitudController(
    private val solicitudRepository: SolicitudCertificadoRepository,
    private val usuarioRepository: UsuarioRepository
) {

    @PostMapping
    fun crearSolicitud(@RequestBody request: CrearSolicitudRequest): ResponseEntity<String> {
        // CORRECCIÓN 1: '!!' para asegurar autenticación
        val email = SecurityContextHolder.getContext().authentication!!.name
        val solicitante = usuarioRepository.findByEmail(email).orElseThrow()

        val nuevaSolicitud = SolicitudCertificado(
            id = null, // CORRECCIÓN 2: id = null explícito
            tipo = request.tipo,
            solicitante = solicitante
        )

        solicitudRepository.save(nuevaSolicitud)
        return ResponseEntity.ok("Solicitud enviada exitosamente")
    }

    @GetMapping("/mis-solicitudes")
    fun misSolicitudes(): ResponseEntity<List<SolicitudResponse>> {
        // CORRECCIÓN 1: '!!' para asegurar autenticación
        val email = SecurityContextHolder.getContext().authentication!!.name
        val usuario = usuarioRepository.findByEmail(email).orElseThrow()

        val misSolicitudes = solicitudRepository.findBySolicitanteId(usuario.id!!)

        val response = misSolicitudes.map { s ->
            SolicitudResponse(
                id = s.id!!,
                tipo = s.tipo,
                fechaSolicitud = s.fechaSolicitud,
                estado = s.estado,
                comentarioAdmin = s.comentarioAdmin
            )
        }

        return ResponseEntity.ok(response)
    }
}