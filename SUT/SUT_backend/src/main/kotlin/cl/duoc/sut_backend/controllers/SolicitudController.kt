package cl.duoc.sut_backend.controllers

import cl.duoc.sut_backend.dtos.CrearSolicitudRequest
import cl.duoc.sut_backend.dtos.ResponderSolicitudRequest
import cl.duoc.sut_backend.dtos.SolicitudResponse
import cl.duoc.sut_backend.models.EstadoSolicitud
import cl.duoc.sut_backend.models.SolicitudCertificado
import cl.duoc.sut_backend.repositories.SolicitudCertificadoRepository
import cl.duoc.sut_backend.repositories.UsuarioRepository
import cl.duoc.sut_backend.services.PdfService
//para transferencia de archivos
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/solicitudes")
class SolicitudController(
    private val pdfService: PdfService,
    private val solicitudRepository: SolicitudCertificadoRepository,
    private val usuarioRepository: UsuarioRepository
) {

    @PostMapping
    fun crearSolicitud(@RequestBody request: CrearSolicitudRequest): ResponseEntity<String> {

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

    private val RUTA_ALMACENAMIENTO = "/app/certificados/"

    @PutMapping("/{id}/responder")
    fun responderSolicitud(
        @PathVariable id: Long,
        @RequestBody request: ResponderSolicitudRequest
    ): ResponseEntity<String> {

        val solicitud = solicitudRepository.findById(id)
            .orElseThrow { RuntimeException("Solicitud no encontrada") }

        solicitud.estado = request.estado
        solicitud.comentarioAdmin = request.comentarioAdmin

        if (request.estado == EstadoSolicitud.APROBADA) {
            try {
                // 1. Generar los bytes del PDF (usando tu PdfService existente)
                val pdfBytes = pdfService.generarCertificadoResidencia(solicitud.solicitante)

                // 2. Crear el directorio si no existe (por seguridad)
                val directorio = File(RUTA_ALMACENAMIENTO)
                if (!directorio.exists()) {
                    directorio.mkdirs()
                }

                // 3. Definir el nombre del archivo físico
                val nombreArchivo = "certificado_${id}_${System.currentTimeMillis()}.pdf"
                val rutaCompleta = Paths.get(RUTA_ALMACENAMIENTO + nombreArchivo)

                // 4. ESCRIBIR EL ARCHIVO FÍSICAMENTE EN EL DISCO
                Files.write(rutaCompleta, pdfBytes)

                // 5. Guardar SOLO el nombre del archivo en la base de datos
                solicitud.rutaCertificado = nombreArchivo

            } catch (e: Exception) {
                e.printStackTrace()
                return ResponseEntity.internalServerError().body("Error al guardar el PDF físico")
            }
        } else {
            solicitud.rutaCertificado = null
        }

        solicitudRepository.save(solicitud)
        return ResponseEntity.ok("Solicitud actualizada y certificado generado.")
    }

    @GetMapping("/{id}/descargar")
    fun descargarCertificado(@PathVariable id: Long): ResponseEntity<ByteArray> {
        val solicitud = solicitudRepository.findById(id).orElseThrow {
            RuntimeException("Solicitud no encontrada")
        }

        if (solicitud.estado != EstadoSolicitud.APROBADA || solicitud.rutaCertificado == null) {
            return ResponseEntity.status(403).build()
        }

        try {
            // 1. Construir la ruta al archivo físico
            val nombreArchivo = solicitud.rutaCertificado!!
            val rutaArchivo = Paths.get(RUTA_ALMACENAMIENTO + nombreArchivo)
            val archivo = rutaArchivo.toFile()

            // 2. Verificar que el archivo realmente existe en el disco
            if (!archivo.exists()) {
                return ResponseEntity.notFound().build()
            }

            // 3. Leer los bytes del archivo
            val fileBytes = Files.readAllBytes(rutaArchivo)

            // 4. Enviar al usuario
            val headers = HttpHeaders()
            headers.contentType = MediaType.APPLICATION_PDF
            headers.setContentDispositionFormData("attachment", nombreArchivo)

            return ResponseEntity.ok()
                .headers(headers)
                .contentLength(fileBytes.size.toLong())
                .body(fileBytes)

        } catch (e: Exception) {
            e.printStackTrace()
            return ResponseEntity.internalServerError().build()
        }
    }

}