/**package cl.duoc.sut_backend.controllers

import cl.duoc.sut_backend.dtos.AvisoResponse
import cl.duoc.sut_backend.dtos.CrearAvisoRequest
import cl.duoc.sut_backend.models.Aviso
import cl.duoc.sut_backend.models.Rol
import cl.duoc.sut_backend.repositories.AvisoRepository
import cl.duoc.sut_backend.repositories.UsuarioRepository
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.nio.file.Files
import java.nio.file.Paths

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
            val imagenUrl = aviso.urlImagen
            if (imagenUrl != null) {
                try {
                    // La URL viene como "/api/uploads/nombre.jpg". Extraemos el nombre.
                    val nombreArchivo = imagenUrl.substringAfterLast("/")
                    val rutaArchivo = Paths.get("/app/uploads").resolve(nombreArchivo)
                    Files.deleteIfExists(rutaArchivo)
                } catch (e: Exception) {
                    println("No se pudo borrar la imagen física: ${e.message}")
                }
            }
            avisoRepository.delete(aviso)
            return ResponseEntity.ok("Aviso eliminado")
        } else {
            return ResponseEntity.status(403).body("No tienes permiso para borrar este aviso")
        }
    }
}**/
package cl.duoc.sut_backend.controllers

import cl.duoc.sut_backend.dtos.AvisoResponse
//import cl.duoc.sut_backend.dtos.CrearAvisoRequest
import cl.duoc.sut_backend.models.Aviso
import org.springframework.http.MediaType
import org.springframework.web.multipart.MultipartFile
import cl.duoc.sut_backend.models.Rol
import cl.duoc.sut_backend.models.TipoAviso
import cl.duoc.sut_backend.repositories.AvisoRepository
import cl.duoc.sut_backend.repositories.UsuarioRepository
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

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

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun publicarAviso(
        @RequestParam("titulo") titulo: String,
        @RequestParam("descripcion") descripcion: String,
        @RequestParam("precio", required = false) precio: Int?, // Puede ser nulo
        @RequestParam("tipo") tipo: TipoAviso, // Spring convierte el String "VENTA" al Enum automáticamente
        @RequestParam("imagen", required = false) imagen: MultipartFile? // Aquí llega el archivo real
    ): ResponseEntity<String> {

        // 1. Obtener el usuario logueado
        val email = SecurityContextHolder.getContext().authentication!!.name
        val vecino = usuarioRepository.findByEmail(email).orElseThrow()

        // 2. Lógica para guardar la imagen (Si existe)
        var rutaImagenFinal: String? = null

        if (imagen != null && !imagen.isEmpty) {
            try {
                // Definir dónde guardar (puedes ajustar esta ruta según tu servidor)
                // IMPORTANTE: Asegúrate de que esta carpeta exista o que tengas permisos
                val directorioUploads = Paths.get("/app/uploads")
                if (!Files.exists(directorioUploads)) {
                    Files.createDirectories(directorioUploads)
                }

                // Generar nombre único para evitar colisiones (ej: imagen_uuid.jpg)
                val nombreArchivo = "${System.currentTimeMillis()}_${imagen.originalFilename}"
                val rutaCompleta = directorioUploads.resolve(nombreArchivo)

                // Guardar el archivo físicamente en el disco
                Files.copy(imagen.inputStream, rutaCompleta, StandardCopyOption.REPLACE_EXISTING)

                // Guardar la URL relativa para la base de datos (ej: /uploads/foto123.jpg)
                // El backend debe estar configurado para servir esta carpeta como recurso estático
                rutaImagenFinal = "/api/uploads/$nombreArchivo"

            } catch (e: Exception) {
                e.printStackTrace()
                return ResponseEntity.internalServerError().body("Error al subir la imagen")
            }
        }

        // 3. Crear la entidad Aviso
        val nuevoAviso = Aviso(
            titulo = titulo,
            descripcion = descripcion,
            precio = precio,
            tipo = tipo,
            urlImagen = rutaImagenFinal, // Aquí va la ruta del archivo guardado o null
            publicador = vecino
        )

        // 4. Guardar en BD
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
            val imagenUrl = aviso.urlImagen
            if (imagenUrl != null) {
                try {
                    // La URL viene como "/api/uploads/nombre.jpg". Extraemos el nombre.
                    val nombreArchivo = imagenUrl.substringAfterLast("/")
                    val rutaArchivo = Paths.get("/app/uploads").resolve(nombreArchivo)
                    Files.deleteIfExists(rutaArchivo)
                } catch (e: Exception) {
                    println("No se pudo borrar la imagen física: ${e.message}")
                }
            }
            avisoRepository.delete(aviso)
            return ResponseEntity.ok("Aviso eliminado")
        } else {
            return ResponseEntity.status(403).body("No tienes permiso para borrar este aviso")
        }
    }
}